import os
os.environ["ENVIRONMENT"] = "test"
os.environ["JWT_SECRET_KEY"] = "FixoProductionMockSecretKeyForAutomatedTests2026!MustBeVeryLong"

import pytest
import backend.main
import backend.routes.auth

@pytest.fixture(autouse=True)
def reset_rate_limits():
    backend.main.RATE_LIMIT_BUCKET.clear()
    backend.routes.auth.ENVIRONMENT = "test"
    yield
    backend.main.RATE_LIMIT_BUCKET.clear()
