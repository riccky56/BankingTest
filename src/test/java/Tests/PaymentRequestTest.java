package Tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.testng.annotations.Test;

public class PaymentRequestTest {
	@Test(groups = "unit")
	public void serializesAllPaymentFields() {
		PaymentRequest request = new PaymentRequest()
				.setRecipientName("John \"Test\" Doe")
				.setRecipientAccount("00123456")
				.setAmount(250.25)
				.setCurrency("EUR")
				.setPaymentType("TestPayment")
				.setAccountId("ACC123")
				.setIdempotencyKey("test-key");
		JSONObject json = new JSONObject(request.toJson());
		assertEquals(json.getString("recipientName"), "John \"Test\" Doe");
		assertEquals(json.getString("recipientAccount"), "00123456");
		assertEquals(json.getBigDecimal("amount"), new BigDecimal("250.25"));
		assertEquals(json.getString("currency"), "EUR");
		assertEquals(json.getString("paymentType"), "TestPayment");
		assertEquals(json.getString("accountId"), "ACC123");
		assertEquals(json.getString("idempotencyKey"), "test-key");
	}

	@Test(groups = "unit")
	public void supportsInvalidIntegerAmountsForNegativeTests() {
		JSONObject json = new JSONObject(new PaymentRequest().setAmount(-100).toJson());
		assertEquals(json.getInt("amount"), -100);
		assertEquals(json.getString("currency"), "GBP");
		assertEquals(json.getString("paymentType"), "FasterPayment");
	}

	@Test(groups = "unit")
	public void omitsUnsetOptionalFields() {
		JSONObject json = new JSONObject(new PaymentRequest().setAmount(100).toString());
		assertFalse(json.has("idempotencyKey"));
		assertFalse(json.has("accountId"));
	}

	@Test(groups = "unit")
	public void schemaAcceptsExampleSuccessResponse() {
		assertThat(successResponse().toString(),
				matchesJsonSchemaInClasspath("payment-response-schema.json"));
	}

	@Test(groups = "unit")
	public void schemaRejectsInvalidResponses() {
		JSONObject missingId = successResponse();
		missingId.remove("transactionId");
		assertFalse(matchesJsonSchemaInClasspath("payment-response-schema.json")
				.matches(missingId.toString()));
		assertFalse(matchesJsonSchemaInClasspath("payment-response-schema.json")
				.matches(successResponse().put("amount", -1).toString()));
		assertFalse(matchesJsonSchemaInClasspath("payment-response-schema.json")
				.matches(successResponse().put("transactionId", "invalid").toString()));
	}

	private JSONObject successResponse() {
		return new JSONObject()
				.put("status", "CONFIRMED")
				.put("transactionId", "TXN1234567890")
				.put("amount", 100)
				.put("timestamp", "2026-09-20T00:00:00Z")
				.put("statusDetails", new JSONObject()
						.put("processedAt", "2026-09-20T00:00:00Z")
						.put("processedBy", "sandbox"));
	}
}