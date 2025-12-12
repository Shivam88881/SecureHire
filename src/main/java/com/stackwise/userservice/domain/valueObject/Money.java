package com.stackwise.userservice.domain.valueObject;

public record Money(String currency, Double amount) {
    public Money {
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be null or empty");
        }
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }

    public Double convertTo(String targetCurrency, Double conversionRate) {
        if (targetCurrency == null || targetCurrency.isEmpty()) {
            throw new IllegalArgumentException("Target currency cannot be null or empty");
        }
        if (conversionRate == null || conversionRate <= 0) {
            throw new IllegalArgumentException("Conversion rate must be positive");
        }
        return this.amount * conversionRate;
    }
}
