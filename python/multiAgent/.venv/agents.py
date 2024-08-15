import dotenv
import os
import autogen
from autogen import config_list_from_json
from autogen.agentchat.contrib.gpt_assistant_agent import GPTAssistantAgent
from tools import getNews
from tools import getNews_api_schema
from autogen import ConversableAgent
from openai import OpenAI

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
            instructions="You are an assistant which retrieves query and searches news with the query. Use function to retrieve news information. If you call getNews function, you can get a JSON object which includes the result of searching. Analyze news titles and figure out that status for stock is positive or negative."
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
                instructions=assistant.instructions,
            )
            return conclusion_agent

    if not found:
        # 새 GPTAssistantAgent 생성
        conclusion_agent = GPTAssistantAgent(
            name="conclusion_agent",
            instructions="You are a manager of Agents. " +
                         "You receive the question from user and make prompts to give answer. You should make prompt if you need any information from other agents to make answer. " +
                         "There is only one agent now. The News Agent.",
            llm_config=llm_config,
        )
    return conclusion_agent

def get_question_agent(llm_config):
    return ConversableAgent(
        name="question_agent",
        llm_config=llm_config,
        human_input_mode="NEVER",
        system_message="You are an AI that answers the user's questions. When a user asks a question, " +
                       "forward the question to the manager_agent and evaluate whether the response is accurate. " +
                       "If the response is not accurate, resend the original question to the manager_agent and instruct it to provide a more precise answer.",
    )