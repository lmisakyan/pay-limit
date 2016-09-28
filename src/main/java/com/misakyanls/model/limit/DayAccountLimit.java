package com.misakyanls.model.limit;

import com.misakyanls.model.Payment;
import com.misakyanls.model.Payment.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class DayAccountLimit extends BaseLimit {
    private final Map<String, LimitContext> accountLimits = new HashMap<>();

    public DayAccountLimit(BigDecimal limitAmount) {
        super(limitAmount);
    }

    @Override
    public void validate(Payment payment) {
        LocalDate now = LocalDate.now(clock);
        String accountNumber = payment.getAccount().getAccountNumber();
        LimitContext currentContext = accountLimits.get(accountNumber);
        BigDecimal currentAccountAmount;
        if (currentContext == null)
            currentAccountAmount = BigDecimal.ZERO;
        else {
            if (!now.isEqual(currentContext.lastValidDate))
                currentContext.currentAmount = BigDecimal.ZERO;
            currentAccountAmount = currentContext.currentAmount;
        }

        BigDecimal cur = currentAccountAmount.add(payment.getAmount());
        if (cur.compareTo(limitAmount) > 0)
            payment.setStatus(Status.SUBMIT_REQUIRED);
        else {
            accountLimits.put(accountNumber, new LimitContext(cur, now));
            payment.setStatus(Status.VALID);
        }
    }

    private static class LimitContext {
        private BigDecimal currentAmount;
        private LocalDate lastValidDate;

        public LimitContext(BigDecimal currentAmount, LocalDate lastValidDate) {
            this.currentAmount = currentAmount;
            this.lastValidDate = lastValidDate;
        }
    }
}
