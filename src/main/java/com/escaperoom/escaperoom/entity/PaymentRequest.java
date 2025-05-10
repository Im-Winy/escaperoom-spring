package com.escaperoom.escaperoom.entity;

public class PaymentRequest {
	
    private Long amount;
    private String paymentToken; // jeton fictif simulant une carte ou un moyen de paiement
    
	public Long getAmount() {
		return amount;
	}
	
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	
	public String getPaymentToken() {
		return paymentToken;
	}
	
	public void setPaymentToken(String paymentToken) {
		this.paymentToken = paymentToken;
	}

}
