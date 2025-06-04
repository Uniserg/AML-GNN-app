package ru.uniserg.graphaml.data;

public class Account {
    private Long accountId;
    private String currency;
    private String bank;

    public Account(Long accountId, String currency, String bank) {
        this.accountId = accountId;
        this.currency = currency;
        this.bank = bank;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", currency='" + currency + '\'' +
                ", bank='" + bank + '\'' +
                '}';
    }
}
