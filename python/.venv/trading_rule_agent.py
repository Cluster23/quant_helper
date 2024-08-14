import autogen

class TradingRoleAgent(autogen.AssistantAgent):
    def __init__(self, name="TradingRoleAgent", llm_config=None, rules=None):
        super().__init__(name=name, llm_config=llm_config)
        self.rules = rules if rules else []

    def enforce_rules(self, trade_decision):
        for rule in self.rules:
            if not rule(trade_decision):
                return "Rule violation detected"
        return "All rules satisfied"

    def calming_message(self):
        return "Keep calm and follow the trading strategy."