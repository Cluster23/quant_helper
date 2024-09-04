package Project.quantHelper.dto;

import Project.quantHelper.domain.FinancialStatement;
import Project.quantHelper.domain.Stock;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
public class FinancialStatementDTO {
    private Long stockId; // FK
    private String content; // 재무제표 내용
    private int year; // 재무제표 연도
    private int quarter; // 재무제표 분기

    @Builder
    public FinancialStatementDTO(Long stockId, String content, int year, int quarter) {
        this.stockId = stockId;
        this.content = content;
        this.year = year;
        this.quarter = quarter;
    }
}
