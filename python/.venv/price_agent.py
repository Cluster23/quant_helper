import autogen
import yfinance as yf

class PriceAgent(autogen.AssistantAgent):
    def __init__(self, name="PriceAgent", llm_config=None):
        super().__init__(name=name, llm_config=llm_config)

    def get_current_price(self, stock_symbol):
        ticker = yf.Ticker(stock_symbol)
        return ticker.history(period="1d")['Close'][0]

    def calculate_trend_line(self, stock_symbol):
        ticker = yf.Ticker(stock_symbol)
        hist = ticker.history(period="1mo")
        trend_line = hist['Close'].rolling(window=5).mean()
        return trend_line