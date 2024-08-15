import dotenv
import os
import autogen
from autogen import GroupChatManager
from autogen import GroupChat
from openai import OpenAI
from agents import get_news_agent, get_conclusion_agent, get_stock_agent, get_financial_statement_agent, get_question_agent

dotenv.load_dotenv()
client = OpenAI()

OpenAI.api_key = os.environ.get("OPENAI_API_KEY")
llm_config = {"config_list": [{"model": "gpt-4o-2024-08-06", "api_key": os.environ.get("OPENAI_API_KEY")}]}

# 이미 생성되어있는 Assistants
my_assistants = client.beta.assistants.list(order="desc", limit="20")

def main():
    # 사용자로부터 질문 입력 받기
    question = input("질문이 무엇인가요? : ")

    # news_agent 가져오기 또는 생성
    news_agent = get_news_agent(llm_config, my_assistants)

    # conclusion_agent 가져오기 또는 생성
    conclusion_agent = get_conclusion_agent(llm_config, my_assistants)

    # stock_agent 가져오기 또는 생성
    stock_agent = get_stock_agent(llm_config, my_assistants)

    # financial_statement_agent 가져오기 또는 생성
    financial_statement_agent = get_financial_statement_agent(llm_config, my_assistants)

    # question_agent 생성
    question_agent = get_question_agent(llm_config)

    group_chat = autogen.GroupChat(
        agents=[news_agent, conclusion_agent, stock_agent, financial_statement_agent],
        messages=[],
        send_introductions=True,
        max_round=3,
    )

    manager_agent = GroupChatManager(
        name="manager_agent",
        groupchat=group_chat,
        llm_config={"config_list": [{"model": "gpt-4o-2024-08-06", "api_key": os.environ["OPENAI_API_KEY"]}]},
    )

    # 그룹 챗을 시작
    result = question_agent.initiate_chat(manager_agent, message=question)
    print(result)

if __name__ == "__main__":
    main()