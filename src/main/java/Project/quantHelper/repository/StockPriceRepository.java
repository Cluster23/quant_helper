package Project.quantHelper.repository;

import Project.quantHelper.domain.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice,Long> {
}
