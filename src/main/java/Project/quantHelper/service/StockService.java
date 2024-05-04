package Project.quantHelper.service;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.dto.StockPriceDTO;
import Project.quantHelper.repository.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    /**
     * stockDTO를 매개변수로 받아서, entity로 변환한 다음 Stock 테이블에 저장한다.
     *
     * @param stockDTO
     * @return stockCode
     */
    @Transactional
    public String save(StockDTO stockDTO) {
        stockRepository.save(stockDTO.toEntity());
        return stockDTO.getStockCode();
    }

    /**
     * stockName으로 stock 정보를 가져온다.
     *
     * @param stockName
     * @return stockDTO
     */
    public StockDTO findByStockName(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).get(0);
        return StockDTO.builder()
                 .stockId(stock.getStockId())
                 .stockCode(stock.getStockCode())
                 .stockName(stock.getStockName())
                 .price(stock.getPrice())
                 .theme(stock.getTheme())
                 .stockPriceIndex(stock.getStockPriceIndex())
                 .status(stock.getStatus())
                 .corpCode(stock.getCorpCode())
                 .build();
    }

    public List<StockDTO> findAll() {
        List<Stock> stocks = stockRepository.findAll();
        List<StockDTO> stockDTOs = new ArrayList<>();
        for (Stock stock : stocks) {
            StockDTO stockDTO = StockDTO.builder()
                    .stockCode(stock.getStockCode())
                    .stockName(stock.getStockName())
                    .price(stock.getPrice())
                    .theme(stock.getTheme())
                    .stockPriceIndex(stock.getStockPriceIndex())
                    .status(stock.getStatus())
                    .corpCode(stock.getCorpCode())
                    .build();
            stockDTOs.add(stockDTO);
        }
        return stockDTOs;
    }

    public Long findStockIdByStockCode(String stockCode) {
        Stock stock = stockRepository.findByStockCode(stockCode);
        return stock.getStockId();
    }

    public String findStockCodeByStockName(String stockName) {
        Stock stock = stockRepository.findByStockName(stockName).get(0);
        log.info(stock.getStockCode());
        return stock.getStockCode();
    }

    /**
     * stockId, stockCode, stockName, corpCode는 고정하고, 나머지 컬럼은 업데이트하는 메소드
     */
    @Transactional
    public int updateStock(String stockName, String stockCode, String stockPriceIndex, Long price, String theme, String status, String corpCode){
        return stockRepository.updateStockByStockName(stockName, stockCode, stockPriceIndex, price, theme, status, corpCode);
    }
}
