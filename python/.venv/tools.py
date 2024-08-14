from autogen.function_utils import get_function_schema

def getNews(query: str) -> dict:
    '''
    retrieves query and search news with query
    param:
    query (string) : The query to search for news
    return:
    Union[str, dict]: A dictionary with news details
    '''

    return {
        "query": query,
        "news": "news"
    }

api_schema = get_function_schema(
    getNews,
    name="getNews",
    description="Returns News information searched with query"
)

assistant_config = {
    "tools": [
        {
            "type": "function",
            "function": api_schema,
        }
    ],
}


