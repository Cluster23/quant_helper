from manager_agent import ManagerAgent
from news_agent import NewsAgent
from price_agent import PriceAgent
from trading_rule_agent import TradingRoleAgent
import dotenv
import os
import autogen
from autogen import config_list_from_json
from autogen.agentchat.contrib.gpt_assistant_agent import GPTAssistantAgent
from autogen.function_utils import get_function_schema
from tools import assistant_config
from autogen import ConversableAgent

dotenv.load_dotenv()

def main():

    # news_agent = NewsAgent(llm_config=llm_config, api_key=os.environ["NAVER_API_KEY"])
    # price_agent = PriceAgent(llm_config=llm_config)
    # trading_role_agent = TradingRoleAgent(llm_config=llm_config, rules=["test rule", "test rule2"])
    # manager_agent = ManagerAgent(llm_config=llm_config)
    #
    # # GroupChat 및 GroupChatManager 초기화
    # groupChat = autogen.GroupChat(agents=[news_agent, price_agent, trading_role_agent, manager_agent], messages=[],max_round=10)
    # manager = autogen.GroupChatManager(groupchat=groupChat, llm_config=llm_config)

    # 사용자로부터 질문 입력 받기
    question = input("질문이 무엇인가요? : ")
    config_list = config_list_from_json("OAI_CONFIG_LIST")
    llm_config = {
        "config_list": config_list,
    }

    news_agent = GPTAssistantAgent(
        name="news_agent",
        instructions="I'm an openai assistant running in autogen",
        llm_config=llm_config,
        assistant_config=assistant_config,
    )

    manager_agent = ConversableAgent(
        name="manager_agent",
        system_message="you are a manager of Agents. you receive the question from user and make prompts to give answer. you should make prompts for each agents.",
        llm_config=llm_config,
        human_input_mode="ALWAYS",
    )

    # NewsAgent로부터 뉴스 분석 결과 가져오기
    # news_decision = news_agent.make_decision(question)
    # print(f"NewsAgent의 결정: {news_decision}")
    #
    # # PriceAgent로부터 현재 가격과 트렌드 라인 가져오기
    # current_price = price_agent.get_current_price(question)
    # trend_line = price_agent.calculate_trend_line(question)
    # print(f"현재 가격: {current_price}")
    # print(f"트렌드 라인: {trend_line[-1]}")  # 최근 트렌드 라인 값 출력
    #
    # # TradingRoleAgent가 트레이딩 규칙을 확인하고 메시지를 제공
    # trade_decision = news_decision  # 기본적으로 뉴스 분석 결과를 사용
    # rule_check = trading_role_agent.enforce_rules(trade_decision)
    # calming_message = trading_role_agent.calming_message()
    # print(f"TradingRoleAgent의 규칙 검사 결과: {rule_check}")
    # print(f"TradingRoleAgent의 안정 메시지: {calming_message}")
    #
    # # ManagerAgent가 최종 결정을 내림
    # opinions = [news_decision, trade_decision]  # 예시로 뉴스 결정과 트레이드 결정을 사용
    # final_decision = manager_agent.collect_opinions(opinions)
    # print(f"ManagerAgent의 최종 결정: {final_decision}")

    # 그룹 챗을 시작
    manager.start()


if __name__ == "__main__":
    main()
