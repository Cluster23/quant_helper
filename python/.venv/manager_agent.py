import autogen


class ManagerAgent(autogen.AssistantAgent):
    def __init__(self, name="Manager", llm_config=None):
        super().__init__(name=name, llm_config=llm_config)

    def collect_opinions(self, opinions):
        # Logic to collect opinions from other agents
        final_decision = self.make_final_decision(opinions)
        return final_decision

    def make_final_decision(self, opinions):
        # Implement decision logic, for simplicity let's take majority
        decision = max(set(opinions), key=opinions.count)
        return decision
