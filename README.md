# 📌프로젝트 소개
* DB 쓰기 작업 및 알람 발송을 카프카를 사용하여, 비동기적으로 처리 
* k6를 사용하여 50명이 1초마다 API 요청하도록 과부하 테스트 
* 알람 발송은 실제로 하지 않고 Thread.sleep(1000) 처리

  
## ⚙️Kafka Cluster 및 Topic 설정
* Kafka Cluster: 3개의 브로커(Kafka 서버)를 하나의 클러스터로 구성
* Topic: `order.notification`
  * Partition: 5개
  * Replication Factor: 3
* 토픽 생성 명령어:
  ```bash
  docker exec -it kafka-1 kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 5 --topic order.notification
 
* 결과적으로 3개의 카프카 서버 각각이 리더 파티션을 1, 2, 2개씩 할당.

## 💻카프카 프로듀서 서버  
* **POST - http://localhost:8080/orders/async** 로 API요청 시 주문을 DB에 저장 후 알람 발송 메시지를 kafka에 전송
* 테스트 데이터:
  ```bash
  {
    "userId": 1,
    "amount": 1000
  }
  ```
 

## 💻카프카 컨슈머 서버  
* 리스너에서 concurrency = 5로 설정하여 5개의 스레드로 메시지 poll
* 메시지를 꺼내 Thread.sleep(1000)으로 알람 발송 후 주문 테이블의 알람발송 컬럼 update

## 🚀실행 방법
1. 터미널에서 프로듀서 코드 다운로드
   ```bash
   git clone https://github.com/nonkafka-vs-kafka-k6-test/kafka-async-producer.git
   ```
3. 터미널에서 컨슈머 코드 다운로드
   ```bash
   git clone https://github.com/nonkafka-vs-kafka-k6-test/kafka-async-consumer.git
   ```
5. 프로듀서, 컨슈머 서버의 application.yaml파일에 본인의 DB정보 입력
6. 터미널에서 프로듀서 프로젝트 경로로 이동 후 카프카 컨테이너 실행
   ```bash
   docker compose up -d
   ```
8. 카프카 컨테이너 실행 확인
   ```bash
   docker ps
   ```
10. 토픽 생성
    ```bash
    docker exec -it kafka-1 kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 5 --topic order.notification
    ```
12. 토픽 생성 확인
    ```bash
    docker exec -it kafka-1 kafka-topics --describe --bootstrap-server localhost:9092 --topic order.notification
    ```
14. powershell 혹은 cmd를 관리자 권한으로 열고 k6 설치(이미 설치했으면 생략)
    ```bash
    choco install k6
    ```
16. 프로듀서, 컨슈머 애플리케이션 실행
17. 터미널에서 프로듀서 프로젝트 경로로 이동 후 k6테스트 시작
    ```bash
    k6 run test.js
    ```

## 📊k6 과부하 테스트 결과
```text
         /\      Grafana   /‾‾/                                                                                                                                                         
    /\  /  \     |\  __   /  /                                                                                                                                                          
   /  \/    \    | |/ /  /   ‾‾\                                                                                                                                                        
  /          \   |   (  |  (‾)  |                                                                                                                                                       
 / __________ \  |_|\_\  \_____/ 


     execution: local
        script: test.js
        output: -

     scenarios: (100.00%) 1 scenario, 50 max VUs, 1m0s max duration (incl. graceful stop):
              * default: 50 looping VUs for 30s (gracefulStop: 30s)



  █ TOTAL RESULTS 

    checks_total.......: 1450    47.337819/s
    checks_succeeded...: 100.00% 1450 out of 1450
    checks_failed......: 0.00%   0 out of 1450

    ✓ status is 200

    HTTP
    http_req_duration..............: avg=50.36ms min=2.67ms med=11.15ms max=976.76ms p(90)=33.99ms p(95)=158.02ms
      { expected_response:true }...: avg=50.36ms min=2.67ms med=11.15ms max=976.76ms p(90)=33.99ms p(95)=158.02ms
    http_req_failed................: 0.00%  0 out of 1450
    http_reqs......................: 1450   47.337819/s

    EXECUTION
    iteration_duration.............: avg=1.05s   min=1s     med=1.01s   max=1.99s    p(90)=1.04s   p(95)=1.16s   
    iterations.....................: 1450   47.337819/s
    vus............................: 50     min=50        max=50
    vus_max........................: 50     min=50        max=50

    NETWORK
    data_received..................: 264 kB 8.6 kB/s
    data_sent......................: 240 kB 7.8 kB/s




running (0m30.6s), 00/50 VUs, 1450 complete and 0 interrupted iterations
default ✓ [======================================] 50 VUs  30s

```

