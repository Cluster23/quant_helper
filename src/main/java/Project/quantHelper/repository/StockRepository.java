package Project.quantHelper.repository;

import Project.quantHelper.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Modifying
    @Query("UPDATE Stock s SET s.stockCode = :stockCode, s.stockPriceIndex = :stockPriceIndex, s.price = :price, s.theme = :theme, s.status = :status, s.corpCode = :corpCode WHERE s.stockName = :stockName")
    int updateStockByStockName(@Param("stockName") String stockName,
                               @Param("stockCode") String stockCode,
                               @Param("stockPriceIndex") String stockPriceIndex,
                               @Param("price") Long price,
                               @Param("theme") String theme,
                               @Param("status") String status,
                               @Param("corpCode") String corpCode);
    Stock findByStockName(String stockName);
    Stock findByStockCode(String stockCode);
    List<Stock> findAll();
}
