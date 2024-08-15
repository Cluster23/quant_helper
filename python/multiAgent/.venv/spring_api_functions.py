import requests

base_url = "http://localhost:8080"
def call_news(query):
    url = base_url + "/news/"  # Spring 서버의 API 엔드포인트
    params = {
        'query' : query,
    }
    response = requests.get(url, params=params)

    if response.status_code == 200:
        data = response.json()
        print("Received data:", data)
        return data
    else:
        print("Failed to fetch data. Status code:", response.status_code)