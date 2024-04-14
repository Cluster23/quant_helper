import xml.etree.ElementTree as ET
import pandas as pd

file_path1 = "data_0026_20240414.csv"
file_path2 = "data_0315_20240414.csv"

df1 = pd.read_csv(file_path1, encoding="EUC-KR")
df2 = pd.read_csv(file_path2, encoding="EUC-KR")

concatenated_df = pd.concat([df1, df2], ignore_index=True)

stock_code_df = concatenated_df[["종목명", "종목코드"]]

tree = ET.parse("CORPCODE.xml")
root = tree.getroot()

data = {}
for stock in root.findall("list"):
    stock_name = stock.find("corp_name").text
    corp_code = stock.find("corp_code").text
    data[stock_name] = [corp_code]


for index, stock in stock_code_df.iterrows():
    try:
        data[stock["종목명"]].append(stock["종목코드"])
    except Exception as e:
        continue

import psycopg2

conn = psycopg2.connect(
    dbname="quant-helper", user="cluster23", password="cluster23", host="localhost"
)

cur = conn.cursor()
for stock_name, stock_info in data.items():
    if len(stock_info) < 2:
        continue
    query = "INSERT INTO stock (stock_name, corp_code, stock_code) VALUES (%s, %s, %s)"
    cur.execute(query, (stock_name, stock_info[0], stock_info[1]))
conn.commit()
cur.close()
conn.close()
