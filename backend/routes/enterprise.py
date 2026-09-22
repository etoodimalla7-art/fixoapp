import uuid
import time
from fastapi import APIRouter, HTTPException, Depends, status
from typing import List, Optional
from pydantic import BaseModel, Field
from backend.database import get_database
from backend.models import (
    EnterpriseProjectModel, UserRole, OrganizationMemberRole,
    OrganizationMemberModel, AddOrganizationMemberRequest
)
from backend.security import get_current_user_claims

router = APIRouter(prefix="/enterprise", tags=["Enterprise & Tenant Isolation"])

class CreateProjectRequest(BaseModel):
    title: str = Field(..., min_length=3, max_length=120)
    description: str = Field(..., max_length=1000)
    location: str = Field(..., min_length=2, max_length=100)
    budget_xaf: float = Field(..., gt=0)
    organization_id: Optional[str] = None

async def verify_org_membership(db, org_id: str, user_id: str, allowed_roles: Optional[List[OrganizationMemberRole]] = None) -> dict:
    """
    Authoritatively checks tenant boundary.
    A user is permitted if they are the direct creator/owner of the org,
    or have an active membership record in organization_members with the required role.
    """
    # Direct owner match
    org = await db.organizations.find_one({"id": org_id})
    if org and org.get("owner_id") == user_id:
        return {"role": OrganizationMemberRole.OWNER.value, "organization_id": org_id, "user_id": user_id}

    # Explicit organization_members collection lookup
    membership = await db.organization_members.find_one({
        "organization_id": org_id,
        "user_id": user_id
    })

    if not membership:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Access denied: You are not an authorized member of this organization."
        )

    if allowed_roles:
        allowed_str = [r.value for r in allowed_roles]
        if membership.get("role") not in allowed_str:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Access denied: Requires one of the following permissions: {allowed_str}"
            )

    return membership

@router.get("/projects", response_model=List[EnterpriseProjectModel])
async def list_enterprise_projects(claims: dict = Depends(get_current_user_claims)):
    """
    Lists ONLY enterprise projects belonging to organizations where the caller is a member.
    Enforces strict multi-tenant boundary isolation.
    """
    db = get_database()
    user_id = claims["sub"]

    # If platform admin, allowed global visibility
    if claims.get("role") == UserRole.ADMIN.value:
        cursor = db.enterprise_projects.find({}, {"_id": 0}).sort("created_at", -1)
        return await cursor.to_list(length=100)

    # Discover all orgs where the user is an owner or member
    memberships = await db.organization_members.find({"user_id": user_id}).to_list(length=50)
    user_org_ids = [m["organization_id"] for m in memberships]

    # Also check owned orgs
    owned_orgs = await db.organizations.find({"owner_id": user_id}).to_list(length=50)
    for o in owned_orgs:
        if o["id"] not in user_org_ids:
            user_org_ids.append(o["id"])

    # Include legacy org_id = user_id for backward compatibility
    user_org_ids.append(user_id)

    cursor = db.enterprise_projects.find(
        {"org_id": {"$in": user_org_ids}},
        {"_id": 0}
    ).sort("created_at", -1)

    projects = await cursor.to_list(length=100)
    return projects

@router.get("/projects/{project_id}", response_model=EnterpriseProjectModel)
async def get_enterprise_project(project_id: str, claims: dict = Depends(get_current_user_claims)):
    """
    Object-level authorization check for enterprise project.
    Prevents BOLA/IDOR by ensuring the user belongs to the owning organization.
    """
    db = get_database()
    project = await db.enterprise_projects.find_one({"id": project_id}, {"_id": 0})
    if not project:
        raise HTTPException(status_code=404, detail="Enterprise project not found.")

    if claims.get("role") == UserRole.ADMIN.value:
        return project

    user_id = claims["sub"]
    org_id = project["org_id"]

    # Verify membership or ownership
    if org_id != user_id:
        await verify_org_membership(db, org_id, user_id)

    return project

@router.post("/projects", response_model=EnterpriseProjectModel)
async def create_enterprise_project(
    req: CreateProjectRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    user_id = claims["sub"]
    user_role = claims.get("role")

    if user_role not in [UserRole.ENTERPRISE.value, UserRole.ADMIN.value]:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Only verified enterprise accounts or administrators can create enterprise projects."
        )

    user = await db.users.find_one({"id": user_id})
    target_org_id = req.organization_id or user_id

    # If creating under a specific organization, verify caller is owner or admin/manager
    if req.organization_id and req.organization_id != user_id:
        await verify_org_membership(
            db,
            req.organization_id,
            user_id,
            allowed_roles=[OrganizationMemberRole.OWNER, OrganizationMemberRole.ADMIN, OrganizationMemberRole.MANAGER]
        )
        target_org_id = req.organization_id

    org_name = user.get("name", "Enterprise Partner") if user else "Enterprise Partner"
    proj_id = f"ep_{uuid.uuid4().hex[:12]}"
    now_ms = int(time.time() * 1000)

    proj_doc = {
        "id": proj_id,
        "org_id": target_org_id,
        "org_name": org_name,
        "title": req.title.strip(),
        "description": req.description.strip(),
        "location": req.location.strip(),
        "budget_xaf": req.budget_xaf,
        "status": "ACTIVE",
        "created_at": now_ms
    }
    await db.enterprise_projects.insert_one(proj_doc)
    return EnterpriseProjectModel(**proj_doc)

@router.get("/organizations/{org_id}/members", response_model=List[OrganizationMemberModel])
async def list_organization_members(org_id: str, claims: dict = Depends(get_current_user_claims)):
    db = get_database()
    user_id = claims["sub"]

    if claims.get("role") != UserRole.ADMIN.value:
        await verify_org_membership(db, org_id, user_id)

    cursor = db.organization_members.find({"organization_id": org_id}, {"_id": 0}).sort("created_at", 1)
    members = await cursor.to_list(length=100)
    return members

@router.post("/organizations/{org_id}/members", response_model=OrganizationMemberModel)
async def add_organization_member(
    org_id: str,
    req: AddOrganizationMemberRequest,
    claims: dict = Depends(get_current_user_claims)
):
    db = get_database()
    user_id = claims["sub"]

    # Only OWNER or ADMIN can invite members
    if claims.get("role") != UserRole.ADMIN.value:
        await verify_org_membership(
            db,
            org_id,
            user_id,
            allowed_roles=[OrganizationMemberRole.OWNER, OrganizationMemberRole.ADMIN]
        )

    target_email = req.user_email.strip().lower()
    target_user = await db.users.find_one({"email": target_email})
    if not target_user:
        raise HTTPException(status_code=404, detail="Target user not found on FIXO platform.")

    # Check if already a member
    existing = await db.organization_members.find_one({
        "organization_id": org_id,
        "user_id": target_user["id"]
    })
    if existing:
        raise HTTPException(status_code=400, detail="User is already a member of this organization.")

    member_id = f"mem_{uuid.uuid4().hex[:12]}"
    now_ms = int(time.time() * 1000)

    member_doc = {
        "id": member_id,
        "organization_id": org_id,
        "user_id": target_user["id"],
        "user_name": target_user["name"],
        "user_email": target_email,
        "role": req.role.value,
        "created_at": now_ms
    }
    await db.organization_members.insert_one(member_doc)
    return OrganizationMemberModel(**member_doc)
