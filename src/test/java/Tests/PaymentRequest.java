package Tests;

import java.math.BigDecimal;

import org.json.JSONObject;

public class PaymentRequest {
	private String recipientName;
	private String recipientAccount;
	private BigDecimal amount;
	private String currency = "GBP";
	private String paymentType = "FasterPayment";
	private String idempotencyKey;
	private String accountId;

	public PaymentRequest setRecipientName(String recipientName) {
		this.recipientName = recipientName;
		return this;
	}

	public PaymentRequest setRecipientAccount(String recipientAccount) {
		this.recipientAccount = recipientAccount;
		return this;
	}

	public PaymentRequest setAmount(double amount) {
		this.amount = BigDecimal.valueOf(amount);
		return this;
	}

	public PaymentRequest setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public PaymentRequest setPaymentType(String paymentType) {
		this.paymentType = paymentType;
		return this;
	}

	public PaymentRequest setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
		return this;
	}

	public PaymentRequest setAccountId(String accountId) {
		this.accountId = accountId;
		return this;
	}

	public String toJson() {
		return new JSONObject()
				.put("recipientName", recipientName)
				.put("recipientAccount", recipientAccount)
				.put("amount", amount)
				.put("currency", currency)
				.put("paymentType", paymentType)
				.put("idempotencyKey", idempotencyKey)
				.put("accountId", accountId)
				.toString();
	}

	@Override
	public String toString() {
		return toJson();
	}
}