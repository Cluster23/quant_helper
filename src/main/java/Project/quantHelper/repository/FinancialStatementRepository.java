package Project.quantHelper.repository;

import Project.quantHelper.domain.FinancialStatement;
import Project.quantHelper.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialStatementRepository extends JpaRepository<FinancialStatement, Long> {
    List<FinancialStatement> findAllByStock(Stock stock);

    @Query("SELECT fs FROM FinancialStatement fs WHERE fs.stock.stockId = :stockId AND fs.year = :year AND fs.quarter = :quarter")
    Optional<FinancialStatement> findByStockIdAndYearAndQuarter(Long stockId, int year, int quarter);

}
