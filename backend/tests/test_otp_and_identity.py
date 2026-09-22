import pytest
from httpx import AsyncClient, ASGITransport
from backend.main import app
from backend.tests.mock_db import MockDatabase

@pytest.fixture(autouse=True)
def setup_mock_db(monkeypatch):
    mock_db = MockDatabase()
    import backend.database
    import backend.routes.auth
    monkeypatch.setattr(backend.database, "get_database", lambda: mock_db)
    monkeypatch.setattr(backend.routes.auth, "get_database", lambda: mock_db)
    return mock_db

@pytest.mark.anyio
async def test_otp_lifecycle_cooldown_and_verification():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        phone = "+237699112233"

        # 1. Request OTP
        req_resp = await client.post("/api/v1/auth/otp/request", json={
            "phone": phone,
            "purpose": "PHONE_VERIFICATION"
        })
        assert req_resp.status_code == 200
        code = req_resp.json().get("debug_code")
        assert code is not None
        assert len(code) == 6

        # 2. Resend Cooldown within 60s -> 429 Too Many Requests
        cooldown_resp = await client.post("/api/v1/auth/otp/request", json={
            "phone": phone,
            "purpose": "PHONE_VERIFICATION"
        })
        assert cooldown_resp.status_code == 429

        # 3. Incorrect OTP verification fails
        bad_verify = await client.post("/api/v1/auth/otp/verify", json={
            "phone": phone,
            "otp": "000000",
            "purpose": "PHONE_VERIFICATION"
        })
        assert bad_verify.status_code == 400
        assert "attempt(s) remaining" in bad_verify.json()["detail"]

        # 4. Correct OTP verifies successfully
        good_verify = await client.post("/api/v1/auth/otp/verify", json={
            "phone": phone,
            "otp": code,
            "purpose": "PHONE_VERIFICATION"
        })
        assert good_verify.status_code == 200

        # 5. Replay attempt on consumed OTP fails
        reuse_verify = await client.post("/api/v1/auth/otp/verify", json={
            "phone": phone,
            "otp": code,
            "purpose": "PHONE_VERIFICATION"
        })
        assert reuse_verify.status_code == 400

@pytest.mark.anyio
async def test_phone_number_change_flow():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        reg = await client.post("/api/v1/auth/register", json={
            "name": "David Ndongo",
            "email": "david@fixo.cm",
            "password": "Password123!",
            "phone": "+237699000111"
        })
        tok = reg.json()["access_token"]
        auth_header = {"Authorization": f"Bearer {tok}"}

        new_phone = "+237655443322"

        # Request phone change
        change_req = await client.post(
            "/api/v1/auth/phone/change-request",
            json={"new_phone": new_phone},
            headers=auth_header
        )
        assert change_req.status_code == 200
        otp = change_req.json().get("debug_code")
        assert otp is not None

        # Verify phone change
        verify_req = await client.post(
            "/api/v1/auth/phone/change-verify",
            json={"new_phone": new_phone, "otp": otp},
            headers=auth_header
        )
        assert verify_req.status_code == 200

        # Check me: phone is now updated
        me_resp = await client.get("/api/v1/auth/me", headers=auth_header)
        assert me_resp.status_code == 200
        assert me_resp.json()["phone"] == new_phone
        assert me_resp.json()["current_verified_phone"] == new_phone

@pytest.mark.anyio
async def test_username_uniqueness_and_reserved_rejection():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        # Register user 1 with username 'fixopro'
        reg1 = await client.post("/api/v1/auth/register", json={
            "name": "User One",
            "email": "user1@fixo.cm",
            "password": "Password123!",
            "phone": "+237699000112",
            "username": "fixopro"
        })
        assert reg1.status_code == 200

        # Register user 2 attempting case-variant 'FixoPro' -> must be rejected
        reg2 = await client.post("/api/v1/auth/register", json={
            "name": "User Two",
            "email": "user2@fixo.cm",
            "password": "Password123!",
            "phone": "+237699000113",
            "username": "FixoPro"
        })
        assert reg2.status_code == 400
        assert "already taken" in reg2.json()["detail"].lower()

        # Register user attempting reserved username 'admin'
        reg_admin = await client.post("/api/v1/auth/register", json={
            "name": "Fake Admin",
            "email": "fakeadmin@fixo.cm",
            "password": "Password123!",
            "phone": "+237699000114",
            "username": "admin"
        })
        assert reg_admin.status_code == 400
        assert "reserved" in reg_admin.json()["detail"].lower()

@pytest.mark.anyio
async def test_password_reset_flow():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        # Register user
        await client.post("/api/v1/auth/register", json={
            "name": "Emmanuel Eto",
            "email": "emmanuel@fixo.cm",
            "password": "OldPassword1!",
            "phone": "+237699000115"
        })

        # Request reset
        reset_req = await client.post("/api/v1/auth/password/reset-request", json={
            "email": "emmanuel@fixo.cm"
        })
        assert reset_req.status_code == 200
        reset_token = reset_req.json().get("debug_token")
        assert reset_token is not None

        # Confirm reset with new strong password
        confirm_resp = await client.post("/api/v1/auth/password/reset-confirm", json={
            "token": reset_token,
            "new_password": "NewSecurePassword2!"
        })
        assert confirm_resp.status_code == 200

        # Login with old password must fail
        old_login = await client.post("/api/v1/auth/login", json={
            "email": "emmanuel@fixo.cm",
            "password": "OldPassword1!"
        })
        assert old_login.status_code == 401

        # Login with new password must succeed
        new_login = await client.post("/api/v1/auth/login", json={
            "email": "emmanuel@fixo.cm",
            "password": "NewSecurePassword2!"
        })
        assert new_login.status_code == 200

@pytest.mark.anyio
async def test_exceeding_max_otp_attempts_causes_rejection():
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        phone = "+237699887766"

        # 1. Request OTP
        req_resp = await client.post("/api/v1/auth/otp/request", json={
            "phone": phone,
            "purpose": "PHONE_VERIFICATION"
        })
        assert req_resp.status_code == 200
        actual_code = req_resp.json().get("debug_code")
        assert actual_code is not None

        # 2. Attempt 1 with wrong code
        bad_1 = await client.post("/api/v1/auth/otp/verify", json={
            "phone": phone,
            "otp": "111111",
            "purpose": "PHONE_VERIFICATION"
        })
        assert bad_1.status_code == 400
        assert "2 attempt(s) remaining" in bad_1.json()["detail"]

        # 3. Attempt 2 with wrong code
        bad_2 = await client.post("/api/v1/auth/otp/verify", json={
            "phone": phone,
            "otp": "222222",
            "purpose": "PHONE_VERIFICATION"
        })
        assert bad_2.status_code == 400
        assert "1 attempt(s) remaining" in bad_2.json()["detail"]

        # 4. Attempt 3 with wrong code -> exhausts attempts
        bad_3 = await client.post("/api/v1/auth/otp/verify", json={
            "phone": phone,
            "otp": "333333",
            "purpose": "PHONE_VERIFICATION"
        })
        assert bad_3.status_code == 400
        assert "0 attempt(s) remaining" in bad_3.json()["detail"]

        # 5. Attempt 4: Even with the CORRECT code, it must now be rejected because max attempts were exceeded
        exhausted_attempt = await client.post("/api/v1/auth/otp/verify", json={
            "phone": phone,
            "otp": actual_code,
            "purpose": "PHONE_VERIFICATION"
        })
        assert exhausted_attempt.status_code == 400
        assert "Maximum verification attempts exceeded" in exhausted_attempt.json()["detail"]
