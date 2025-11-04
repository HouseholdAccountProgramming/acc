개인 가계부 - CLI 애플리케이션
개인 재정을 관리하기 위한 자바 기반의 명령줄 인터페이스(CLI) 애플리케이션입니다. 이 경량 도구는 사용자가 금융 거래 내역을 기록, 조회, 수정, 삭제할 수 있게 해주며, 데이터는 CSV 또는 JSON 파일 형식으로 영구적으로 저장됩니다.

📝 기능
금융 거래 내역 추가: 유형(수입/지출), 날짜, 금액, 카테고리, 설명을 포함한 거래 내역 기록

내역 삭제: ID를 기준으로 거래 내역 삭제

데이터 조회: 모든 내역 보기, 날짜 범위별 필터링, 카테고리별 필터링

다중 파일 형식 지원: CSV와 JSON 형식 간 동적 전환 가능

데이터 영속성: 선택한 파일 형식으로 자동 저장되며, 수동으로 저장/불러오기 가능

입력 유효성 검사: 모든 사용자 입력에 대한 포괄적인 유효성 검사

메뉴 기반 인터페이스: 번호로 된 메뉴 옵션을 통한 직관적인 내비게이션

📂 프로젝트 구조
src/
└── main/
    └── java/
        └── com/
            └── accountbook/
                ├── AccountBookApp.java        # 메인 애플리케이션 시작점
                ├── model/
                │   └── LedgerItem.java        # 핵심 데이터 모델
                ├── service/
                │   └── LedgerService.java     # 비즈니스 로직 및 CRUD 작업
                ├── ui/
                │   └── CliInterface.java      # 명령줄 인터페이스
                └── util/
                    ├── ValidationUtil.java    # 입력 유효성 검사 유틸리티
                    ├── FileFormat.java        # 파일 형식 열거형
                    ├── CsvFileHandler.java    # CSV 파일 작업
                    └── JsonFileHandler.java   # JSON 파일 작업
🚀 시작하기
요구 사항
자바 8 이상

외부 종속성(라이브러리) 없음

1. 컴파일
프로젝트 루트 디렉터리(예: C:\b\accountbook)에서 아래 명령어를 실행하세요.

(unmappable character 오류 방지를 위해 -encoding UTF-8 옵션을, 올바른 위치에 클래스 파일을 생성하기 위해 -d . 옵션을 사용합니다.)

Bash

javac -encoding UTF-8 -d . src/main/java/com/accountbook/*.java src/main/java/com/accountbook/model/*.java src/main/java/com/accountbook/service/*.java src/main/java/com/accountbook/ui/*.java src/main/java/com/accountbook/util/*.java
2. 애플리케이션 실행
기본 사용법 (기본 ledger.csv 파일 사용)

Bash

java com.accountbook.AccountBookApp
사용자 지정 데이터 파일 사용

Bash

java com.accountbook.AccountBookApp my_ledger.csv
📖 사용 가이드
메인 메뉴
애플리케이션은 계층적 메뉴 시스템을 제공합니다:

==== 개인 가계부 ====
현재 파일 형식: CSV 형식
1. 내역 관리
    1.1 내역 추가
    1.2 내역 삭제
2. 내역 조회
    2.1 전체 보기
    2.2 날짜 범위별 보기
    2.3 카테고리별 보기
3. 파일에 저장
4. 파일 불러오기
5. 파일 형식 변경
6. 프로그램 종료

옵션 선택: _
내역 추가
1. 내역 관리 → 1.1 내역 추가를 선택합니다.

다음 정보를 순서대로 입력합니다:

유형: 수입 또는 지출

날짜: YYYY-MM-DD 형식 (예: 2025-10-15)

금액: 1억 이하의 양의 정수

카테고리: Food, Transport, Living, Shopping, Transfer, Hobby 중 선택 (하위 카테고리는 안내에 따라 선택)

설명: 선택 사항, 최대 50자

내역 조회
2. 내역 조회에서 원하는 옵션을 선택합니다.

전체 보기: ID별로 정렬된 모든 내역을 보여줍니다.

날짜 범위별 보기: 시작 날짜와 종료 날짜를 입력하여 두 날짜 사이의 내역을 필터링합니다.

카테고리별 보기: 특정 카테고리의 내역을 보여줍니다.

내역 삭제
1. 내역 관리 → 1.2 내역 삭제를 선택합니다.

시스템이 현재 내역을 ID와 함께 표시하며, 삭제하려는 내역의 ID를 입력합니다.

파일 형식 관리
5. 파일 형식 변경을 선택합니다.

현재 사용 중인 파일 형식과 지원되는 형식 목록이 표시됩니다.

새로운 형식을 선택하면 기존 데이터가 새 형식으로 자동 변환됩니다.

🗃️ 데이터 형식
CSV 형식
id,type,date,category,amount,description

1,지출,2025-10-02,Food,12000,Lunch (점심)
2,지출,2025-10-02,Transport,4500,Bus fare (버스 요금)
3,지출,2025-10-03,Living,35000,Groceries (식료품)
JSON 형식
JSON

{
  "items": [
    {
      "id": 1,
      "type": "지출",
      "date": "2025-10-02",
      "category": "Food",
      "amount": 12000,
      "description": "Lunch (점심)"
    },
    {
      "id": 2,
      "type": "지출",
      "date": "2025-10-02",
      "category": "Transport",
      "amount": 4500,
      "description": "Bus fare (버스 요금)"
    },
    {
      "id": 3,
      "type": "지출",
      "date": "2025-10-03",
      "category": "Living",
      "amount": 35000,
      "description": "Groceries (식료품)"
    }
  ]
}
⚠️ 문제 해결
컴파일 오류: 자바 8+가 설치되어 있는지, 그리고 JAVA_HOME 환경 변수가 올바르게 설정되었는지 확인하세요.

파일 권한 오류: 애플리케이션 디렉터리에 쓰기 권한이 있는지 확인하거나, 관리자 권한으로 명령 프롬프트를 실행해 보세요.

CSV/JSON 형식 문제: 기존 파일이 있다면 헤더 형식이 올바른지 확인하세요.

(CSV 헤더: id,type,date,category,amount,description)

💡 향후 개선 사항 (2단계)
통계 계산 (월별/카테고리별 합계)

무결성 검사를 포함한 고급 파일 처리

텍스트 기반 요약 보고서

포괄적인 단위 테스트

이 프로젝트는 자바 학습 및 개발 연습을 위한 교육 목적으로 제작되었습니다.