package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class VNPayGateway {
    public String createPaymentUrl(Transaction transaction) {
        return UriComponentsBuilder.fromPath("/api/payments/demo-return")
                .queryParam("vnpTxnRef", transaction.getVnpTxnRef())
                .queryParam("success", true)
                .build()
                .toUriString();
    }
}
