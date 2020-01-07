package msadaka.services;

import msadaka.models.Payment;
import msadaka.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    public List<Payment> findPaymentsByChurchId(long Id)
    {
        return paymentRepository.findPaymentsByChurchId(Id);
    }
}
