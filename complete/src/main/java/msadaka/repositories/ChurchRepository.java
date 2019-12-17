package msadaka.repositories;

import msadaka.enums.PayBillStatus;
import msadaka.models.Church;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChurchRepository extends CrudRepository<Church, Long> {
    Church findChurchById(Long Id);

    Church findChurchByIdAndStatus(Long Id, PayBillStatus payBillStatus);

    List<Church> findChurchesByUserEmail(String userEmail);
}
