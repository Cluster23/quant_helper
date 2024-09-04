import sys
from PyQt5.QtWidgets import (
    QApplication,
    QMainWindow,
    QWidget,
    QVBoxLayout,
    QHBoxLayout,
    QPushButton,
    QGraphicsView,
    QGraphicsScene,
    QGraphicsPixmapItem,
    QGraphicsTextItem,
    QGraphicsPathItem,
    QLabel,
    QMessageBox,
    QGroupBox,
)
from PyQt5.QtCore import Qt, QPointF
from PyQt5.QtGui import QPixmap, QPen, QPainterPath, QPainter  # Importing QPainter

import random


class DraggablePixmapItem(QGraphicsPixmapItem):
    def __init__(self, pixmap, label_text=None):
        super().__init__(pixmap)
        self.setFlag(QGraphicsPixmapItem.ItemIsMovable, True)
        self.setFlag(QGraphicsPixmapItem.ItemIsSelectable, True)
        self.label_text = label_text

    def mouseDoubleClickEvent(self, event):
        if self.label_text:
            QMessageBox.information(
                None, "Agent Information", f"Details for {self.label_text}"
            )
        super().mouseDoubleClickEvent(event)


class AgentGraphicsView(QGraphicsView):
    def __init__(self, parent=None):
        super().__init__(parent)
        self.setScene(QGraphicsScene(self))
        self.setRenderHint(QPainter.Antialiasing)

    def add_agent(self, image_path, position, label_text=None):
        pixmap = QPixmap(image_path).scaled(80, 80, Qt.KeepAspectRatio)
        pixmap_item = DraggablePixmapItem(pixmap, label_text)
        pixmap_item.setPos(position)
        self.scene().addItem(pixmap_item)

        if label_text:
            text = QGraphicsTextItem(label_text)
            text.setDefaultTextColor(Qt.black)
            text.setPos(
                position.x() + pixmap.width() / 2 - text.boundingRect().width() / 2,
                position.y() + pixmap.height() + 5,
            )
            self.scene().addItem(text)

        return pixmap_item

    def connect_agents(self, start_item, end_item):
        start_pos = QPointF(
            start_item.pos().x() + start_item.pixmap().width() / 2,
            start_item.pos().y() + start_item.pixmap().height(),
        )
        end_pos = QPointF(
            end_item.pos().x() + end_item.pixmap().width() / 2, end_item.pos().y()
        )

        path = QPainterPath(start_pos)
        mid_point = QPointF(
            (start_pos.x() + end_pos.x()) / 2, (start_pos.y() + end_pos.y()) / 2
        )
        control_point = QPointF(
            mid_point.x(), start_pos.y() + (end_pos.y() - start_pos.y()) / 2
        )
        path.quadTo(control_point, end_pos)

        line_item = QGraphicsPathItem(path)
        pen = QPen(Qt.blue, 2, Qt.SolidLine)
        pen.setCosmetic(True)
        line_item.setPen(pen)
        self.scene().addItem(line_item)


class CoordinatorAgent:
    def __init__(self):
        self.news_agent = NewsAnalysisAgent()
        self.rule_agent = RuleBasedTradingAgent()
        self.market_agent = MarketAnalysisAgent()

    def get_final_recommendation(self, prompts):
        opinions = []
        for prompt in prompts:
            news_opinion = self.news_agent.analyze_news(prompt)
            rule_opinion = self.rule_agent.apply_rules(prompt)
            market_opinion = self.market_agent.analyze_market(prompt)
            opinions.append(
                f"{prompt} -> News: {news_opinion}, Rules: {rule_opinion}, Market: {market_opinion}"
            )

        final_recommendation = "\n".join(opinions)
        return final_recommendation


class NewsAnalysisAgent:
    def analyze_news(self, stock_name):
        opinions = ["Positive", "Negative", "Neutral"]
        return random.choice(opinions)


class RuleBasedTradingAgent:
    def apply_rules(self, stock_name):
        return "Hold based on rules."


class MarketAnalysisAgent:
    def analyze_market(self, stock_name):
        return "Trending upward."


class ModernMultiAgentUI(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Stock Trading Advice Interface")
        self.setGeometry(100, 100, 1200, 800)

        self.coordinator_agent = CoordinatorAgent()
        self.selected_prompts = []

        self.central_widget = QWidget()
        self.setCentralWidget(self.central_widget)
        self.main_layout = QVBoxLayout(self.central_widget)

        topics = {
            "Stock Analysis": [
                "Analyze stock XYZ",
                "Evaluate stock JKL",
                "Technical analysis of VWX",
                "Fundamental analysis of YZA",
            ],
            "Market Trends": [
                "What is the market trend for ABC?",
                "Short-term forecast for MNO",
                "Long-term trend for PQR",
            ],
            "News Impact": [
                "Get news sentiment for GHI",
                "Impact of recent news on STU",
            ],
        }

        self.buttons = {}

        for topic, prompts in topics.items():
            group_box = QGroupBox(topic)
            group_layout = QVBoxLayout()
            group_box.setLayout(group_layout)

            for prompt in prompts:
                button = QPushButton(prompt)
                button.setCheckable(True)
                button.clicked.connect(
                    lambda checked, p=prompt, btn=button: self.toggle_prompt(p, btn)
                )
                self.buttons[prompt] = button
                group_layout.addWidget(button)

            self.main_layout.addWidget(group_box)

        self.submit_button = QPushButton("Submit Selected Prompts")
        self.submit_button.clicked.connect(self.submit_prompt)
        self.main_layout.addWidget(self.submit_button)

        self.graphics_view = AgentGraphicsView(self)
        self.main_layout.addWidget(self.graphics_view)

    def toggle_prompt(self, prompt, button):
        if button.isChecked():
            self.selected_prompts.append(prompt)
            button.setStyleSheet("background-color: lightblue;")
        else:
            self.selected_prompts.remove(prompt)
            button.setStyleSheet("")

    def submit_prompt(self):
        if not self.selected_prompts:
            QMessageBox.warning(
                self, "Input Error", "Please select at least one prompt."
            )
            return

        self.graphics_view.scene().clear()

        center_x = self.graphics_view.width() / 2
        top_margin = 50
        coordinator_position = QPointF(center_x - 40, top_margin)
        coordinator_node = self.graphics_view.add_agent(
            "sample.png", coordinator_position, "Coordinator"
        )

        agents_info = [
            ("sample.png", "News Analysis"),
            ("sample.png", "Rule-Based Trading"),
            ("sample.png", "Market Analysis"),
        ]
        bottom_margin = 250

        for i, (image_path, label_text) in enumerate(agents_info):
            horizontal_spacing = 150
            x = center_x - horizontal_spacing + (i * horizontal_spacing)
            y = top_margin + bottom_margin
            agent_position = QPointF(x, y)
            agent_node = self.graphics_view.add_agent(
                image_path, agent_position, label_text
            )
            self.graphics_view.connect_agents(coordinator_node, agent_node)

        final_recommendation = self.coordinator_agent.get_final_recommendation(
            self.selected_prompts
        )

        final_result_position = QPointF(center_x - 40, top_margin + bottom_margin + 100)
        final_text_item = QGraphicsTextItem(
            f"Final Recommendation:\n{final_recommendation}"
        )
        final_text_item.setPos(
            final_result_position.x(), final_result_position.y() + 90
        )
        self.graphics_view.scene().addItem(final_text_item)

        self.graphics_view.fitInView(self.graphics_view.sceneRect(), Qt.KeepAspectRatio)
        self.graphics_view.centerOn(coordinator_node)

        self.selected_prompts.clear()
        for button in self.buttons.values():
            button.setChecked(False)
            button.setStyleSheet("")


def main():
    app = QApplication(sys.argv)
    window = ModernMultiAgentUI()

    try:
        import qdarkstyle

        app.setStyleSheet(qdarkstyle.load_stylesheet_pyqt5())
    except ImportError:
        print("QDarkStyle not installed. Continuing without dark theme.")

    window.show()
    sys.exit(app.exec_())


if __name__ == "__main__":
    main()
