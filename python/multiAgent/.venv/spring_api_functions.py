import requests

base_url = "http://localhost:8080"
def call_news(query):
    url = base_url + "/news/"  # Spring 서버의 API 엔드포인트
    params = {
        'query' : query,
    }
    response = requests.get(url, params=params)

    if response.status_code == 200:
        return response.json()
    else:
        print("Failed to fetch data. Status code:", response.status_code)

def call_stock_price(stock_name, start_date, end_date):
    url = base_url + "/stock/price"  # Spring 서버의 API 엔드포인트
    data = {
        'stockName': stock_name,
        'startDate': start_date,
        'endDate': end_date,
    }
    response = requests.post(url, json=data)  # 데이터를 요청 바디에 JSON으로 전달

    if response.status_code == 200:
        return response.json()
    else:
        print("Failed to fetch data. Status code:", response.status_code)

def call_stock_info(stock_name):
    url = base_url + "/stock/"  # Spring 서버의 API 엔드포인트
    data = {
        'stockName': stock_name,
    }
    response = requests.post(url, json=data)  # 데이터를 요청 바디에 JSON으로 전달

    if response.status_code == 200:
        return response.json()
    else:
        print("Failed to fetch data. Status code:", response.status_code)

def call_financial_statement(corp_name, year, quarter):
    url = base_url + "/financial-statement/"  # Spring 서버의 API 엔드포인트
    data = {
        'corpName': corp_name,
        'year': year,
        'quarter': quarter,
    }
    response = requests.post(url, json=data)  # 데이터를 요청 바디에 JSON으로 전달

    if response.status_code == 200:
        return response.json()
    else:
        print("Failed to fetch data. Status code:", response.status_code)