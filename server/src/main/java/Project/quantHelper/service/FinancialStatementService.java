package Project.quantHelper.service;

import Project.quantHelper.domain.FinancialStatement;
import Project.quantHelper.domain.Stock;
import Project.quantHelper.dto.FinancialStatementDTO;
import Project.quantHelper.repository.FinancialStatementRepository;
import Project.quantHelper.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancialStatementService {

    @Autowired
    private final FinancialStatementRepository financialStatementRepository;

    @Autowired
    private final StockRepository stockRepository;

    /**
     * 재무제표 DTO를 받아서 Entity로 저장하고, 저장한 Entity의 id를 반환하는 메소드
     * @param financialStatementDTO
     * @return financialStatementId
     */
    @Transactional
    public Long save(FinancialStatementDTO financialStatementDTO){
        Stock foundStock = stockRepository.findById(financialStatementDTO.getStockId()).orElseThrow(() -> new RuntimeException());
        FinancialStatement financialStatement = FinancialStatement.builder()
                .content(financialStatementDTO.getContent())
                .year(financialStatementDTO.getYear())
                .quarter(financialStatementDTO.getQuarter())
                .build();
        financialStatement.changeStock(foundStock);
        financialStatementRepository.save(financialStatement);
        return financialStatement.getId();
    }

    /**
     * StockId와 연도, 분기에 해당하는 재무제표를 찾는 메소드
     * @param stockId, year, quarter
     * @return financialStatment
     */
    public FinancialStatementDTO findFinancialStatementByStockId(Long stockId, int year, int quarter) {
        Optional<FinancialStatement> financialStatement = financialStatementRepository.findByStockIdAndYearAndQuarter(stockId, year, quarter);
        if (financialStatement.isPresent()) {
            FinancialStatement fs = financialStatement.get();
            FinancialStatementDTO fsDTO = FinancialStatementDTO.builder()
                    .stockId(fs.getStock().getStockId())
                    .year(fs.getYear())
                    .quarter(fs.getQuarter())
                    .content(fs.getContent())
                    .build();
            return fsDTO;
        } else {
            throw new RuntimeException("No financial statement found for stock ID " + stockId + " for year " + year + " and quarter " + quarter);
        }
    }

    /**
     * 주식 이름으로 3년치 재무제표를 찾는 메소드
     * @param stockName
     * @return financialStatmentDTO
     */
    public List<FinancialStatementDTO> find3yearsFinancialStatementByStockName(String stockName){
        Stock foundStock = stockRepository.findByStockName(stockName); // stockName으로 Stock Entity 찾기
        List<FinancialStatement> financialStatements = financialStatementRepository.findAllByStock(foundStock); // Stock으로 재무제표 모두 가져오기

        List<FinancialStatementDTO> fsIn3years = new ArrayList<>();

        LocalDate today = LocalDate.now();
        for(FinancialStatement fs : financialStatements){
            if(fs.getYear() > (today.getYear() - 3)){
                // 3년 내의 재무제표면 일단 fsIn3years 리스트에 넣기
                FinancialStatementDTO fsDTO = FinancialStatementDTO.builder()
                        .stockId(fs.getStock().getStockId())
                        .year(fs.getYear())
                        .quarter(fs.getQuarter())
                        .content(fs.getContent())
                        .build();

                fsIn3years.add(fsDTO);
            }
        }

        return fsIn3years;
    }
}
