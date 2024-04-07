package Project.quantHelper.service;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    @Autowired
    private final StockRepository stockRepository;

    /**
     * stockDTO를 매개변수로 받아서, entity로 변환한 다음 Stock 테이블에 저장한다.
     * @param stockDTO
     * @return stockId
     */
    @Transactional
    public Long save(StockDTO stockDTO){
        stockRepository.save(stockDTO.toEntity());
        return stockDTO.getStockId();
    }

    public StockDTO findByStockName(String stockName){
         Stock stock = stockRepository.findByStockName(stockName);
        return StockDTO.builder()
                 .stockId(stock.getStockId())
                 .stockName(stock.getStockName())
                 .price(stock.getPrice())
                 .theme(stock.getTheme())
                 .stockPriceIndex(stock.getStockPriceIndex())
                 .status(stock.getStatus())
                 .build();
    }

}
