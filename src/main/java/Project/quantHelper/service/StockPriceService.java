package Project.quantHelper.service;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.domain.StockPrice;
import Project.quantHelper.dto.StockPriceDTO;
import Project.quantHelper.repository.StockPriceRepository;
import Project.quantHelper.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public StockPriceDTO getStockPriceDTOByStockIdAndDate(Long stockId, LocalDate startDate){
        StockPrice stockPrice = stockPriceRepository.findStockPriceByStockAndDate(stockRepository.findById(stockId).get(), startDate);

        if(stockPrice != null) {
            StockPriceDTO stockPriceDTO = StockPriceDTO.builder()
                    .stockId(stockId)
                    .date(startDate)
                    .maxPriceDay(stockPrice.getMaxPriceDay())
                    .minPriceDay(stockPrice.getMinPriceDay())
                    .openPrice(stockPrice.getOpenPrice())
                    .closePrice(stockPrice.getClosePrice())
                    .tradingVolume(stockPrice.getTradingVolume())
                    .build();
            return stockPriceDTO;
        } else {
            return null;
        }
    }

}
