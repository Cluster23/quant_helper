package Project.quantHelper.repository;

import Project.quantHelper.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findByStockName(String stockName);
    Stock findByStockCode(String stockCode);
    List<Stock> findAll();
}
