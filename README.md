## What is QuantHelper
QuantHelper는 AI를 통해 주식 데이터를 분석하고 인사이트를 제공하는 주식 분석 시스템이다. 
Autogen을 이용한 Multi-Agent 구조로, 사용자 질문에 최적화된 결과를 생성한다.

<br>


## 동작 로직 및 에이전트 구성
1. 사용자가 질문을 입력하면, 이 질문은 User Proxy Agent로 전달된다.

2. User Proxy Agent는 질문을 Manager Agent에 넘기고, Manager Agent는 질문을 그룹챗 내에서 관리하며, 필요한 에이전트에게 순서대로 작업을 할당한다.

3. 각 에이전트는 Java Spring 서버의 API를 호출하여 필요한 데이터를 수집하고 분석한다:

   - News Agent는 종목과 관련된 최신 뉴스를 서버 API로부터 가져온다.
   - Stock Agent는 주가, 이동평균선, 시가, 종가 등의 정보를 API를 통해 수집한다.
   - Financial Statement Agent는 PER, ROE 등 재무 관련 정보를 API 호출로 수집한다.
   - 수집한 정보는 그룹챗 내에서 공유되며, Conclusion Agent는 모든 정보를 종합해 사용자에게 전달할 결론을 생성한다.

4. Prompt Agent는 각 에이전트에게 명령 프롬프트를 전달하여 그룹챗이 원활히 진행되도록 한다.

![image](https://github.com/user-attachments/assets/d48b36ec-1abd-4318-be26-c29c8ce8c7c9)



<br>

## 서버 구성
Java Spring 서버는 

- 한국투자증권 오픈 API
- 전자공시 시스템

을 통해 최신 주가 및 재무 데이터를 수집하며, 에이전트들이 이를 활용해 분석 결과를 제공한다.

QuantHelper는 에이전트 협업을 통해 종합적이고 유용한 주식 정보를 제공하는 AI 기반 주식 분석 시스템이다.

<br>

## 시스템 아키텍처
![image](https://github.com/user-attachments/assets/f9b0bff6-96a0-49a7-a180-23111b9a8592)


