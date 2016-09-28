package com.misakyanls.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Payment {
    public enum Status {
        WAITING, VALID, SUBMIT_REQUIRED
    }

    private final Service service;
    private final Account account;
    private final BigDecimal amount;
    private Status status;

    public Payment(Service service, Account account, BigDecimal amount, Status status) {
        this.service = Objects.requireNonNull(service, "The 'service' argument must not be null");
        this.account = Objects.requireNonNull(account, "The 'account' argument must not be null");
        this.amount = Objects.requireNonNull(amount, "The 'amount' argument must not be null");
        this.status = Objects.requireNonNull(status, "The 'status' argument must not be null");
    }

    public Service getService() {
        return service;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
