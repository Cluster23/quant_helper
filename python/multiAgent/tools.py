from autogen.function_utils import get_function_schema
from spring_api_functions import call_news
from spring_api_functions import call_stock_price
from spring_api_functions import call_stock_info
from spring_api_functions import call_financial_statement




def getNews(query: str) -> dict:
    '''
    retrieves query and search news with query
    param:
    query (string) : The query to search for news
    return:
    Union[str, dict]: A dictionary with news details
    '''
    response = call_news(query)
    return {
        "query": query,
        "news": response,
    }

def getStockPrice(stock_name, start_date, end_date) -> dict:
    response = call_stock_price(stock_name, start_date, end_date)
    return {
        "data": response,
    }

def getStockInfo(stock_name) -> dict:
    response = call_stock_info(stock_name)
    return {
        "data": response,
    }


def getFinancialStatement(corp_name, year, quarter) -> dict:
    response = call_financial_statement(corp_name, year, quarter)
    return {
        "data": response,
    }


getNews_api_schema = {
    "name" : "getNews",
    "parameters": {
        "type": "object",
        "properties": {
            "query": {
                "type": "string",
                "description": "The query to search for news (Korean)",
            },
        },
        "required": ["query"],
    },
    "description": "This is an API endpoint allowing users to input query and search news",
}

getStockPrice_api_schema = {
    "name": "getStockPrice",
    "parameters": {
        "type": "object",
        "properties": {
            "stock_name": {
                "type": "string",
                "description": "The name of the stock for which the price data is requested. (Korean)",
            },
            "start_date": {
                "type": "string",
                "description": "The start date for the price data retrieval (YYYYMMDD).",
            },
            "end_date": {
                "type": "string",
                "description": "The end date for the price data retrieval (YYYYMMDD).",
            },
        },
        "required": ["stock_name", "start_date", "end_date"],
    },
    "description": "This is an API endpoint allowing users to retrieve historical stock price data for a specific stock within a given date range.",
}

getStockInfo_api_schema = {
    "name": "getStockInfo",
    "parameters": {
        "type": "object",
        "properties": {
            "stock_name": {
                "type": "string",
                "description": "The name of the stock for which information is requested. (Korean)",
            },
        },
        "required": ["stock_name"],
    },
    "description": "This is an API endpoint allowing users to retrieve general information about a specific stock.",
}

getFinancialStatement_api_schema = {
    "name": "getFinancialStatement",
    "parameters": {
        "type": "object",
        "properties": {
            "corp_name": {
                "type": "string",
                "description": "The name of the corporation for which the financial statement is requested. (Korean)",
            },
            "year": {
                "type": "integer",
                "description": "The year for which the financial statement is requested.",
            },
            "quarter": {
                "type": "integer",
                "description": "The quarter for which the financial statement is requested (1-4).",
            },
        },
        "required": ["corp_name", "year", "quarter"],
    },
    "description": "This is an API endpoint allowing users to retrieve the financial statement of a specific corporation for a given year and quarter.",
}



