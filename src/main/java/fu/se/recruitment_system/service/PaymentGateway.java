package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.Transaction;

public interface PaymentGateway {
    String createPaymentUrl(Transaction transaction);

    boolean verifyCallback(String transactionReference, boolean success);
}
