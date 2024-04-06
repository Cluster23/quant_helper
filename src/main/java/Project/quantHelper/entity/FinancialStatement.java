package Project.quantHelper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

/**
 * 재무제표 테이블 엔티티
 */
@Entity
@Getter @Setter
public class FinancialStatement {

    @Id @GeneratedValue
    @Column(name = "financial_statement_id")
    private Long id; // PK

    @ManyToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "stock_id")
    private Stock stock; // FK
    private Year year; // 재무제표 연도
    private int quarter; // 재무제표 분기
}
