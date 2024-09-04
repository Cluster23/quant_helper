package Project.quantHelper.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.Year;

/**
 * 재무제표 테이블 엔티티
 */
@Entity
@Getter
public class FinancialStatement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "financial_statement_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stockId", referencedColumnName = "stockId")
    private Stock stock; // FK
    private String content;
    private int year; // 재무제표 연도
    private int quarter; // 재무제표 분기

    // Foreign Key 빼고 빌더 패턴 생성
    // id도 자동 생성되므로 제외
    @Builder
    public FinancialStatement(String content, int year, int quarter) {
        this.content = content;
        this.year = year;
        this.quarter = quarter;
    }

    public FinancialStatement() {
    }


    // 재무제표 Entity를 만들 때, 어떤 주식을 위한 재무제표인지 stock 객체를 넣어줘야 한다. 이 때 사용하는 메소드이다.
    // 양방향 연관관계이므로 Stock의 FinancialStatmentlist에도 이 재무제표 객체를 넣어줘야 한다.
    public void changeStock(Stock stock) {
        this.stock = stock;
        stock.getFinancialStatementlist().add(this);
    }
}
