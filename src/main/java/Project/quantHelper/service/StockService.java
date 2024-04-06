package Project.quantHelper.service;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    public StockDTO findStock(String stockName){
         Stock stock = stockRepository.findByName(stockName);
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
