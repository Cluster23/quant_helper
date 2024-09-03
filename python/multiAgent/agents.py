import dotenv
import os
import autogen
from autogen import config_list_from_json
from autogen.agentchat.contrib.gpt_assistant_agent import GPTAssistantAgent
from tools import getNews
from tools import getNews_api_schema
from tools import getStockPrice
from tools import getStockPrice_api_schema
from tools import getStockInfo
from tools import getStockInfo_api_schema
from tools import getFinancialStatement
from tools import getFinancialStatement_api_schema
from autogen import ConversableAgent
from autogen import UserProxyAgent
from openai import OpenAI
import datetime

def get_user_proxy_agent():
    user_proxy_agent = UserProxyAgent(
        name="user_proxy_agent",
        code_execution_config=False,
        llm_config=None,
        human_input_mode="ALWAYS"
    )
    return user_proxy_agent

def get_news_agent(llm_config, my_assistants):
    found = False

    for assistant in my_assistants:
        if assistant.name == "news_agent":
            found = True
            news_agent = GPTAssistantAgent(
                name=assistant.name,
                llm_config=llm_config,
                assistant_config={
                    "assistant_id": assistant.id,
                    "tools": [
                        {
                            "type": "function",
                            "function": getNews_api_schema,
                        }
                    ]
                },
                overwrite_instructions=True,
                instructions=assistant.instructions,
            )
            news_agent.register_function(
                function_map={
                    "getNews": getNews,
                }
            )
            return news_agent

    if not found:
        # 새 GPTAssistantAgent 생성
        news_agent = GPTAssistantAgent(
            name="news_agent",
            llm_config=llm_config,
            assistant_config={
                "tools": [
                    {
                        "type": "function",
                        "function": getNews_api_schema,
                    }
                ]
            },
            description="The agent that analysis news",
            overwrite_instructions=True,
            instructions="당신은 주제를 기반으로 뉴스를 검색하고 관련 정보를 제공하는 도우미입니다. " +
                         "함수를 사용하여 뉴스 정보를 검색하세요. " +
                         "getNews 함수를 호출하면 검색 결과를 포함한 JSON 객체를 얻을 수 있습니다. " +
                         "뉴스 제목을 분석하여 주식의 상태가 긍정적인지 부정적인지 파악하세요. " +
                         "분석을 마친 후에, 추가적으로 필요한 정보가 있다면 알려주세요. 추가 정보가 필요하지 않다면 응답에 포함시키지 않아도 됩니다. " +
                         "예를 들어, '삼성전자'에 대한 검색 결과에 '삼성전자, SK 하이닉스와의 주가 경쟁중'이라는 뉴스가 포함되어 있으면, SK 하이닉스에 대한 추가 검색이 필요할 수 있습니다. " +
                         "결과 출력 시, 분석 후에는 '추가적으로 필요한 정보: ' 형식으로 출력해 주세요. 모든 과정은 반드시 한국어로 진행되어야 합니다."
        )
        news_agent.register_function(
            function_map={
                "getNews": getNews,
            }
        )
    return news_agent

def get_conclusion_agent(llm_config, my_assistants):
    found = False
    for assistant in my_assistants:
        if assistant.name == "conclusion_agent":
            found = True
            conclusion_agent = GPTAssistantAgent(
                name=assistant.name,
                llm_config=llm_config,
                assistant_config={
                    "assistant_id": assistant.id,
                },
                description="The agent that summarize information from other agents at the last sequence",
                overwrite_instructions=True,
                instructions=assistant.instructions,
            )
            return conclusion_agent

    if not found:
        # 새 GPTAssistantAgent 생성
        conclusion_agent = GPTAssistantAgent(
            name="conclusion_agent",
            instructions="지금까지 그룹에서 한 대화 내용을 정리해서 ",
            llm_config=llm_config,
            overwrite_instructions=True,
            description="The agent that summarize information from other agents at the last sequence",
        )
    return conclusion_agent

def get_stock_agent(llm_config, my_assistants):
    found = False
    for assistant in my_assistants:
        if assistant.name == "stock_agent":
            found = True
            stock_agent = GPTAssistantAgent(
                name=assistant.name,
                llm_config=llm_config,
                assistant_config={
                    "assistant_id": assistant.id,
                    "tools": [
                        {
                            "type": "function",
                            "function": getStockPrice_api_schema,
                        },
                        {
                            "type": "function",
                            "function": getStockInfo_api_schema,
                        }
                    ]
                },
                overwrite_instructions=True,
                instructions=assistant.instructions,
            )
            stock_agent.register_function(
                function_map={
                    "getStockPrice": getStockPrice,
                    "getStockInfo": getStockInfo,
                }
            )
            return stock_agent

    if not found:
        # 새 GPTAssistantAgent 생성
        stock_agent = GPTAssistantAgent(
            name="stock_agent",
            instructions="당신은 주식 정보를 분석하고 제공하는 데 전문화된 금융 도우미입니다. " +
                         " 당신의 업무는 주식의 현재 가격, 52주 최고 및 최저가, 오늘의 시가 및 종가, 지난 5일, 20일, 60일 간의 이동 평균 등의 주요 데이터를 토대로 질문에 대답하는 것입니다. " +
                         " 주가 정보가 필요하다면 'getStockPrice' 및 'getStockInfo' 함수를 사용하여 최신 주식 데이터를 가져오세요. " +
                         "" +
                         "이 데이터를 활용하여 논리적이고 잘 정리된 답변을 제공하세요. ",
            llm_config=llm_config,
            assistant_config={
                "tools": [
                    {
                        "type": "function",
                        "function": getStockPrice_api_schema,
                    },
                    {
                        "type": "function",
                        "function": getStockInfo_api_schema,
                    }
                ]
            },
            overwrite_instructions=True,
        )
        stock_agent.register_function(
            function_map={
                "getStockPrice": getStockPrice,
                "getStockInfo": getStockInfo,
            }
        )
    return stock_agent

def get_financial_statement_agent(llm_config, my_assistants):
    found = False
    for assistant in my_assistants:
        if assistant.name == "financial_statement_agent":
            found = True
            financial_statement_agent = GPTAssistantAgent(
                name=assistant.name,
                llm_config=llm_config,
                assistant_config={
                    "assistant_id": assistant.id,
                    "tools": [
                        {
                            "type": "function",
                            "function": getFinancialStatement_api_schema
                        }
                    ]
                },
                overwrite_instructions=True,
                instructions=assistant.instructions,
            )
            financial_statement_agent.register_function(
                function_map={
                    "getFinancialStatement": getFinancialStatement,
                }
            )
            return financial_statement_agent

    if not found:
        # 새 GPTAssistantAgent 생성
        financial_statement_agent = GPTAssistantAgent(
            name="financial_statement_agent",
            instructions="당신은 기업의 재무제표를 해석하고 설명하는 데 전문화된 금융 분석 도우미입니다. " +
                         " 당신의 업무는 현재 자산, 비유동 자산, 총 자산, 유동 부채, 비유동 부채, 총 부채, 자본 등 주요 데이터를 사용하여 질문에 논리적으로 대답하는 것입니다. " +
                         " 'getFinancialStatement' 함수를 사용하여 최신 재무 데이터를 가져오고 이를 이전 기간의 데이터와 비교하세요. " +
                         " 이 데이터를 활용하여 통찰력 있는 데이터 기반 분석을 제공하고 사용자가 회사의 재무 상태와 성과를 이해하도록 도와주세요. " +
                         " 보고 기간 간의 추세와 주요 변화를 명확히 제시하여 회사의 재무적 경향을 분명히 보여주세요." +
                         " 만약, 당신이 가지고 있지 않은 정보를 요구 한다면, 어떤 정보가 추가로 필요한지 명확하게 알려주세요",
            llm_config=llm_config,
            assistant_config={
                "tools": [
                    {
                        "type": "function",
                        "function": getFinancialStatement_api_schema,
                    }
                ]
            },
            overwrite_instructions=True,
        )
        financial_statement_agent.register_function(
            function_map={
                "getFinancialStatement": getFinancialStatement,
            }
        )
    return financial_statement_agent

def get_prompt_agent(llm_config, rule):
    return ConversableAgent(
        name="prompt_agent",
        llm_config=llm_config,
        human_input_mode="NEVER",
        system_message="당신은 프롬프트를 생성하는 AI입니다. 사용자는 주식과 관련된 질문을 할 것 입니다. 사용자의 질문에 논리적으로 대답하기 위해, 다른 AI들에게 보낼 프롬프트를 생성 해야합니다." +
                       " 현재 날짜와 시간은 " + datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S") + " 입니다." +
                       " 당신은 여러개의 프롬프트를 생성할 수 있습니다." +
                       " 당신이 프롬프트를 보낼 수 있는 AI는 3종류가 존재합니다. " +
                       " 1. 주식 가격 AI(주식의 현재 가격, 이동평균선, 주가 추세 등을 알고있습니다.)" +
                       " 2. 뉴스 AI(주식과 관련된 뉴스 정보를 갖고 있습니다.)" +
                       " 3. 재무제표 AI(회사의 재무제표를 가지고 있습니다.)" +
                       " 사용자가 당신에게 주가 예측이나 주식의 성장성 등을 물어본다면, 해당 사용자가 정해놓은 투자의 규칙에 맞게 프롬프트를 생성하세요." +
                       rule +
                       " 예를 들어, 사용자는 다음과 같이 질문할 수 있습니다: 삼성전자의 현재 주가가 고평가 되었다고 생각해?" +
                       " 당신은 사용자의 투자 규칙에 맞춰서 AI들에게 보낼 프롬프트를 생성합니다." +
                       " 주식 명칭: 삼성전자" +
                       " 프롬프트 1. 삼성전자와 관련된 최신 뉴스를 검색하고 긍정적인 부분과 부정적인 평가를 분석해주세요" +
                       " 프롬프트 2. 삼성전자의 주가가 20일 이동평균선보다 위에 있는지 분석해주세요" +
                       " 프롬프트 3. 삼성전자의 재무제표를 분석해서 전달해주세요" +
                       " 위는 예시일 뿐입니다. 프롬프트를 반드시 3개 생성할 필요는 없습니다. 당신이 필요하다고 생각하는 것만 의뢰하세요" +
                       " 답변을 출력할 때는, 주식 명과 프롬프트만 출력하세요.",
    )

# def get_analysis_agent(llm_config, rule):
#     analysis_agent = ConversableAgent(
#         name="analysis_agent",
#         llm_config=llm_config,
#         human_input_mode="NEVER",
#         system_message="당신은 프롬프트를 받아서 도구를 실행한 뒤, 결과를 수집해서 논리적으로 정리하는 AI입니다." +
#                        " 당신이 사용할 수 있는 도구는 3가지가 있습니다."
#                        " 1. 뉴스를 검색하는 도구 - 뉴스와 관련된 프롬프트가 들어오면, 해당 프롬프트를 인수로 넘겨서 사용합니다."
#                        " 2. 주식의 가격, 정보를 분석하는 도구 - 주식의 가격, 또는 정보와 관련된 프롬프트가 들어오면, 해당 프롬프트를 인수로 넘겨서 사용합니다." +
#                        " 3. 재무제표 관련 정보를 얻는 도구 - 재무제표와 관련된 정보(PER, ROE, 주식의 발행 수 등)와 관련된 프롬프트가 들어오면, 해당 프롬프트를 인수로 넘겨서 사용합니다." +
#                        " 각각의 도구를 실행해서 얻은 결과를 하나로 종합하세요",
#     )
#     analysis_agent.register_for_llm(chat_about_news)
