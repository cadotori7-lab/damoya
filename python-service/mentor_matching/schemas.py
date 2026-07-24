from pydantic import BaseModel, ConfigDict, Field

# 프로젝트 멘토 매칭 요청 스키마
class MentorMatchRequest(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    reference: str = Field(min_length=1, max_length=100)
    project_name: str = Field(alias="projectName", min_length=1, max_length=200)
    project_description: str = Field(
        alias="projectDescription",
        min_length=10,
        max_length=5000,
    )

# 멘토 선택 스키마
class MentorChoice(BaseModel):
    member_id: int = Field(description="후보 목록에 있는 멘토 회원 ID")
    reason: str = Field(
        min_length=1,
        max_length=500,
        description="프로젝트와 멘토가 잘 맞는 구체적인 이유",
    )

# 멘토 선택 결과 스키마
class MentorSelection(BaseModel):
    recommendations: list[MentorChoice] = Field(
        default_factory=list,
        max_length=3,
    )

# 멘토 추천 스키마
class MentorRecommendation(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    member_id: int = Field(alias="memberId")
    login_id: str = Field(alias="loginId")
    name: str
    field: str
    career: str
    cert: str
    similarity_score: float = Field(alias="similarityScore")
    reason: str

# 프로젝트 멘토 매칭 응답 스키마
class MentorMatchResponse(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    reference: str
    project_name: str = Field(alias="projectName")
    candidate_count: int = Field(alias="candidateCount")
    indexed_count: int = Field(alias="indexedCount")
    model: str
    recommendations: list[MentorRecommendation] = Field(default_factory=list)
