# ♾️DevLoop | StackConnect

<tbody>
    <tr>
      <td style="text-align:center; vertical-align:middle;">
        <a href="https://youtu.be/MhNaepmEHxw" style="display:inline-block; text-align:center;">
          <img src="https://github.com/user-attachments/assets/370d25ed-9a61-4886-97e5-4fc9ff51712a" height="500px" width="1500px" alt=""/><br />
          <span style="font-size:36px; font-weight:bold; color:#000; display:block; margin-top:10px; text-align:center;">
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
              <b><img src="https://img.shields.io/badge/YouTube-FF0000?style=for-the-badge&logo=YouTube&logoColor=white" /></b>
          </span>
        </a>
      </td>
        <br />
        <td style="text-align:center; vertical-align:middle;">
        <a href="https://candied-coil-34f.notion.site/DevLoop-StackConnect-1456913e3042802ca19ecd25f1b017e8?pvs=4" style="display:inline-block; text-align:center;">
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
              <b><img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" /></b>
          </span>
        </a>
      </td>
    </tr>
</tbody>

<br></br>
## 📚스터디형 개발자 통합 서비스

![devloop이미지](https://github.com/DevLoop-StackConnect/DevLoop/blob/main/dev.png)

>**DevLoop**는 통합형 개발자 커뮤니티로 **대규모 트래픽**에서도 이미지 및 강의영상을 **안정적이고 빠르게** 제공하며, 사용자 경험을 충족시키기 위해 **카테고리 및 클래스별로 세분화된 검색 옵션을 제공**합니다.
## 🎯프로젝트 핵심 목표

1. **성능 최적화**
    - Elasticsearch를 통해 대량 데이터의 실시간 검색 및 분석을 통한 향상된 검색결과 제공
    - Redis 캐싱을 통한 성능과 응답속도 향상

2. **대규모 트래픽 대응**
     - AWS CloudFront를 통한 컨텐츠 로드 속도 개선 및 효율적인 트래픽 관리
     - AWS LAB을 통한 효율적인 트래픽 관리 및 안정성을 향상

3. **운영 및 배포 효율화**
   - Jenkins를 통해 자동화된 빌드와 배포 파이프라인을 통한 배포 안정성 향상
   - Docker를 통해 환경 독립성 확보와 개발 및 배포 자동화로 개발 배포 속도 향상과 유연한 협업을 이룸
   - AWS RDS로 데이터 베이스 관리를 간소화 하여 운영 효율성 향상
   - Signed URL을 통해 유료 강의에 대한 사용자 접근을 제한해 유료 강의 영상 무단 다운로드 및 공유 방지

4. **로그 및 모니터링**
   - ELK Stack으로 커뮤니티 서비스에서 데이터 관리 및 모니터링을 통해 실시간 로그 분석
   - Prometheus & Grafana 를 통해 시스템 성능 메트릭 수집 및 서버 성능 상태 모니터링으로 안정성 확보

<br></br>
   ## 🔑KEY SUMMARY

   ### **🔫 트러블 슈팅 : Elasticsearch - Full-Text Search 성능 및 안정성 개선**

   > Elasticsearch 도입으로 기존 검색 속도 대비 **99%의 성능 향상** 및 Fallback 매커니즘 도입으로 **서비스 중단 발생X**
   
![스크린샷 2024-11-18 오전 11 40 45](https://github.com/user-attachments/assets/7ea9ee6f-b90b-42ba-b505-73798526ff65)



1.  **문제상황**
   - **JPA, QueryDSL**
     - 기본적인 데이터 조회 및 필터링은 문제 없었지만, 데이터 양 증가했을 때, Full-Text Search와 같이 특정 키워드 데이터를 찾을 때 성능저하 발생
   - **Redis Cache**
     - Cache Miss 발생 시 검색 성능 저하 문제 발생

2. **해결 방안**
   - **Elasticsearch 도입**
   -  데이터를 Elasticsearch에 인덱싱하여 검색 요청 시 DB를 직접 조회하지 않고, 미리 생성된 검색 인덱스 활용하도록 설계.
   -  또한 캐싱기능을 활용해 동일한 검색 요청 반복시 더 빠르게 결과를 반환하도록 구현
   -  Fallback 매커니즘 도입으로 Elasticsearch에서의 비정상적인 동작 및 장애 발생 시, 기존에 사용하던 JPA, RedisCachs, QueryDSL을 활용한 검색으로 자동전환하도록 함.
  
3. **결과**
   이를 통해 Full-Text Search와 다중 필터 조건을 처리하는 검색 속도 기존 JPA대비 50% 향상
  실제 장애 상황 테스트 결과, Fallback매커니즘으로 평균 2~3초 이내 대체 검색 수행
  Redis & Elasticsearc 캐싱 활용으로 JPA 대비 99%의 성능 향상 기록

<br></br>
   ### **👍 성능 개선 : 첨부파일 로드, AWS CDN 적용으로 응답속도 약 16.7% 향상**

   > AWS CDN 도입으로 첨부파일 로드 속도가 약 16.7% 향상 및 트래픽 밀집되는 상황에서도 안정적인 서비스 유지

   1차 측정
   ![image (3)](https://github.com/user-attachments/assets/fdce17d4-5af8-466b-a820-3a9e5b622701)
<br></br>
   2차 측정
   ![image (4)](https://github.com/user-attachments/assets/6cf039df-07c8-475b-b24f-ec8083c0b124)


1. **도입 배경**  
   - PWT 게시글은 이미지 첨부 파일을 업로드 할 수 있는데, 유저 입장에서는 이미지를 포함 해당 페이지내용이 빠르게 로드 되어야 결제까지 고려해 볼 수 있다.  
     따라서 S3 이미지나 영상을 다운로드 하는 속도가 개선 되어야함.
   - 참여자수 제한 및 일정시간동안 판매되는 PWT 특성 상 트래픽 집중 가능성이 있어 트래픽 관리가 필요 

2. **기술적 선택지**  

   - **AWS CloudFront**  
      - S3 원본 서버 대신 엣지 서버에서 데이터 캐싱 후 제공
      - 사용자 위치에 가까운 엣지 서버를 활용해 속도 개선 및 트래픽 분산 가능  

   **결론:**  AWS Cloudfront 도입으로 Python을 활용해 각 url로 Get요청을 1000회 보내 응답시간 측정한 결과
   S3 url은 평균 0.12초, CloudFront url은 0.1초로 측정되어 **약 16.7%의 응답속도 개선을 이루었음**

<br></br>

## 인프라 아키텍쳐 & 적용 기술

### 아키텍쳐 다이어그램

![DevLoop 아키텍처 drawio (1)](https://github.com/user-attachments/assets/0a30f800-8eb7-4aaa-bd94-90f5b08fca73)
이 아키텍처는 ALB를 통해 사용자 요청을 처리하며, VPC 내 Public/Private 서브넷을 구성하였고, Jenkins를 통해 CI/CD 파이프라인을 구현함
Redis 및 SpringBoot 애플리케이션와 같은 서비스는 Private 서브넷에서 Docker 컨테이너로 구동되고, NAT Gateway와 Bastion Host를 통해 보안과 접근성을 유지하며 안정성이 높은 배포 환경을 제공함
<br></br>
<br></br>
<h3 align="center">✨ Tech Stack ✨</h3>
<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=20232a" />&nbsp
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=ffd35b" />&nbsp
    <img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white" />
</div>


<div align="center">
  <img src="https://img.shields.io/badge/Kibana-005571?style=for-the-badge&logo=Kibana&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/redis-FF4438.svg?style=for-the-badge&logo=redis&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white" />&nbsp
    <img src="https://img.shields.io/badge/grafana-F46800.svg?style=for-the-badge&logo=grafana&logoColor=white" />&nbsp
</div>
<div align="center">
  <img src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=Prometheus&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=Jenkins&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/LOGSTASH-D6D251?style=flat-square&logo=LOGSTASH&logoColor=white" />&nbsp
    <img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white" />
</div>

<h3 align="center">✨ Dev Tool ✨</h3>
<div align="center">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=20232a" />&nbsp
 <img src="https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=20232a" />&nbsp
</div>

<h3 align="center">✨ Communication ✨</h3>
<div align="center">
  <img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white" />&nbsp
  <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=20232a" />&nbsp
 <img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=20232a" />&nbsp
</div>

<br></br>
<details>
<summary><b>📦 적용 기술 상세보기</b></summary>

### ⚙️애플리케이션 개발 및 프레임워크
1. **JDK 17**
2. **Spring Boot 3.2.9**
3. **Spring Security & JWT**
   - **적용위치**: 회원가입 및 로그인 기능
   - **사용목적**: 인증 및 권한 관리로 보안 강화 및 토큰 기반의 인증 사용


### 💾 **데이터베이스 및 캐싱**
1. **Redis**
   - **적용위치**: 캐시 서버
   - **사용목적**: 검색에 cache기능 도입으로 검색 성능 향상
2. **MySQL**     

### 📉로그 및 모니터링
1. **ELK**
   - **적용위치**:
   - **사용목적**: ELK스택으로 커뮤니티 서비스에서 데이터 관리 및 모니터링의 효율성 높히기
2. **Prometheus & Grafana**
   - **적용위치**:
   - **사용목적**: 시스템 성능 메트릭 수집 및 서버 성능 상태 모니터
3. **Logstash**
4. **Kibana**


### 🔍검색 기능
1. **QueryDSL***
2. **Redis & Elasticsearch**
   - **적용위치**: 검색 기능
   - **사용목적**: 캐싱을 통한 검색 성능과 응답속도 향상을 이루고, 대량 데이터의 실시간 검색 및 분석을 통한 향상된 검색 결과 제공

### 🖼️첨부파일
1. **AWS CloudFront**
   - **적용위치**: 게시글 기능
   - **사용목적**: 콘텐츠 조회속도 향상 및 트래픽 관리
2. **AWS S3**
3. **Signed Url**
   - **적용위치**: 강의기능
   - **사용목적**: 유료 강의에 대한 사용자 접근 제한
4. **AWS MultipartUpload**
   - **적용위치**:
   - **사용목적**: 병렬 업로드로 속도 향상 및 안정성 강화
   


### 🌐 **인프라 및 배포**
1. **Jenkins**
   - **적용위치**: 
   - **사용목적**:자동화된 빌드와 배포 파이프라인을 통한 배포 안정성 향상
2. **AWS EC2**
3. **AWS RDS**
   - **적용위치**: 
   - **사용목적**:데이터베이스 관리 간소화 및 운영 효율성 향상
4. **AWD ALB**
   - **적용위치**:  
   - **사용목적**: 효율적인 트래픽 관리와 안정성 향상
5. **AWS Route53**
6. **AWS NAT**
7. **ASW IGW**
8. **Docker**
   - **적용위치**: 
   - **사용목적**: 환경 독립성 확보와 개발 및 배포 자동화로 속도 향상
     


### 💸외부 API
1. OAuth 2.0(Kakao)
   - **적용위치**: 회원가입 및 로그인
   - **사용목적**: 대부분의 사람들이 사용하는 카카오톡 소셜 로그인을 통해 편리한 회원가입 및 로그인 환경을 지원하고자 함.
2. Toss payments 
   - **적용위치**: 강의 및 PWT결제
   - **사용목적**: 강의 및 상품 판매과정에서 사용자 친화적이며 다양한 결제 수단을 지원하기 위함


### 📜테스트
1. Junit5
2. Mockito
3. JMEter
4. Python (MatplotLib)
   - **적용위치**: 
   - **사용목적**: 데이터 시각화 라이브러리를 사용해 테스트 결과를 시각적으로 표현함으로서 직관적으로 이해하기 위함
5. Kibana Dev Tool
   - **적용위치**: 
   - **사용목적**: Elasticsearch에 저장된 로그 데이터를 분석하고, 테스트 결과 및 시스템 동작을 효율적으로 모니터링 

</details>

<br></br>
## 주요 기능

### 🍁 **통합 검색 기능 : Elasticsearch 및 Redis를 활용**
- 카테고리 및 클래스별로 세분화된 검색 옵션 제공
- Elasticsearch를 통해 대량 데이터의 실시간 검색 및 분석을 통한 향상된 검색결과 제공
- Redis 캐싱을 통한 성능과 응답속도 향상

### 🍁 **슬랙 알림 기능**
- 슬랙/ 채널 입장 알림  
- DM 알림, Error메시지, 문의 메시지 등을 전송

### 🍁 **Project With Tutor 게시판**
- 실시간 예매 시스템과 결제 시스템을 통한 서비스 최적화.
- PWT 승인 시, 캘린더 기능 제공
- 낙관적 락 통해 Todo 동시성을 제어 하여 데이터 일관성 유지

### 🍁 **스터디 파티 모집 게시판**
- 자유롭게 스터디 파티를 모집하고, 최신 수능로 정렬된 게시글을 제공
- 스터디 파티의 진행 상태와 카테고리에 따른 검색 기능
- 게시글 수정 및 삭제를 통해 자유로운 관리 지원

### 🍁 **개발 커뮤니티 게시판**
- 통합 검색과 카테고리별 검색을 통해 원하는 정보를 쉽게 탐색 가능
  
### 🍁 **결제 기능**
- 장바구니 및 주문 시스템
- PG(토스페이먼츠)를 사용한 결제 시스템
- 비관적락을 적용하여 안정성 유지


### 🍁 **강의 제공을 통한 온라인 학습 지원**
- 강의 등록 기능
- 강의 후기 작성 기능으로 사용자 경험 공유
- SignedUrl, AWS Multipart Upload 사용으로 서버 메모리 사용량을 줄이고, 파일 업로드 시간 감소


### 🍁 **프로젝트 신청자 재고 관리 시스템**


<br></br>

## 🔥기술적 고도화

<details>
<summary><b>✅ AWS Multipart Upload로 대용량 강의 영상의 속도 문제 개선</b></summary>



#### 기술 선택지

- **MultipartFile 업로드**  
  
- **AWS Multipart Upload**
 


#### AWS Multipart Upload 방식 채택

- **MultipartFile 업로드**  
  - 대용량 파일(150MB 이상) 업로드 시 속도 저하 문제. 
  - 전체 파일 업로드 실패 시 처음부터 다시 업로드해야하는 비효율성

-**AWS Multipart Upload**
  - 파일을 병렬로 업로드 하여 속도를 향상
  - 네트워크 중단 시 실패한 부분만 재업로드 가능
  - 파일 크기에 따라 동적으로 분할 크기 조정 가능


### 적용 후

- 기존 MultipartFile 업로드 방식 대비 서버의 **메모리 사용량 감소**
- 대용량 파일 **업로드의 소요시간이 크게 줄어듬** 

</details>



<details>
<summary><b>✅ CDN으로 강의 컨텐츠 조회 속도 향상</b></summary>

### 배경

- 대용량 영상 파일에 접근할 때, 사용자에게 빠른 접근성을 보장해야한다.
- 특히 여러 지역에서 강의를 시청하는 사용자가 많을 경우, 파일 전송 속도와 안정성에 문제 발생 가능성이 있다.



### CDN 적용

- 파일을 여러 지역의 서버에 분산 저장하고, 사용자와 물리적으로 가까운 서버에서 빠르게 파일을 받을 수 있는
  CDN을 적용하기로 하였음
- 또한 HTTPS르 적용 시킬 수 있어 보안을 한층 강화할 수 있다는 장점이 있음


### 해결

- **JdbcPagingItemReader로 변경**  
  - 더티체킹 문제를 제거하며 추가적인 쿼리 발생을 방지했습니다.  
  - 결과적으로 데이터 처리 시간이 기존 4분 46초에서 **60초로 4.6배 개선**되었습니다.  



### 적용 후

- **90.88%의 영상 조회 성능 개선:** 기존 657.03ms → 60.28ms로 감소  
- **서버의 부하가 감소:** 요청이 CDN 서버로 분

</details>

<details>
<summary><b>✅ 스케쥴 Todo 수정 기능의 낙관적 락을 사용한 동시성 제어</b></summary>

### 동시성 제어 시 여러 선택지 중, 낙관적 락을 선택한 이유



#### 낙관적 락과 비관적 락의 선택지


- **비관적 락**  
  비관적 락으로 데이터를 조회하면 해당 트랜잭션이 끝나기 전까지는 데이터에 대한 Insert 작업이 불가능
  - 단점: 트래픽이 많은 경우 성능 저하 발생 및 타임아웃 문제.  

- **낙관적 락**  
  낙관적 락은 충돌 발생 시 롤백 처리를 요구하며, 충돌 비용이 높음  
  - 단점: CPU 점유율이 상승하고, 예상치 못한 오류 발생 가능.  



#### 낙관적 락을 사용한 이유

[요구사항]
PWT(Project With Tutor) 에서 해당 부트캠프 진행자(튜터)와 멘티(일반유저)들이 구매를 통해 모이게 되면, ScheduleBoard가 생성이 되고, 그 안에서 ScheduleTodo로 일정을 관리할 수 있다.
이 때,

- 튜터는 일반 유저의 Todo를 수정할 수 있고,
- 일반 유저는 본인의 Todo만 수정이 가능하다.

1. 비관적 락은 동시성 제어에 있어 유리하다. 하지만 데드락으로 인한 성능 저하를 발생 시킬 수 있다는 단점도 존재
2. 현재 상황에서 볼 때, 최대 두명의 유저(튜터1,유저1)에서 데이터 충돌이 날 가능성이 있음 => 동시성 문제가 발생할 가능성이 비교적 낮음

**결론** : 충돌 가능성이 상대적으로 높은 상황에서는 비관적 락을 사용하되, **아닌 경우**에는 **낙관적 락을 유지하는 방식**이 고려하는 것이 좋을 것이라고 판단




### 적용 후

- 전후 데이터 비교
- [이미지첨부]
  낙관적 락 적용 전:
실제 업데이트 성공 횟수가 두개로 나오며 테스트는 실패했다. 이는 수정요청이 한번만 성공 되었어야했는데,  두명의 사용자가 동시에 수정에 성공했음에도 불구하고 최종적으로는 한 사람의 수정 결과로만 덮어씌워졌음을 나타낸다.

낙관적 락 적용 후: 
예외 발생 수가 1 이상인것을 보아, 한 명의 사용자가 데이터 수정에 성공했고, 다른 한 명은 충돌로 인해 수정에 실패했음을 알 수 있다. 이를 통해 데이터 손상이 발생하지 않고 최종 데이터가 일관성을 유지하게 된다.

[회고]
- 기술의 장단점
낙관적 락을 통해 동시에 수정을 하려는 동시성 문제를 해결 할 수 있음을 확인 할 수 잇었고, 이를 통해 효과적으로 데이터 충돌 가능성이 낮은 환경에서도 데이터의 일관성 및 충돌을 적절히 손보고 있다는 점을 알 수 있었다.
하지만 좀 더 효과적인 동시성 제어를 위해서는 비관적락을 적용하는 방법도 고려해보아야한다고 생각한다.
- 다시 시도한다면?
예상 데이터 충돌이 더 많은 환경에서 낙관적 락이 아닌 비관적락을 사용해보고싶다.

</details>



<details>
<summary><b>✅ 캐싱 무효화 </b></summary>



#### 기술 선택지

- **AWS lamda로 캐시 무효를 자동화**
  1. 장점 : 트리거를 활용해 캐시 무효화를 자동화 할 수 있으며, 서버 로드 부담 감소
  2. 단점 : AWS CloudFront 캐싱 무효화 비용 + 람다 함수 실행 비용
  
- **AWS CloudFront 에서 캐시 무효화 경로 지정**
  1. 장점 : 간단함
  2. 단점 : AWS에 들어와서 경로를 직접 하나씩 생성해줘야함

- **Java용 AWS SDK 로 캐시 무효화 요청**
  1. 장점 : lamda사용하는 것보다 빠르며 람다 함수 실행 비용 X
  2. 단점 : 서버 로드 부담 증
 


#### Java용 AWS SDK 로 캐시 무효화 방식 채택

- **AWS Lamda**  
  - CloudFront 캐시 무효화 비용에 함수 실행 비용까지 감당해야함

- **JAVA 용 AWS SDK**
  - CloudFront 캐시 무효화 비용만 발생
  - 현재 프로젝트 로직에 유연하게 통합 가능
  - 정 조건에서 CloudFront 캐시 무효화 요청을 프로그래밍적으로 수행하여 운영 효율성을 개선가능


### 적용 후

- **캐시 무효화 자동화** 구현
- **비용절감**

</details>

<br></br>
## 역할 분담 및 협업 방식

### **Detail Role**

<table>
  <thead>
    <tr>
      <th style="text-align:center">이름</th>
      <th style="text-align:center">포지션</th>
      <th style="text-align:center">담당(개인별 기여점)</th>
      <th style="text-align:center">Github 링크</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="text-align:center">
        <a href="https://github.com/ks12467">
          <img src="https://avatars.githubusercontent.com/u/137591102?v=4" height="150px" width="150px" alt=""/><br />
          <span style="font-size:240px; font-weight:bold; display:block; margin-top:10px;"><b>🐼팀장 : 김현수</b></span>
        </a>
      </td>
<td style="text-align:center">
      리더</td>
      <td style="text-align:left">
        ▶ <b>Auth</b>: LOCAL 회원가입 / 로그인 / 회원탈퇴 ,SOCIAL KakaoLogin 기능 구현<br>
        ▶ <b>Search</b>: 통합 검색 기능 (Redis cache 적용, QueryDSL 전환, Elasticsearch 검색 엔진 구현)<br>
        ▶ <b>Slack notification</b>: 워크스페이스 및 채널 입장 알림, 개인 DM 알림 전송<br>
        ▶ <b>AOP 및 GlobalExceptionHandler</b>, <b>ELK Stack</b> 적용<br>
        ▶ <b>Prometheus, Grafana 적용</b>: 시스템 모니터링 구축
      </td>
      <td style="text-align:center">
        <a href="https://github.com/ks12467">🍁 깃헙링크</a>
      </td>
    </tr>
    <tr>
      <td style="text-align:center">
        <a href="https://github.com/ackrilll">
          <img src="https://avatars.githubusercontent.com/u/112237701?v=4" height="150px" width="150px" alt=""/><br />
          <span style="font-size:240px; font-weight:bold; display:block; margin-top:10px;"><b>🦊부팀장 : 남진현</b></span>
        </a>
      </td>
      <td style="text-align:center">부리더</td>
      <td style="text-align:left">
        ▶ <b>유저</b>: 프로필 조회, 이미지 생성 및 변경<br>
        ▶ <b>첨부파일</b>: 업로드, 응답 속도 개선, CDN 캐싱 무효화 적용<br>
        ▶ <b>결제</b>: PWT 결제 동시성 제어
      </td>
      <td style="text-align:center">
        <a href="https://github.com/ackrilll">🍁 깃헙링크</a>
      </td>
    </tr>
    <tr>
      <td style="text-align:center">
        <a href="https://github.com/kang-sumin">
          <img src="https://avatars.githubusercontent.com/u/54437758?v=4" height="150px" width="150px" alt=""/><br />
          <span style="font-size:240px; font-weight:bold; display:block; margin-top:10px;"><b>🐶팀원 : 강수민</b></span>
        </a>
      </td>
      <td style="text-align:center">팀원</td>
      <td style="text-align:left">
            ▶ <b>튜터 신청 서비스</b>: CRUD 기능 구현<br>
            ▶ <b>PWT</b>: CRUD 구현, 권한별 서비스 지정<br>
            ▶ <b>주문 시스템</b>: 장바구니 CRUD, 재고 관리 테이블화<br>
            ▶ <b>결제 시스템</b>: PG 연동, 결제 트랜잭션 구현<br>
            ▶ <b>CI/CD</b>: Jenkins pipeline 구축, ALB 적용
      </td>
      <td style="text-align:center">
        <a href="https://github.com/kang-sumin">🍁 깃헙링크</a>
      </td>
    </tr>
    <tr>
      <td style="text-align:center">
        <a href="https://github.com/jiyumi00">
          <img src="https://avatars.githubusercontent.com/u/101707266?v=4" height="150px" width="150px" alt=""/><br />
          <span style="font-size:240px; font-weight:bold; display:block; margin-top:10px;"><b>🐹팀원 : 정지윤</b></span>
        </a>
      </td>
      <td style="text-align:center">팀원</td>
      <td style="text-align:left">
           ▶ <b>스터디파티</b>: 스터디파티 CRUD 기능 구현, 스터디 파티 댓글 CRUD<br>
            ▶ <b>인터넷 강의</b>: CRUD 기능 구현, AWS Multipart Upload 적용, CDN & Signed URL 적용<br>
            ▶ <b>AWS</b>: VPC 서브넷 EC2 라우팅 테이블 ELB RDS 구성, EC2 인스턴스 SpringBoot 배포
      </td>
      <td style="text-align:center">
        <a href="https://github.com/jiyumi00">🍁 깃헙링크</a>
      </td>
    </tr>
    <tr>
      <td style="text-align:center">
        <a href="https://github.com/goodsoundisme">
          <img src="https://avatars.githubusercontent.com/u/117337891?v=4" height="150px" width="150px" alt=""/><br />
          <span style="font-size:240px; font-weight:bold; display:block; margin-top:10px;">
        🐰팀원 : 조은솔
      </span>
        </a>
      </td>
      <td style="text-align:center">팀원</td>
      <td style="text-align:left">
        ▶ <b>개발자 커뮤니티</b>: 커뮤니티 CRUD 구현, JPQL 최적화<br>
        ▶ <b>캘린더 대시보드</b>: PWT 게시글 승인 후 대시보드 생성 로직<br>
        ▶ <b>스케줄 보드 지급</b>: PWT 결제 완료 시 권한 지급<br>
        ▶ <b>캘린더 Todo</b>: CRUD, 낙관적 락 적용<br>
        ▶ <b>부하 테스트</b>: JMeter로 성능 테스트 진행
      </td>
      <td style="text-align:center">
        <a href="https://github.com/goodsoundisme">🍁 깃헙링크</a>
      </td>
    </tr>
  </tbody>
</table>

<br></br>
### **Ground Rule**

🍁 **하루 2번 데일리 스크럼 10:00 ~ 11:00)(17:00 ~ 18:00)**  

🍁 **무조건 Pull 받고 Pull Request 진행**  

🍁 **Pull Request 진행할 때 코드 리뷰도 같이 진행**  

🍁 **Pull Request 승인 인원은 최소 2명 이상**  

🍁 **환경 변수 항상 업데이트 & 슬랙에서 관리**  

🍁 **회의 전 질문 및 보고 정리해서 5분 보드 & 슬랙에 정리**  

🍁 **자리 비울 때 상태 '자리비움'으로 전환** 

🍁 **예쁘고 둥글게 말하기** 

🍁 **무조건 캠 켜고 진행하기** 

<br></br>
## 시스템 구조도
![image](https://github.com/user-attachments/assets/11be2eda-ce2e-461c-a9b2-de2494cb29d5)

## 시스템 WorkFlow
<details>
    <summary><b>⚙️ 회원 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/990c07c2-65d4-49da-95ea-841f2fc90b11">
    </div>
</details>

<details>
    <summary><b>⚙️ PWT 게시판 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/bdd73c08-ca3c-4659-8381-5cc9162ab234">
    </div>
</details>
<details>
    <summary><b>⚙️ 인터넷 강의 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/7ad05850-a73e-46ef-bf7a-c1c3010ded00">
    </div>
</details>
<details>
    <summary><b>⚙️ 개발자 커뮤니티 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/044e1b18-7b49-4fda-8fdc-3e74f1a2dc72">
    </div>
</details>
 <details>
    <summary><b>⚙️ 스터디 파티 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/341d743a-5489-4e45-9506-b31adfb5edce">
    </div>
</details>
 <details>
    <summary><b>⚙️ 주문 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/10022868-237d-4097-baa9-3dfcf28c204d">
    </div>
</details>
 <details>
    <summary><b>⚙️ 캘린더 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/92c60ab4-3f65-4b9e-b30e-156e8689c112">
    </div>
</details>
 <details>
    <summary><b>⚙️ 검색 API </b></summary>
    <div>
        <img src="https://github.com/user-attachments/assets/4c6c3c00-bfbf-4ae2-94e2-0eacfbff9f90">
    </div>
</details>

<br></br>
## ✨성과 및 회고

### 🤗잘 진행 된 점

- 이미지 캐싱을 통한 페이지 로드 시간 단축(CDN 적용)
- 매일 스크럼 및 회의를 통해 문제를 빠르게 공유하고, 적극적으로 해결
- 검색 성능 향상과 다양한 조건으로 검색이 가능한 부분

<aside>

### 🤔아쉬운 점

- 시간 부족으로 일부 기능의 성능향상을 이루지 못한 점
- 결제 시스템에 분산 락을 사용하여 성능 개선 시키지 못한 점
- 로그를 이용한 Elasticsearch 동기화 실패
</aside>

<aside>


### 🚀향후 계획

- 기술적 고도화
    - 주문 시스템 비동기 처리
        - 주문 시스템을 비동기 방식으로 전환하여 처리 속도를 개선하고 병목 현상을 완화하고, 이를 통해 대량 트래픽 상황에서도 안정적인 시스템 성능을 보장하고자 함.
    - 결제 트랜잭션 분산락 적용
        - 트랜잭션 충돌 및 동시 처리 문제를 해결하기 위해 분산락 기술을 적용시켜 결제 프로세스의 안전성과 일관성을 강화
    - Kafka 기반 이벤트 처리
        - Kafka를 활용한 이벤트 처리로 서버간 메시지 큐 기반의 비동기 통신을 구현하고, 효율적인 MSA 구조를 구축하고자 함. 이를 통해 시스템 간의 의존성을 줄이고 확장성을 강화할 예정
    - MSA 구조 전환
        - 모놀리스아키텍쳐 구조가 아닌 MSA 구조로 전환
- 테스트
    - 기존 단위 테스트 외에 통합 테스트를 통해 안정성을 더욱 강화
    - JMeter등을 사용한 부하 테스트를 추가하여, 진행한 기술적 고도화 이후의 성능이 실제로 어떻게 향상되었는지 확인
</aside>

