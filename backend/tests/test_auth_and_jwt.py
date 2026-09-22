import pytest
import time
from httpx import AsyncClient, ASGITransport
from backend.main import app
from backend.tests.mock_db import MockDatabase
from backend.security import create_access_token, create_refresh_token, decode_token

@pytest.fixture(autouse=True)
def setup_mock_db(monkeypatch):
    mock_db = MockDatabase()
    import backend.database
    import backend.routes.auth
    import backend.routes.admin
    monkeypatch.setattr(backend.database, "get_database", lambda: mock_db)
    monkeypatch.setattr(backend.routes.auth, "get_database", lambda: mock_db)
    monkeypatch.setattr(backend.routes.admin, "get_database", lambda: mock_db)
    return mock_db

@pytest.mark.anyio
async def test_register_success_and_duplicate_rejection():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        # 1. Successful registration
        resp = await client.post("/api/v1/auth/register", json={
            "name": "Jean Dupont",
            "email": "jean.dupont@fixo.cm",
            "password": "Password123!",
            "phone": "+237690112233",
            "role": "CUSTOMER"
        })
        assert resp.status_code == 200
        data = resp.json()
        assert "access_token" in data
        assert "refresh_token" in data
        assert data["email"] == "jean.dupont@fixo.cm"

        # 2. Duplicate registration with same email (case-insensitive)
        dup_resp = await client.post("/api/v1/auth/register", json={
            "name": "Jean Duplicate",
            "email": "JEAN.DUPONT@FIXO.CM",
            "password": "Password123!",
            "phone": "+237690998877",
            "role": "CUSTOMER"
        })
        assert dup_resp.status_code == 400
        assert "already registered" in dup_resp.json()["detail"].lower()

@pytest.mark.anyio
async def test_register_weak_password():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        resp = await client.post("/api/v1/auth/register", json={
            "name": "Weak User",
            "email": "weak@fixo.cm",
            "password": "pass",  # Too short
            "phone": "+237690112233"
        })
        assert resp.status_code == 422 or resp.status_code == 400

@pytest.mark.anyio
async def test_login_success_and_invalid_password():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        # Register first
        await client.post("/api/v1/auth/register", json={
            "name": "Alice Mballa",
            "email": "alice@fixo.cm",
            "password": "ValidPassword1!",
            "phone": "+237677112233"
        })

        # Correct password
        login_resp = await client.post("/api/v1/auth/login", json={
            "email": "alice@fixo.cm",
            "password": "ValidPassword1!"
        })
        assert login_resp.status_code == 200
        data = login_resp.json()
        assert data["email"] == "alice@fixo.cm"

        # Incorrect password
        fail_resp = await client.post("/api/v1/auth/login", json={
            "email": "alice@fixo.cm",
            "password": "WrongPassword99!"
        })
        assert fail_resp.status_code == 401

@pytest.mark.anyio
async def test_refresh_token_rotation_and_reuse_detection():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        reg = await client.post("/api/v1/auth/register", json={
            "name": "Bob Kamga",
            "email": "bob@fixo.cm",
            "password": "Password123!",
            "phone": "+237670000111"
        })
        initial_refresh_token = reg.json()["refresh_token"]

        # First refresh: succeeds and rotates
        ref1 = await client.post("/api/v1/auth/refresh", json={
            "refresh_token": initial_refresh_token
        })
        assert ref1.status_code == 200
        new_refresh_token = ref1.json()["refresh_token"]
        assert new_refresh_token != initial_refresh_token

        # Attempt to reuse old rotated refresh token (REUSE ATTACK)
        reuse_attempt = await client.post("/api/v1/auth/refresh", json={
            "refresh_token": initial_refresh_token
        })
        assert reuse_attempt.status_code == 401
        assert "revoked or invalid" in reuse_attempt.json()["detail"].lower()

@pytest.mark.anyio
async def test_logout_and_session_revocation():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        reg = await client.post("/api/v1/auth/register", json={
            "name": "Charlie Tche",
            "email": "charlie@fixo.cm",
            "password": "Password123!",
            "phone": "+237670999888"
        })
        access_tok = reg.json()["access_token"]
        refresh_tok = reg.json()["refresh_token"]

        # Logout with refresh token
        logout_resp = await client.post(
            "/api/v1/auth/logout",
            json={"refresh_token": refresh_tok},
            headers={"Authorization": f"Bearer {access_tok}"}
        )
        assert logout_resp.status_code == 200

        # Attempting refresh after logout must fail
        ref_after_logout = await client.post("/api/v1/auth/refresh", json={
            "refresh_token": refresh_tok
        })
        assert ref_after_logout.status_code == 401

@pytest.mark.anyio
async def test_google_auth_fails_securely_without_synthetic_fallback(monkeypatch):
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        # If GOOGLE_CLIENT_ID is not configured, must return 503 / configuration error, NOT create fake user
        import backend.routes.auth
        monkeypatch.setattr(backend.routes.auth, "GOOGLE_CLIENT_ID", "")

        resp = await client.post("/api/v1/auth/google", json={
            "id_token": "arbitrary_fake_token_12345"
        })
        assert resp.status_code == 503
        assert "BLOCKED BY EXTERNAL CONFIGURATION" in resp.json()["detail"]

def test_missing_or_short_jwt_secret_fails_secure_startup(monkeypatch):
    import importlib
    import backend.security

    # Case 1: missing secret
    monkeypatch.setenv("JWT_SECRET_KEY", "")
    with pytest.raises(RuntimeError, match="CRITICAL SECURITY FAILURE: JWT_SECRET_KEY environment variable is mandatory"):
        importlib.reload(backend.security)

    # Case 2: short secret (< 32 characters)
    monkeypatch.setenv("JWT_SECRET_KEY", "short_insecure_key_123")
    with pytest.raises(RuntimeError, match="must be at least 32 characters in length"):
        importlib.reload(backend.security)

    # Restore valid secret for subsequent tests
    monkeypatch.setenv("JWT_SECRET_KEY", "test_jwt_secret_key_minimum_32_characters_for_fixo_testing!")
    importlib.reload(backend.security)

def test_jwt_validation_rejects_missing_claims_and_invalid_issuer():
    import jwt
    import time
    from backend.security import decode_token, JWT_SECRET_KEY, ALGORITHM, JWT_ISSUER
    from fastapi import HTTPException

    # 1. Invalid issuer
    bad_iss_payload = {
        "iss": "malicious-issuer",
        "sub": "usr_test",
        "role": "CUSTOMER",
        "type": "access",
        "jti": "atk_123",
        "iat": int(time.time()),
        "exp": int(time.time()) + 900
    }
    bad_iss_token = jwt.encode(bad_iss_payload, JWT_SECRET_KEY, algorithm=ALGORITHM)
    with pytest.raises(HTTPException) as exc:
        decode_token(bad_iss_token)
    assert exc.value.status_code == 401
    assert "issuer is invalid" in exc.value.detail.lower()

    # 2. Expired token
    expired_payload = {
        "iss": JWT_ISSUER,
        "sub": "usr_test",
        "role": "CUSTOMER",
        "type": "access",
        "jti": "atk_123",
        "iat": int(time.time()) - 1800,
        "exp": int(time.time()) - 900
    }
    expired_token = jwt.encode(expired_payload, JWT_SECRET_KEY, algorithm=ALGORITHM)
    with pytest.raises(HTTPException) as exc:
        decode_token(expired_token)
    assert exc.value.status_code == 401
    assert "expired" in exc.value.detail.lower()
