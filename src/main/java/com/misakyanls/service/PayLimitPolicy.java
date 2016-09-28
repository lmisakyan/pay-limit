package com.misakyanls.service;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;
import com.misakyanls.model.limit.Limit;
import com.misakyanls.repository.LimitRepository;

import java.util.Objects;

public final class PayLimitPolicy {
    private final LimitRepository limitRepository;

    public PayLimitPolicy(LimitRepository limitRepository) {
        this.limitRepository = limitRepository;
    }

    public void validate(Payment payment) {
        Objects.requireNonNull(payment, "The 'payment' argument must not be null");

        for (Limit limit : limitRepository.getLimits(payment.getService())) {
            limit.validate(payment);
            if (payment.getStatus() == Status.SUBMIT_REQUIRED)
                break;
        }

        if (payment.getStatus() == Status.WAITING)
            payment.setStatus(Status.VALID);
    }

}
