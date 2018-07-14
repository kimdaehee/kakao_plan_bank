# kakao_plan_bank

core 구성
(1) kakaobank.core.ai.producer
해당 프로세스는 요구사항(2)에 해당되는 내용이 담긴 엔진입니다.
자동으로 계정생성을 하고, 입금30건, 출근30건, 계좌이체40건(현존하는계좌에 랜덤이체)의 프로그램으로 구성되어있고, 해당계정이 생성될때마다, 위의 조건의 거래내역을 kafka를 통해 전송하게 되는 구조입니다.

(2)kakaobank.core.s1.profiler
해당 프로세스는 kafka의 consumer 역할을 수행합니다.
producer에서 전송하는 각종 로그를 받아서 저장하는 역할을 합니다.

(3)restful_api.account_Info
고객 기본정보 프로파일 조회의 요구사항을 spark router를 이용하여 구현해봤습니다.
/api/customer?costomer_number=1
 

(4)restful_api.account_Info
고객 계좌 프로파일 조회의 요구사항을 위와같은 방법으로 구현하였습니다.
/api/customer?costomer_number=1&account_number=3333021
 

