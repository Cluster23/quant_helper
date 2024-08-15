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
            overwrite_instructions=True,
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
                overwrite_instructions=True,
                instructions=assistant.instructions,
            )
            return conclusion_agent

    if not found:
        # 새 GPTAssistantAgent 생성
        conclusion_agent = GPTAssistantAgent(
            name="conclusion_agent",
            instructions="You are a manager of Agents. " +
                         "You receive the question from user and make prompts to give answer. You should make prompt if you need any information from other agents to make answer. " +
                         "There are three agents: news_agent, stock_agent, financial_statement_agent",
            llm_config=llm_config,
            overwrite_instructions=True,
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
            instructions="You are a financial assistant specialized in analyzing and providing detailed stock information. " +
                         "Your task is to analyze stock-related queries by referencing key data points such as the current price, " +
                         "52-week high and low, today's opening and closing prices, and moving averages for the past 5, 20, and 60 days. " +
                         "When responding to queries, utilize the functions 'getStockPrice' and 'getStockInfo' to retrieve up-to-date stock data. " +
                         "Use this data to provide logical and well-reasoned answers. " +
                         "If a user inquires about the performance or outlook of a specific stock, base your response on the relevant data " +
                         "and consider market trends, historical performance, and other relevant financial indicators. " +
                         "Your goal is to deliver accurate, data-driven insights that help users make informed decisions regarding their stock investments.",
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
            instructions="You are a financial analyst assistant specialized in interpreting and explaining financial statements of corporations. " +
                         "Your task is to analyze queries related to corporate financial health using key data points such as current assets, non-current assets, " +
                         "total assets, current liabilities, non-current liabilities, total liabilities, and equity. " +
                         "You should also consider the income statement data including revenue, operating profit, and net income. " +
                         "When responding to queries, utilize the function 'getFinancialStatement' to retrieve the latest financial data and compare it with previous periods' data. " +
                         "Use this data to provide insightful, data-driven analysis and help users understand the financial condition and performance of a company. " +
                         "Ensure to present trends and significant changes between reporting periods to offer a clear picture of the company's financial trajectory.",
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

def get_question_agent(llm_config):
    return ConversableAgent(
        name="question_agent",
        llm_config=llm_config,
        human_input_mode="NEVER",
        system_message="You are an AI that answers the user's questions. When a user asks a question, " +
                       "forward the question to the manager_agent and evaluate whether the response is accurate. " +
                       "If the response is not accurate, resend the original question to the manager_agent and instruct it to provide a more precise answer.",
    )