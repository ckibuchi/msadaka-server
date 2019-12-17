package msadaka.repositories;

import msadaka.models.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Payment findPaymentByRefID(String refID);

    Payment findPaymentById(int Id);

    List<Payment> findPaymentsByMsisdnAndPaymentDateAndStatus(String msisdn, Date paymentDate, String status);

    /*  List<Payment> findPaymentsByPaymentDate(Date paymentDate);
      List<Payment> findPaymentsByCarRegNoAndPaymentDate(String carRegNo,Date paymentDate);
      List<Payment>  findPaymentsByCarRegNoAndCountyAndSubCountyAndPaymentDateAndStatus(String carRegNo,String county,String subCounty,Date paymentDate,String status);
      List<Payment> findPaymentsByCarRegNoIgnoreCaseContainingAndCountyIgnoreCaseContainingAndSubCountyIgnoreCaseContainingAndPaymentDate(String carRegNo,String county,String subCounty,Date paymentDate);
      List<Payment> findPaymentsByCarRegNoIgnoreCaseContainingAndCountyIgnoreCaseContainingAndSubCountyIgnoreCaseContainingAndPaymentDateGreaterThanEqualAndPaymentDateIsLessThanEqual(String carRegNo,String county,String subCounty,Date fromDate,Date toDate);
      */
    Long countByPaymentDateAndStatusIgnoreCaseContaining(Date today, String status);

    @Query(" select SUM(amount) from Payment p where p.status='COMPLETED' and p.paymentDate = ?1")
    Float getTotalToday(Date today);

    @Query(" select count(id) from Payment p where p.status='COMPLETED' and p.church.userEmail=?1 and p.paymentDate = ?2")
    Long getCountTodayForEmail(String email, Date today);

    @Query(" select SUM(amount) from Payment p where p.status='COMPLETED' and p.church.userEmail=?1 and p.paymentDate = ?2")
    Float getTotalTodayForEmail(String email, Date today);

    @Query(" select SUM(amount) from Payment p where p.status='COMPLETED' and p.church.userEmail=?1")
    Float getForeverTotalForEmail(String email);

    @Query(" select SUM(amount) from Payment p where p.status='COMPLETED' and p.church.userEmail=?1 and paymentDate>=?2 and paymentDate<=?3 ")
    Float getThisMonthTotalForEmail(String email, Date firstday, Date lastday);


}
