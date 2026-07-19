package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.PaymentTransaction;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PaymentGatewayAdapter {
    public String createPaymentUrl(PaymentTransaction transaction) {
        return UriComponentsBuilder.fromPath("/api/payments/demo-return")
                .queryParam("vnpTxnRef", transaction.getVnpTxnRef())
                .queryParam("success", true)
                .build()
                .toUriString();
    }
}
