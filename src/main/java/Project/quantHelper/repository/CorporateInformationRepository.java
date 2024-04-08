package Project.quantHelper.repository;

import Project.quantHelper.domain.CorporateInformation;
import Project.quantHelper.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorporateInformationRepository extends JpaRepository<CorporateInformation, Long> {
}
