package Project.quantHelper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 주식 테이블 엔티티
 */
@Entity
@Getter @Setter
public class Stock {

    @Id
    private Long stock_id; // PK
    private String stock_name; // 주식 이름
    private int price; // 현재가
    private String theme; // 업종 구분
    private String stock_price_index; // KOSPI인지 KOSDAK인지
    private String status;

    @OneToMany
    private List<CorporateInformation> corporateInformationList;
}
