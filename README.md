개인 가계부 - CLI 애플리케이션 (README.md)
개인 재정을 효율적으로 관리하기 위한 가벼운 자바 기반의 명령줄 인터페이스(CLI) 애플리케이션입니다. 이 도구는 사용자가 금융 거래 내역을 기록, 조회, 수정, 삭제할 수 있게 해주며, 모든 데이터는 CSV 파일로 영구적으로 저장됩니다.

📝 기능
금융 거래 내역 추가: 날짜, 금액, 카테고리, 메모를 포함한 거래 내역을 기록합니다.

내역 삭제: 고유 ID를 기준으로 특정 거래 내역을 삭제합니다.

데이터 조회: 모든 내역을 보거나, 날짜 및 카테고리별로 필터링하여 조회할 수 있습니다.

데이터 영속성: 모든 데이터는 CSV 파일에 자동으로 저장되며, 수동으로 저장하거나 불러올 수 있습니다.

입력 유효성 검사: 모든 사용자 입력에 대해 포괄적인 유효성 검사를 수행하여 데이터의 신뢰성을 보장합니다.

메뉴 기반 인터페이스: 번호로 된 메뉴 옵션을 통해 직관적인 탐색이 가능합니다.

📂 프로젝트 구조
src/
└── main/
    └── java/
        └── com/
            └── accountbook/
                ├── AccountBookApp.java      # 메인 애플리케이션 시작점
                ├── model/
                │   └── LedgerItem.java      # 핵심 데이터 모델
                ├── service/
                │   └── LedgerService.java   # 비즈니스 로직 및 CRUD 작업
                ├── ui/
                │   └── CliInterface.java    # 명령줄 인터페이스
                └── util/
                    ├── ValidationUtil.java  # 입력 유효성 검사 유틸리티
                    └── CsvFileHandler.java  # CSV 파일 작업
🚀 시작하기
요구 사항
자바 8 이상

외부 종속성(라이브러리) 없음

컴파일 및 실행 가이드
프로젝트 루트 디렉터리(예: c:\accountbook)에서 아래 명령어를 순서대로 실행하세요.

1. 컴파일

unmappable character 및 error while writing 오류를 방지하기 위해 UTF-8 인코딩을 명시하고 클래스 파일이 올바른 위치에 생성되도록 . 옵션을 사용합니다.

Bash

javac -encoding UTF-8 -d . src\main\java\com\accountbook\*.java src\main\java\com\accountbook\model\*.java src\main\java\com\accountbook\service\*.java src\main\java\com\accountbook\ui\*.java src\main\java\com\accountbook\util\*.java
2. 애플리케이션 실행

컴파일에 성공하면, 프로젝트의 최상위 디렉터리에서 다음 명령어를 사용하여 애플리케이션을 실행할 수 있습니다.

기본 사용법:

Bash

java com.accountbook.AccountBookApp
사용자 지정 데이터 파일 사용:

Bash

java com.accountbook.AccountBookApp my_ledger.csv
📖 사용 가이드
애플리케이션은 계층적 메뉴 시스템을 제공하여 쉽게 조작할 수 있습니다.

메인 메뉴
==== 개인 가계부 ====
1. 내역 관리
   1.1 내역 추가
   1.2 내역 삭제
2. 내역 조회
   2.1 전체 보기
   2.2 날짜 범위별 보기
   2.3 카테고리별 보기
3. 파일에 저장
4. 파일 불러오기
5. 프로그램 종료
내역 추가
메인 메뉴에서 **1 (내역 관리)**을 선택한 다음, **1 (내역 추가)**을 선택합니다.

다음 정보를 입력합니다:

날짜: YYYY-MM-DD 형식 (2025-10-01 또는 그 이후)

금액: 1억 이하의 양의 정수

카테고리: Food, Transport, Living, Shopping, Transfer, Hobby 중 하나

메모: 선택 사항, 최대 50자

내역 삭제
메인 메뉴에서 **1 (내역 관리)**을 선택한 다음, **2 (내역 삭제)**를 선택합니다.

삭제하려는 내역의 ID를 입력합니다.

내역 조회
전체 보기: 모든 내역을 ID별로 정렬하여 보여줍니다.

날짜 범위별 보기: 시작 날짜와 끝 날짜를 입력하여 해당 범위 내의 내역을 필터링합니다.

카테고리별 보기: 특정 카테고리의 모든 내역을 보여줍니다.

⚠️ 문제 해결
일반적인 문제
컴파일 오류: 자바 8+가 설치되어 있는지, 그리고 JAVA_HOME 환경 변수가 올바르게 설정되었는지 확인하세요.

파일 권한 오류: 애플리케이션 디렉터리에 쓰기 권한이 있는지 확인하거나, 관리자 권한으로 명령 프롬프트를 실행해 보세요.

CSV 형식 문제: 기존 CSV 파일이 있다면 id,date,category,amount,note 형식의 올바른 헤더를 가지고 있는지 확인하세요.

💡 향후 개선 사항 (2단계)
통계 계산 (월별/카테고리별 합계)

무결성 검사를 포함한 고급 파일 처리

텍스트 기반 요약 보고서

포괄적인 단위 테스트

이 프로젝트는 자바 학습 및 개발 연습을 위한 교육 목적으로 제작되었습니다.