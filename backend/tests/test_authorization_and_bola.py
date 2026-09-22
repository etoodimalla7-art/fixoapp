import pytest
from httpx import AsyncClient, ASGITransport
from backend.main import app
from backend.tests.mock_db import MockDatabase
from backend.security import create_access_token

@pytest.fixture(autouse=True)
def setup_mock_db(monkeypatch):
    mock_db = MockDatabase()
    import backend.database
    import backend.routes.auth
    import backend.routes.bookings
    import backend.routes.chat
    import backend.routes.media
    import backend.routes.admin
    import backend.routes.enterprise

    for module in [
        backend.database, backend.routes.auth, backend.routes.bookings,
        backend.routes.chat, backend.routes.media, backend.routes.admin,
        backend.routes.enterprise
    ]:
        monkeypatch.setattr(module, "get_database", lambda: mock_db)
    return mock_db

@pytest.mark.anyio
async def test_admin_authorization_enforced(setup_mock_db):
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        customer_tok = create_access_token("usr_cust_123", "CUSTOMER")
        worker_tok = create_access_token("usr_wrk_123", "WORKER")
        admin_tok = create_access_token("usr_adm_123", "ADMIN")

        # 1. Customer attempting admin endpoint -> 403
        resp1 = await client.get("/api/v1/admin/stats", headers={"Authorization": f"Bearer {customer_tok}"})
        assert resp1.status_code == 403

        # 2. Worker attempting admin endpoint -> 403
        resp2 = await client.get("/api/v1/admin/stats", headers={"Authorization": f"Bearer {worker_tok}"})
        assert resp2.status_code == 403

        # 3. Admin attempting admin endpoint -> 200
        resp3 = await client.get("/api/v1/admin/stats", headers={"Authorization": f"Bearer {admin_tok}"})
        assert resp3.status_code == 200

@pytest.mark.anyio
async def test_admin_user_suspension_and_revocation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        admin_tok = create_access_token("usr_admin_master", "ADMIN")

        # Create target user and active session
        target_uid = "usr_bad_actor_99"
        await db.users.insert_one({"id": target_uid, "name": "Bad Actor", "email": "bad@fixo.cm"})
        await db.sessions.insert_one({
            "id": "sess_bad_1",
            "user_id": target_uid,
            "token_hash": "hash123",
            "is_revoked": False,
            "created_at": 1000
        })

        # Admin suspends user
        resp = await client.post(
            f"/api/v1/admin/users/{target_uid}/suspend",
            json={"reason": "Fraudulent payment activity"},
            headers={"Authorization": f"Bearer {admin_tok}"}
        )
        assert resp.status_code == 200
        assert "suspended and 1 session(s) revoked" in resp.json()["message"]

        # Check DB state
        user_doc = await db.users.find_one({"id": target_uid})
        assert user_doc["is_suspended"] is True
        sess_doc = await db.sessions.find_one({"id": "sess_bad_1"})
        assert sess_doc["is_revoked"] is True

@pytest.mark.anyio
async def test_booking_bola_idor_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        booking_id = "bk_test_1001"
        await db.bookings.insert_one({
            "id": booking_id,
            "customer_id": "usr_customer_A",
            "worker_id": "wrk_plumber_1",
            "customer_name": "Customer A",
            "worker_name": "Plumber 1",
            "service_title": "Pipe Repair",
            "category": "PLUMBING",
            "date": "2026-10-01",
            "time_slot": "09:00 - 11:00",
            "status": "ACCEPTED",
            "address": "Akwa, Douala",
            "notes": "",
            "price_amount_xaf": 15000.0,
            "escrow_status": "HOLDING",
            "payment_method": "FIXO_WALLET",
            "created_at": 1000
        })

        tok_customer_a = create_access_token("usr_customer_A", "CUSTOMER")
        tok_customer_b = create_access_token("usr_customer_B", "CUSTOMER")

        # Customer A can view their own booking
        resp_a = await client.get(f"/api/v1/bookings/{booking_id}", headers={"Authorization": f"Bearer {tok_customer_a}"})
        assert resp_a.status_code == 200
        assert resp_a.json()["id"] == booking_id

        # Customer B attempts to view Customer A's booking -> BOLA 403
        resp_b = await client.get(f"/api/v1/bookings/{booking_id}", headers={"Authorization": f"Bearer {tok_customer_b}"})
        assert resp_b.status_code == 403
        assert "Unauthorized" in resp_b.json()["detail"]

@pytest.mark.anyio
async def test_chat_bola_idor_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        conv_id = "cnv_private_99"
        await db.conversations.insert_one({
            "id": conv_id,
            "customer_id": "usr_alice",
            "worker_id": "wrk_bob",
            "customer_name": "Alice",
            "worker_name": "Bob",
            "last_message": "Hello",
            "last_message_time": 1000,
            "created_at": 1000
        })

        tok_alice = create_access_token("usr_alice", "CUSTOMER")
        tok_eve = create_access_token("usr_eve", "CUSTOMER")

        # Alice (authorized participant) can view messages
        resp_alice = await client.get(f"/api/v1/chat/conversations/{conv_id}/messages", headers={"Authorization": f"Bearer {tok_alice}"})
        assert resp_alice.status_code == 200

        # Eve (unauthorized snooper) -> 403
        resp_eve = await client.get(f"/api/v1/chat/conversations/{conv_id}/messages", headers={"Authorization": f"Bearer {tok_eve}"})
        assert resp_eve.status_code == 403

@pytest.mark.anyio
async def test_enterprise_tenant_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        proj_a = "ep_orga_001"
        await db.enterprise_projects.insert_one({
            "id": proj_a,
            "org_id": "org_alpha_corporation",
            "org_name": "Alpha Corp",
            "title": "Hospital HVAC Installation",
            "description": "Commercial contract",
            "location": "Yaounde",
            "budget_xaf": 5000000.0,
            "status": "ACTIVE",
            "created_at": 1000
        })

        tok_user_org_b = create_access_token("usr_beta_member", "ENTERPRISE")

        # Member of Org B attempts to access Org A project -> 403
        resp = await client.get(f"/api/v1/enterprise/projects/{proj_a}", headers={"Authorization": f"Bearer {tok_user_org_b}"})
        assert resp.status_code == 403
        assert "Access denied" in resp.json()["detail"]
