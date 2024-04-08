package Project.quantHelper.repository;

import Project.quantHelper.domain.FinancialStatement;
import Project.quantHelper.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialStatementRepository extends JpaRepository<FinancialStatement, Long> {
    List<FinancialStatement> findAllByStock(Stock stock);
}
