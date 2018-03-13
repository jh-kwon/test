
# 환경 정보
1. Spring Boot+ Java 8 기반으로 작성
2. 저장소는 MongoDB 사용
    : 단, 테스트 목적이므로 사용이 간단한 Embedded MongoDB(in-memory DB)를 사용
3. 클라이언트 역할의 front end는 thymeleaf 프레임워크 기반에 jquery + bootstrap으로 작성
    (1) 쿠폰 리스트를 표시하기 위해서는 jquery의 plugin인 DataTables를 사용함
    : https://datatables.net/


# 프로젝트 빌드 및 실행 방법
##       *소스 레포지토리 : https://github.com/jh-kwon/kptest.git

1. 빌드 : maven이용
    mvn package

2. 실행
    (1) 단독 실행
     일반적인 Java 프로그램과 같이 main()함수 호출만으로 실행(배포도 executable JAR형태로 이루어짐)되므로
     Java가 설치되어 있다면 그 외에 특별한 환경 설정이 필요 없음

     ex> java -jar kptest-0.0.1-SNAPSHOT.jar
     : 1의 단계의 빌드를 통해 생성된 jar를 실행

    (2) IDE를 통한 실행
    : Spring Boot를 개발할 때 일반적으로 많이 사용되는 STS와 IntelliJ에서의 실행 방법만 기술하도록 함

        0) 위에 명시된 소스 레포지토리에서 소스를 클론한 후 각각의 IDE에서 프로젝트로 추가

        1) STS

            a. 상위 메뉴에서 Run > Run Configurations 선택
            b. 나타난 팝업 창에서 Spring Boot App 항목 선택
            c. new버튼(파일에 +표시 아이콘) 클릭해서 새로운 설정 생성
                Project : 실행하고자 하는 현재 프로젝트 선택
                Main Type : Search 버튼을 클릭해서 결과로 나오는 KakaopayApplication을 선택
            d. Apply를 통해 설정 내용 저장
            e. Run 버튼을 클릭


        2) IntelliJ
         : 일반 자바 프로그램과 동일하게 개발 및 실행이 가능하므로 Community버전으로도 실행 가능함

            a. 상위 메뉴에서 Run > Edit Configurations 선택
            b. 나타난 팝업 창에서 왼쪽 상단의 +를 클릭해서 나타나는 목록중 Application 항목 선택
            c. 오른쪽의 설정화면의 Configuration탭에서
                Main Class 항목의 오른쪽에 있는 ... 버튼을 클릭해서 나타난 검색창에서 KakaopayApplication을 찾아서 선택
            d. Apply를 통해 설정 내용 저장
            e. 위의 과정을 통해 설정한 내용이 작업창의 도구바의 실행버튼(초록색 화살표)옆에 나타나는데,
             이 상태가 해당 설정이 선택된 상태이므로 그대로 실행버튼을 누르면 됨


# 특이 사항
1. 프로세스와 동일한 메모리에 부팅되는 in-memory DB를 사용함에 따라
    (1) 어플리케이션 프로세스를 종료할 때 데이터도 모두 사라지게 된다
     : 즉, 재기동 할때마다 데이터 초기화 -> 유저 시퀀스 시작값은 초기화 과정에 따라 임의로 조절 가능
    (2) 프로세스 기동 시 DB도 함께 기동하기 때문에 속도가 조금 느림
2. 쿠폰 발급 ID(seq) 관련
    (1) 증가하는 일련의 숫자이지만 중간에 건너뛰어질 수 있음
    (2) 쿠폰 발급 ID의 시작값은 1부터 시작되나, 초기값 설정에 따라 변경도 가능함.
3. 쿠폰번호는 쿠폰 발급 ID를 참조하여 사전순으로 발급되나, 랜덤하게 변경하도록 가능함
4. 쿠폰리스트의 페이징은 아래와 같이 동작함
    (1) 한 번의 서버 요청으로 미리 어느정도 일정 데이터를 획득하여 페이징
    (2) 로딩하고 있는 페이지의 마지막 페이지에 다다르면(해당 페이지로 직접 이동하거나 next 버튼을 통해)
       자동으로 다음에 로딩할 데이터를 서버에 요청함



# 문제해결 전략
## 과제 :
*  사용자로부터 이메일 주소를 입력으로 받아서
    16자리의 알파벳과 숫자로 이루어진 **중복없는** 쿠폰 번호를 발급하고
    발급된 쿠폰 정보를 같은 페이지에 리스팅하는 웹어플리케이션 개발
       * 쿠폰번호는 [0-9a-zA-Z]으로 구성
       * 중복된 이메일 입력에 따른 쿠폰 발행은 불가
       * 쿠폰번호 리스팅은 Pagination 가능하도록 구현

1. 이메일 주소 유효성 확인
    : 정규표현식으로 이메일 주소의 유효성을 확인함, 유효성 체크 기준은 아래와 같다
    ! 클라이언트 단에서의 유효성 검사는 없으며, 서버 단에서 이루어짐

    (1) 이메일은 알파벳이나 숫자로 시작해야한다
    (2) 이메일은 ASCII코드로 구성되어 있되, 일반적인 표시 문자이다
      : 즉, 알파벳, 숫자, 일반적으로 많이 쓰이는 몇가지 기호(!#$%*+=~._-)만 인정함
    (3) @을 기준으로 전반부와 후반부로 나누어 보았을 때,
        1) 전반부는 (2)에서 언급한 문자를 모두 사용 가능
        2) 후반부의 경우는 알파벳, 숫자, _-. 만 사용 가능하며, 맨마지막 .이후에는 2자이상 10자 미만의 알파벳만 사용가능

2. 16자리의 알파벳과 숫자로 이루어진 중복 없는 쿠폰 번호 발급
    (1) DB나 파일 등을 이용해 미리 발급해놓은 쿠폰 번호를 소비하는 방식이 아니라
     쿠폰 번호 발급 요청이 올때 마다 생성하는 방식
    (2) 쿠폰 번호는 숫자의 진법표시와 동일한 방식으로 발급
        1) 즉, 각 자리를 알파벳 대,소문자와 숫자인 62개의 기호내에서 표시가 가능 62진법으로 상정
        2) 따라서 쿠폰번호를 뒤에서 부터 봤을때 각 자리는 62^0, 62^1, 62^2, ..., 62^15의 자리에 해당
        3) 이 62진법으로 표시할 10진법의 숫자는 쿠폰의 이슈 발급 id이다
         : 즉, 이슈 발급 id를 62진법으로 표현하되, 항상 16자리수가 되도록 앞쪽의 빈자리는 0(0에 해당되는 기호)으로 표시하도록 함
        4) 1부터 62에 해당하는 기호는 임의적으로 설정이 가능하므로 사전순으로 랜덤으로도 생성되도록 할 수 있음

3. 중복된 이메일에 대한 쿠폰 발급 불가
    (1) 문제 상황에 대한 자세한 시나리오 제시가 별도로 없었으므로
     부담이 적다고 판단한 DB의 unique index를 통해서 중복 이메일이 insert되지 않도록 함
     : 즉, 중복된 데이터로 인한 DB에 insert 실패시 발생하는 에러를 비즈니스 로직에서 exception으로 받아 실패로 처리.
      단, 이 때 쿠폰 발급 id의 시퀀스는 이미 1 증가하게 되며, 이로 인해 사용되지 않는(건너뛰어진) id가 생기게 됨.

      ! 실제 환경에서는 unique index 대상으로 이메일과 발급된 쿠폰번호를 복합 인덱스로 생성하겠지만,
        해당 프로젝트에서 사용중인 Embedded MongoDB에 대한 인덱스 생성에
        Spring Data의 MongoTemplate를 이용해야 하는데, 하나의 필드에 대한 인덱스 생성만 지원하므로
        기능 구현에 있어 우선 순위가 높은 이메일에 대해서만 unique인덱스를 생성함

4. 쿠폰번호 리스팅은 Pagination 가능하도록 구현
    (1) 페이징에 대한 제어권은 클라이언트에 있다고 상정,
     단, 잦은 서버 요청으로 인한 부담을 줄이기 위해 한 번의 서버 요청으로 미리 어느정도 일정 데이터를 획득하도록 한다.
    (2) 페이징 제어를 위해 한 번의 서버 요청으로 받을 데이터 건수(rowcnt), 요청 페이지에 해당하는 데이터의 기준점(rowno)을 파라미터로 전달한다.
        1) rowcnt는 한 페이지에서 표시할 데이터 건수와 동일하게 해도 되지만, (1)에서 말했듯이 서버 요청으로 인한 부담을 줄이기 위해
          적당히 여유있는 값으로 설정해 어느 정도의 데이터를 미리 확보해 두도록 한다.
          ! 단, 너무 큰 값을 이용한 요청으로 인한 서버의 부하를 줄이기 위해 서버에서 상한값을 정해두고 해당 값을 초과했을 시에 기본값으로 바꿔서 처리
        2) rowno는 서버에서 관리하는 값이다.
            a. 0일 경우, 맨 처음 페이지 인 것으로 상정한다.
              따라서, 서버와의 통신 이전 요청의 시작이나 페이지 초기화의 경우에는 0으로 설정해서 서버 요청
            b. 한번이라도 서버 요청을 통해 데이터를 획득하게 되면 서버에서 돌려주는 0이 아닌 값으로 갱신, 다음 페이지 요청에 사용
            c. 서버에서 -1을 돌려주는 경우는 다음 페이지에 해당하는 내용이 없음, 즉 마지막 페이지임을 의미함