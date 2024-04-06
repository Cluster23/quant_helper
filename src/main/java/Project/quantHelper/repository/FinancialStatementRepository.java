package Project.quantHelper.repository;

import Project.quantHelper.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialStatementRepository extends JpaRepository<Stock, Long> {
}
