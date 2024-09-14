import dotenv
import os
import autogen
from autogen import GroupChatManager
from autogen import GroupChat
from autogen.cache.in_memory_cache import InMemoryCache
from autogen.agentchat.contrib.capabilities import transform_messages, transforms
from openai import OpenAI
from agents import (
    get_news_agent,
    get_stock_agent,
    get_financial_statement_agent,
    get_question_agent,
    get_user_proxy_agent,
    get_prompt_agent,
)


dotenv.load_dotenv()
client = OpenAI()

OpenAI.api_key = os.environ.get("OPENAI_API_KEY")
rule = os.environ.get("RULE")

llm_config = {
    "config_list": [
        {"model": "gpt-4o-mini", "api_key": os.environ.get("OPENAI_API_KEY")}
    ]
}


def is_termination_msg(content) -> bool:
    have_content = content.get("content", None) is not None
    if have_content and "TERMINATE" in content["content"]:
        return True
    return False


# 이미 생성되어있는 Assistants
my_assistants = client.beta.assistants.list(order="desc", limit="20")


def main():
    # 사용자로부터 질문 입력 받기
    question = input("질문이 무엇인가요? : ")

    user_proxy_agent = get_user_proxy_agent()

    prompt_agent = get_prompt_agent(llm_config, rule)

    # question_agent 생성
    # question_agent = get_question_agent(llm_config, rule)

    # news_agent 가져오기 또는 생성
    news_agent = get_news_agent(llm_config, my_assistants)

    # stock_agent 가져오기 또는 생성
    stock_agent = get_stock_agent(llm_config, my_assistants)

    # financial_statement_agent 가져오기 또는 생성
    financial_statement_agent = get_financial_statement_agent(llm_config, my_assistants)

    # select_speaker_compression_args = dict(
    #     model_name="microsoft/llmlingua-2-xlm-roberta-large-meetingbank",
    #     use_llmlingua2=True,
    #     device_map="cpu",
    # )

    # select_speaker_transforms = transform_messages.TransformMessages(
    #     transforms=[
    #         transforms.MessageHistoryLimiter(max_messages=10),
    #         transforms.TextMessageCompressor(
    #             min_tokens=1000,
    #             text_compressor=transforms.LLMLingua(
    #                 select_speaker_compression_args, structured_compression=True
    #             ),
    #             cache=InMemoryCache(seed=23),
    #             filter_dict={
    #                 "role": ["system"],
    #                 "name": ["news_agent", "stock_agent", "financial_statement_agent"],
    #             },
    #             exclude_filter=True,
    #         ),
    #         transforms.MessageTokenLimiter(
    #             max_tokens=3000, max_tokens_per_message=500, min_tokens=300
    #         ),
    #     ]
    # )
    speaker_transitions_dict = {
        news_agent: [prompt_agent],
        stock_agent: [prompt_agent],
        financial_statement_agent: [prompt_agent],
        prompt_agent: [news_agent, stock_agent, financial_statement_agent],
    }
    group_chat = GroupChat(
        agents=[news_agent, stock_agent, financial_statement_agent, prompt_agent],
        messages=[],
        send_introductions=True,
        allowed_or_disallowed_speaker_transitions=speaker_transitions_dict,
        speaker_transitions_type="allowed",  # 위의 allowed_or_disallowed_speaker_transitions가 허용조건인지 제한조건인지 설정
        max_round=12,
    )

    manager_agent = GroupChatManager(
        name="manager_agent",
        groupchat=group_chat,
        # is_termination_msg=is_termination_msg,
        system_message="당신은 주식 관련 질문에 응답하기 위해서 다른 에이전트와 협업하는 AI입니다.",
        llm_config={
            "config_list": [
                {"model": "gpt-4o-mini", "api_key": os.environ["OPENAI_API_KEY"]}
            ]
        },
    )

    # prompts = user_proxy_agent.initiate_chat(prompt_agent,
    #                                          messages=question,
    #                                          max_turns=1,
    #                                          summary_method="last_msg",
    #                                         )

    result = user_proxy_agent.initiate_chat(
        manager_agent,
        message=question,
        summary_method="reflection_with_llm",
        max_turns=1,
    )
    print("Group chat result:")
    for message in group_chat.messages:
        # Each message contains agent name and text they sent
        print(message["name"])
    print("result:", result)


if __name__ == "__main__":
    main()
