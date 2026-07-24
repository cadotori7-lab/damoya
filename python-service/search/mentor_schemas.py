from pydantic import BaseModel, Field


class MentorCandidate(BaseModel):
    member_id: int
    login_id: str = ""
    name: str = ""
    field: str = ""
    intro: str = ""
    career: str = ""
    cert: str = ""
    account_status: str = ""
    approved: bool = False
    profile_public: bool = False
    mentor_reference: str
    similarity_score: float = 0.0


class MentorCandidatesResult(BaseModel):
    candidates: list[MentorCandidate] = Field(default_factory=list)
    indexed_count: int = 0
