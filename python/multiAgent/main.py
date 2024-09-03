import dotenv
import os
import autogen
from autogen import GroupChatManager
from autogen import GroupChat
from openai import OpenAI
from agents import (
    get_news_agent,
    get_stock_agent,
    get_financial_statement_agent,
    get_user_proxy_agent,
    get_prompt_agent,
)


dotenv.load_dotenv()
client = OpenAI()

OpenAI.api_key = os.environ.get("OPENAI_API_KEY")
rule = os.environ.get("RULE")

llm_config = {"config_list": [{"model": "gpt-4o-mini", "api_key": os.environ.get("OPENAI_API_KEY")}]}

# 이미 생성되어있는 Assistants
my_assistants = client.beta.assistants.list(order="desc", limit="20")


"""
    version2 
    : user-proxy,  group chat 만을 사용하는 형태
    group chat에는 총 5개 agent가 존재
    1. 정보
    2.
    3.
    4.
    5.
"""
def main():
    # 사용자로부터 질문 입력 받기
    question = input("질문이 무엇인가요? : ")

    user_proxy_agent = get_user_proxy_agent()

    prompt_agent = get_prompt_agent(llm_config, rule)

    # news_agent 가져오기 또는 생성
    news_agent = get_news_agent(llm_config, my_assistants)

    # stock_agent 가져오기 또는 생성
    stock_agent = get_stock_agent(llm_config, my_assistants)

    # financial_statement_agent 가져오기 또는 생성
    financial_statement_agent = get_financial_statement_agent(llm_config, my_assistants)



    group_chat = autogen.GroupChat(
        agents=[news_agent, stock_agent, financial_statement_agent, prompt_agent],
        messages=[],
        send_introductions=True,
        max_round=8,
    )

    manager_agent = GroupChatManager(
        name="manager_agent",
        groupchat=group_chat,
        system_message="당신은 주식 관련 질문에 응답하기 위해서 다른 에이전트와 협업하는 AI입니다.",
        llm_config={"config_list": [{"model": "gpt-4o-mini", "api_key": os.environ["OPENAI_API_KEY"]}]}
    )

    # prompts = user_proxy_agent.initiate_chat(prompt_agent,
    #                                          messages=question,
    #                                          max_turns=1,
    #                                          summary_method="last_msg",
    #                                         )

    result = user_proxy_agent.initiate_chat(manager_agent,
                                          message=question,
                                          summary_method="reflection_with_llm",
                                          max_turns=1)
    print("result:", result)

if __name__ == "__main__":
    main()