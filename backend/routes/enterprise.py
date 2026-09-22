import uuid
import time
from fastapi import APIRouter, HTTPException, Depends
from typing import List
from backend.database import get_database
from backend.models import EnterpriseProjectModel
from backend.security import get_current_user_claims

router = APIRouter(prefix="/enterprise", tags=["Enterprise"])

@router.get("/projects", response_model=List[EnterpriseProjectModel])
async def list_enterprise_projects(claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]
    cursor = db.enterprise_projects.find({}, {"_id": 0}).sort("created_at", -1)
    projects = await cursor.to_list(length=50)
    return projects

@router.post("/projects", response_model=EnterpriseProjectModel)
async def create_enterprise_project(
    title: str,
    description: str,
    location: str,
    budget_xaf: float,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    user = await db.users.find_one({"id": claims["sub"]})
    org_name = user["name"] if user else "Enterprise Partner"

    proj_id = f"ep_{uuid.uuid4().hex[:12]}"
    proj_doc = {
        "id": proj_id,
        "org_id": claims["sub"],
        "org_name": org_name,
        "title": title,
        "description": description,
        "location": location,
        "budget_xaf": budget_xaf,
        "status": "ACTIVE",
        "created_at": int(time.time() * 1000)
    }
    await db.enterprise_projects.insert_one(proj_doc)
    return EnterpriseProjectModel(**proj_doc)
