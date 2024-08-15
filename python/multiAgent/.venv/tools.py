from autogen.function_utils import get_function_schema
from spring_api_functions import call_news

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


getNews_api_schema = {
    "name" : "getNews",
    "parameters": {
        "type": "object",
        "properties": {
            "query": {
                "type": "string",
                "description": "The query to search for news",
            },
        },
        "required": ["query"],
    },
    "description": "This is an API endpoint allowing users to input query and search news",
}