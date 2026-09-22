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
    import backend.routes.wallet
    import backend.routes.notifications
    import backend.routes.workers

    for module in [
        backend.database, backend.routes.auth, backend.routes.bookings,
        backend.routes.chat, backend.routes.media, backend.routes.admin,
        backend.routes.enterprise, backend.routes.wallet,
        backend.routes.notifications, backend.routes.workers
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

@pytest.mark.anyio
async def test_booking_cancel_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        booking_id = "bk_victim_101"
        await db.bookings.insert_one({
            "id": booking_id,
            "customer_id": "usr_victim",
            "worker_id": "wrk_legit",
            "status": "ACCEPTED",
            "price_amount_xaf": 20000.0,
            "escrow_status": "HOLDING",
            "payment_method": "FIXO_WALLET"
        })

        tok_attacker = create_access_token("usr_attacker", "CUSTOMER")

        # Attacker attempts to cancel Victim's booking -> 403 Forbidden
        resp = await client.post(
            f"/api/v1/bookings/{booking_id}/cancel",
            json={"reason": "Malicious cancellation attempt"},
            headers={"Authorization": f"Bearer {tok_attacker}"}
        )
        assert resp.status_code == 403
        assert "Unauthorized" in resp.json()["detail"]

@pytest.mark.anyio
async def test_chat_message_sending_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        conv_id = "cnv_confidential_42"
        await db.conversations.insert_one({
            "id": conv_id,
            "customer_id": "usr_alice",
            "worker_id": "wrk_bob",
            "customer_name": "Alice",
            "worker_name": "Bob"
        })

        tok_mallory = create_access_token("usr_mallory", "CUSTOMER")

        # Mallory (outsider) attempts to inject a message into Alice's conversation -> 403 Forbidden
        resp = await client.post(
            f"/api/v1/chat/conversations/{conv_id}/messages",
            json={"text": "Impersonated attack message"},
            headers={"Authorization": f"Bearer {tok_mallory}"}
        )
        assert resp.status_code == 403
        assert "Unauthorized" in resp.json()["detail"]

@pytest.mark.anyio
async def test_wallet_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        # User A's transaction
        await db.transactions.insert_one({
            "id": "txn_alice_1",
            "user_id": "usr_alice_wallet",
            "type": "DEPOSIT",
            "amount_xaf": 50000.0,
            "currency": "XAF",
            "description": "Private salary deposit",
            "status": "COMPLETED",
            "payment_provider": "MTN_MOMO",
            "reference_code": "DEP-ALICE-1",
            "created_at": 1000
        })

        tok_alice = create_access_token("usr_alice_wallet", "CUSTOMER")
        tok_bob = create_access_token("usr_bob_wallet", "CUSTOMER")

        # Alice sees her transaction
        resp_alice = await client.get("/api/v1/wallet/transactions", headers={"Authorization": f"Bearer {tok_alice}"})
        assert resp_alice.status_code == 200
        txns_alice = resp_alice.json()
        assert len(txns_alice) == 1
        assert txns_alice[0]["id"] == "txn_alice_1"

        # Bob queries his transactions -> Alice's transaction is NEVER visible to Bob
        resp_bob = await client.get("/api/v1/wallet/transactions", headers={"Authorization": f"Bearer {tok_bob}"})
        assert resp_bob.status_code == 200
        txns_bob = resp_bob.json()
        assert len(txns_bob) == 0

@pytest.mark.anyio
async def test_notification_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        notif_id = "notif_alice_priv"
        await db.notifications.insert_one({
            "id": notif_id,
            "user_id": "usr_alice",
            "title": "Confidential security alert",
            "message": "Password updated",
            "is_read": False,
            "created_at": 1000
        })

        tok_bob = create_access_token("usr_bob", "CUSTOMER")

        # Bob attempts to mark Alice's notification as read -> 404 (isolation prevents access)
        resp = await client.put(f"/api/v1/notifications/{notif_id}/read", headers={"Authorization": f"Bearer {tok_bob}"})
        assert resp.status_code == 404

        # Verify notification remains unread
        doc = await db.notifications.find_one({"id": notif_id})
        assert doc["is_read"] is False

@pytest.mark.anyio
async def test_media_bola_and_tampering_isolation(setup_mock_db, tmp_path, monkeypatch):
    import os
    import backend.routes.media
    db = setup_mock_db
    monkeypatch.setattr(backend.routes.media, "MEDIA_DIR", str(tmp_path))

    # Create dummy private document
    file_id = "fixo_media_identity_cni.pdf"
    file_path = os.path.join(str(tmp_path), file_id)
    with open(file_path, "wb") as f:
        f.write(b"%PDF-1.4 confidential identity CNI card")

    await db.media_files.insert_one({
        "file_id": file_id,
        "owner_id": "usr_victim_owner",
        "is_private": True,
        "purpose": "CNI",
        "booking_id": None,
        "content_type": "application/pdf"
    })

    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        tok_owner = create_access_token("usr_victim_owner", "WORKER")
        tok_attacker = create_access_token("usr_attacker", "CUSTOMER")

        # 1. Attacker attempts to read private identity media -> 403 Forbidden
        resp_read = await client.get(f"/api/v1/media/secure/{file_id}", headers={"Authorization": f"Bearer {tok_attacker}"})
        assert resp_read.status_code == 403

        # 2. Attacker attempts to delete Victim's media asset -> 403 Forbidden
        resp_del = await client.delete(f"/api/v1/media/{file_id}", headers={"Authorization": f"Bearer {tok_attacker}"})
        assert resp_del.status_code == 403

        # 3. Legitimate Owner can access
        resp_legit = await client.get(f"/api/v1/media/secure/{file_id}", headers={"Authorization": f"Bearer {tok_owner}"})
        assert resp_legit.status_code == 200

@pytest.mark.anyio
async def test_worker_resources_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        await db.workers.insert_one({
            "id": "wrk_artisan_real",
            "user_id": "usr_artisan_owner",
            "name": "Artisan Pro",
            "working_days": ["MON", "TUE"],
            "slot_intervals": ["08:00 - 12:00"],
            "emergency_callout_available": False
        })

        tok_stranger = create_access_token("usr_stranger", "CUSTOMER")

        # Stranger attempts to modify Artisan's availability -> 404 (cannot modify another user's worker profile)
        resp = await client.put(
            "/api/v1/workers/profile/availability",
            json={
                "working_days": ["SUN"],
                "slot_intervals": ["00:00 - 04:00"],
                "emergency_available": True
            },
            headers={"Authorization": f"Bearer {tok_stranger}"}
        )
        assert resp.status_code == 404

        # Confirm Artisan's profile was not altered
        artisan_doc = await db.workers.find_one({"id": "wrk_artisan_real"})
        assert artisan_doc["emergency_callout_available"] is False

@pytest.mark.anyio
async def test_organization_membership_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        org_id = "org_private_enterprise"
        await db.organizations.insert_one({
            "id": org_id,
            "owner_id": "usr_enterprise_ceo",
            "name": "Cameroon Infrastructure Ltd"
        })

        tok_outsider = create_access_token("usr_unauthorized_outsider", "CUSTOMER")

        # Outsider attempts to list organization members -> 403 Forbidden
        resp_list = await client.get(
            f"/api/v1/enterprise/organizations/{org_id}/members",
            headers={"Authorization": f"Bearer {tok_outsider}"}
        )
        assert resp_list.status_code == 403

        # Outsider attempts to add a member to the organization -> 403 Forbidden
        resp_add = await client.post(
            f"/api/v1/enterprise/organizations/{org_id}/members",
            json={"user_email": "target@fixo.cm", "role": "MEMBER"},
            headers={"Authorization": f"Bearer {tok_outsider}"}
        )
        assert resp_add.status_code == 403

@pytest.mark.anyio
async def test_dispute_filing_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        booking_id = "bk_order_55"
        await db.bookings.insert_one({
            "id": booking_id,
            "customer_id": "usr_client_alice",
            "worker_id": "wrk_electrician_bob",
            "status": "IN_PROGRESS",
            "price_amount_xaf": 12000.0,
            "escrow_status": "HOLDING"
        })

        tok_unrelated_third_party = create_access_token("usr_random_person", "CUSTOMER")

        # Third party tries to hijack and dispute Alice and Bob's booking -> 403 Forbidden
        resp = await client.post(
            f"/api/v1/bookings/{booking_id}/dispute",
            json={
                "reason": "POOR_QUALITY",
                "description": "Unauthorized dispute attack",
                "evidence_urls": []
            },
            headers={"Authorization": f"Bearer {tok_unrelated_third_party}"}
        )
        assert resp.status_code == 403
        assert "Unauthorized to file dispute" in resp.json()["detail"]

@pytest.mark.anyio
async def test_worker_location_tracking_bola_isolation(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        booking_id = "bk_live_route_77"
        await db.bookings.insert_one({
            "id": booking_id,
            "customer_id": "usr_customer_carol",
            "worker_id": "wrk_driver_dan",
            "status": "EN_ROUTE"
        })
        await db.worker_locations.insert_one({
            "booking_id": booking_id,
            "worker_id": "wrk_driver_dan",
            "latitude": 4.051056,
            "longitude": 9.767869,
            "is_active": True
        })

        tok_stalker = create_access_token("usr_stalker", "CUSTOMER")

        # Stalker attempts to track the worker's live location -> 403 Forbidden
        resp = await client.get(
            f"/api/v1/bookings/{booking_id}/location",
            headers={"Authorization": f"Bearer {tok_stalker}"}
        )
        assert resp.status_code == 403
        assert "Unauthorized to track worker" in resp.json()["detail"]

@pytest.mark.anyio
async def test_admin_disputes_and_actions_negative_authorization(setup_mock_db):
    db = setup_mock_db
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as client:
        dispute_id = "dsp_high_value_999"
        await db.disputes.insert_one({
            "id": dispute_id,
            "booking_id": "bk_target_1",
            "status": "OPEN",
            "created_at": 1000
        })

        tok_regular_user = create_access_token("usr_regular_customer", "CUSTOMER")

        # 1. Non-admin attempting to list all platform disputes -> 403 Forbidden
        resp_list = await client.get("/api/v1/admin/disputes", headers={"Authorization": f"Bearer {tok_regular_user}"})
        assert resp_list.status_code == 403

        # 2. Non-admin attempting to adjudicate/resolve dispute -> 403 Forbidden
        resp_resolve = await client.post(
            f"/api/v1/admin/disputes/{dispute_id}/resolve",
            json={
                "resolution": "RESOLVED_REFUND_CUSTOMER",
                "admin_notes": "Attempting illegal refund override"
            },
            headers={"Authorization": f"Bearer {tok_regular_user}"}
        )
        assert resp_resolve.status_code == 403
