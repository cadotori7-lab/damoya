# Git MCP 리포트 기능 이전 묶음

## 새 브랜치에 적용할 파일

`git-feature` 폴더의 내용을 프로젝트 루트에 동일한 경로로 복사합니다.

- Python MCP Git 저장소 분석 및 PDF 생성 기능
- Python 서비스 설정과 의존성
- `TestController` 및 JSP 테스트 페이지
- 테스트 코드
- Git 제외 규칙
- `python-service/README.md` 설명

## 선택 적용

관리자 대시보드 변경도 필요하면 `optional-dashboard` 폴더의 내용을 프로젝트 루트에 복사합니다.

`dashboard.jsp`에는 이번 Git MCP 기능과 무관한 앞선 관리자 대시보드 작업이 포함되어 있습니다.

## 포함하지 않은 파일

- `deploy.bat`: 작업 전에 이미 수정되어 있던 파일
- `target/`: Maven 빌드 산출물
- `output/pdf/damoya-mcp-sample-report.pdf`: 테스트용 생성 PDF

## 주의

이 보관 폴더 자체는 새 브랜치에 커밋하지 말고, 내부 파일만 프로젝트의 원래 경로로 복사합니다.
