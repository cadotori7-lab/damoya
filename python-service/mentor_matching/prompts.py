from langchain_core.prompts import ChatPromptTemplate

MENTOR_SELECTION_PROMPT = ChatPromptTemplate.from_messages(
    [
        (
            "system",
            (
                "너는 프로젝트에 가장 적합한 멘토를 선별하는 매칭 담당자다. "
                "제공된 후보 안에서만 최대 3명을 고른다. 전문 분야, 소개, 경력, "
                "자격증이 프로젝트의 기술과 목표에 얼마나 직접적으로 도움이 되는지 "
                "비교한다. 후보에 없는 member_id를 만들지 말고, 각 추천 사유는 "
                "후보 정보의 구체적인 근거를 포함해 한국어로 작성한다."
            ),
        ),
        (
            "human",
            (
                "[프로젝트]\n"
                "이름: {project_name}\n"
                "설명: {project_description}\n\n"
                "[벡터 검색 후보]\n{candidates}"
            ),
        ),
    ]
)
