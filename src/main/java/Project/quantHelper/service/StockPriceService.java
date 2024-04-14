package Project.quantHelper.service;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.domain.StockPrice;
import Project.quantHelper.dto.StockPriceDTO;
import Project.quantHelper.repository.StockPriceRepository;
import Project.quantHelper.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final StockPriceRepository stockPriceRepository;

    private final StockRepository stockRepository;
    @Transactional
    public Long saveStockPrice(StockPriceDTO stockPriceDTO){
        Stock stock = stockRepository.findById(stockPriceDTO.getStockId()).get(); // Id로 stock을 찾아옴
        StockPrice stockPrice = StockPrice.builder()
                .date(stockPriceDTO.getDate())
                .maxPriceDay(stockPriceDTO.getMaxPriceDay())
                .minPriceDay(stockPriceDTO.getMinPriceDay())
                .openPrice(stockPriceDTO.getOpenPrice())
                .closePrice(stockPriceDTO.getClosePrice())
                .tradingVolume(stockPriceDTO.getTradingVolume())
                .build(); // stockPriceDTO로 stockPrice Entity 빌드
        stockPrice.changeStock(stock); // 빌드된 Entity에 stock(FK) 삽입

        stockPriceRepository.save(stockPrice);
        return stockPrice.getId();
    }
}
