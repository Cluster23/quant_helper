package Project.quantHelper.repository;

import Project.quantHelper.domain.Stock;
import Project.quantHelper.domain.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice,Long> {
    public StockPrice findStockPriceByStockAndDate(Stock stock, LocalDate date);
}
