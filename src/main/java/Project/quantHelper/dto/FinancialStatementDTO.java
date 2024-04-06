package Project.quantHelper.dto;

import Project.quantHelper.domain.FinancialStatement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
public class FinancialStatementDTO {
    private Long id; // PK
    private Long stockId; // FK
    private Year year; // 재무제표 연도
    private int quarter; // 재무제표 분기

    public FinancialStatement toEntity(FinancialStatement financialStatement) {
        return FinancialStatement.builder()
                .id(stockId)
                .year(year)
                .quarter(quarter)
                .build();
    }

    @Builder
    public FinancialStatementDTO(Long id, Long stockId, Year year, int quarter) {
        this.id = id;
        this.stockId = stockId;
        this.year = year;
        this.quarter = quarter;
    }
}
