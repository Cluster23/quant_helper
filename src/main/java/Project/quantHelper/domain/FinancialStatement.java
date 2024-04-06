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

    @Id @GeneratedValue
    @Column(name = "financial_statement_id")
    private Long id; // PK

    @ManyToOne
    @JoinColumn(name = "stockId", referencedColumnName = "stockId")
    private Stock stock; // FK
    private Year year; // 재무제표 연도
    private int quarter; // 재무제표 분기

    public void setStock(Stock stock){
        this.stock = stock;
    }

    // Foreign Key 빼고 빌더 패턴 생성
    @Builder
    public FinancialStatement(Long id, Year year, int quarter) {
        this.id = id;
        this.year = year;
        this.quarter = quarter;
    }

    public FinancialStatement() {
    }
}
