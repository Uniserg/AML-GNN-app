package ru.uniserg.graphaml.data;

public class Transaction {
    private Long transactionId;
    private long sourceAccount;
    private long targetAccount;
    private double amount;
    private long timestamp;
    private String paymentType;

    public Transaction(Long transactionId, long sourceAccount, long targetAccount, double amount, long timestamp, String paymentType) {
        this.transactionId = transactionId;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.timestamp = timestamp;
        this.paymentType = paymentType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public long getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(long sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public long getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(long targetAccount) {
        this.targetAccount = targetAccount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", sourceAccount=" + sourceAccount +
                ", targetAccount=" + targetAccount +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }
}
