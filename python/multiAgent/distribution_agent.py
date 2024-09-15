from autogen import ConversableAgent
from autogen import UserProxyAgent
from autogen.agentchat.contrib.gpt_assistant_agent import GPTAssistantAgent
from openai import OpenAI
from agents import (
    get_news_agent,
    get_stock_agent,
    get_financial_statement_agent,
    get_user_proxy_agent,
)
import dotenv
import os

dotenv.load_dotenv()
client = OpenAI()

OpenAI.api_key = os.environ.get("OPENAI_API_KEY")

llm_config = {"config_list": [{"model": "gpt-4o-mini", "api_key": os.environ.get("OPENAI_API_KEY")}]}

# 이미 생성되어있는 Assistants
my_assistants = client.beta.assistants.list(order="desc", limit="20")

proxy = get_user_proxy_agent() # llm이 들어가있지 않음

# news_agent 가져오기 또는 생성
news_agent = get_news_agent(llm_config, my_assistants)

# stock_agent 가져오기 또는 생성
stock_agent = get_stock_agent(llm_config, my_assistants)

# financial_statement_agent 가져오기 또는 생성
financial_statement_agent = get_financial_statement_agent(llm_config, my_assistants)

def get_distribution_agent(llm_config):
    distribution_agent = ConversableAgent(
        name="analysis_agent",
        llm_config=llm_config,
        human_input_mode="NEVER",
        system_message="당신은 프롬프트를 받아서 분배하는 AI입니다." +
                       " 당신이 사용할 수 있는 도구는 3가지가 있습니다." +
                       " 1. 뉴스 에이전트에게 프롬프트를 보내는 도구 - 뉴스와 관련된 프롬프트가 들어오면, 해당 프롬프트를 인수로 넘겨서 사용합니다." +
                       " 2. 주식 에이전트에게 프롬프트를 보내는 도구 - 주식의 가격, 또는 정보와 관련된 프롬프트가 들어오면, 해당 프롬프트를 인수로 넘겨서 사용합니다." +
                       " 3. 재무제표 에이전트에게 프롬프트를 보내는 - 재무제표와 관련된 정보(PER, ROE, 주식의 발행 수 등)와 관련된 프롬프트가 들어오면, 해당 프롬프트를 인수로 넘겨서 사용합니다." +
                       " 각각의 도구를 실행해서 얻은 결과를 모두 그대로 응답해주세요",
    )
    distribution_agent.register_for_llm(name="chat_news_agent", description="chat with news agent")(chat_news_agent)
    distribution_agent.register_for_llm(name="chat_stock_agent", description="chat with stock agent")(chat_stock_agent)
    distribution_agent.register_for_llm(name="chat_financial_statement_agent", description="chat with financial statement agent")(chat_financial_statement_agent)

def chat_news_agent(prompt: str) -> str:
    return proxy.initiate_chat(news_agent,
                                message=prompt,
                                summary_method="reflection_with_llm",
                                max_turns=1)

def chat_stock_agent(prompt: str) -> str:
    return proxy.initiate_chat(news_agent,
                                message=prompt,
                                summary_method="reflection_with_llm",
                                max_turns=1)

def chat_financial_statement_agent(prompt: str) -> str:
    return proxy.initiate_chat(news_agent,
                                message=prompt,
                                summary_method="reflection_with_llm",
                                max_turns=1)


