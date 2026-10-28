package Tests;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.UUID;

import org.json.JSONObject;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class PaymentAPITest {
	    private String baseUrl;
	    private String authToken;
	    
	    @BeforeClass
	    public void setUp() {
	        // Use an approved sandbox implementing the example contract below.
	        baseUrl = requiredEnvironmentVariable("PAYMENT_API_BASE_URL");
	        String username = requiredEnvironmentVariable("PAYMENT_API_USERNAME");
	        String password = requiredEnvironmentVariable("PAYMENT_API_PASSWORD");
	        authToken = getAuthToken(username, password);
	        assertNotNull(authToken, "Authentication response must contain a token");
	        assertFalse(authToken.isBlank(), "Authentication token must not be blank");
	    }
	    
	    private String getAuthToken(String username, String password) {
	        return given()
	            .baseUri(baseUrl)
	            .contentType(ContentType.JSON)
	            .body(new JSONObject()
	                .put("username", username)
	                .put("password", password)
	                .toString())
	        .when()
	            .post("/auth/login")
	        .then()
	            .statusCode(200)
	            .extract()
	            .path("token");
	    }

	    private static String requiredEnvironmentVariable(String name) {
	        String value = System.getenv(name);
	        if (value == null || value.isBlank()) {
	            throw new SkipException("Set " + name + " for an approved payment API sandbox before running these tests.");
	        }
	        return value;
	    }

	    private RequestSpecification paymentRequest() {
	        return given()
	            .baseUri(baseUrl)
	            .header("Authorization", "Bearer " + authToken)
	            .contentType(ContentType.JSON);
	    }

	    private PaymentRequest validPayment(double amount) {
	        return new PaymentRequest()
	            .setRecipientName("John Doe")
	            .setRecipientAccount("12345678")
	            .setAccountId("ACC123")
	            .setAmount(amount)
	            .setCurrency("GBP")
	            .setPaymentType("FasterPayment");
	    }

	    @Test(groups = {"api", "payment"})
	    public void testSuccessfulPaymentTransfer() {
	        // Arrange: Build payment request
	        PaymentRequest request = validPayment(500.00);
	        
	        // Act: Make API call
	        paymentRequest()
	            .body(request.toJson())
	        .when()
	            .post("/payments/transfer")
	        .then()
	            // Assert: Response structure and content
	            .statusCode(201)
	            .body("status", equalTo("CONFIRMED"))
	            .body("transactionId", notNullValue())
	            .body("transactionId", matchesPattern("^TXN\\d{10}$"))
	            .body("amount", equalTo(500.00f))
	            .body("timestamp", notNullValue())
	            .header("Content-Type", containsString("application/json"));
	    }
	    
	    @Test(groups = {"api", "payment"})
	    public void testPaymentValidationErrors() {
	        paymentRequest()
	            .body(validPayment(-100).toJson()) // Only the amount is invalid
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(400)
	            .body("errors.size()", greaterThan(0))
	            .body("errors[0].field", equalTo("amount"))
	            .body("errors[0].message", containsString("must be positive"));
	    }
	    
	    @Test(groups = {"api", "payment"})
	    public void testInsufficientFundsScenario() {
	        paymentRequest()
	            .body(validPayment(999999.99).toJson()) // Sandbox account must have insufficient funds
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(422) // Unprocessable Entity
	            .body("status", equalTo("REJECTED"))
	            .body("reason", equalTo("INSUFFICIENT_FUNDS"));
	    }
	    
	    @Test(groups = {"api", "payment"})
	    public void testDuplicatePaymentAttempts() {
	        // Sequential replay tests idempotency, not concurrent execution.
	        String idempotencyKey = UUID.randomUUID().toString();
	        String requestJson = validPayment(250.00)
	            .setIdempotencyKey(idempotencyKey)
	            .toJson();
	        
	        // First request succeeds
	        String transactionId1 = paymentRequest()
	            .header("Idempotency-Key", idempotencyKey)
	            .body(requestJson)
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(201)
	            .extract()
	            .path("transactionId");
	        
	        // Duplicate request with same idempotency key returns same result
	        String transactionId2 = paymentRequest()
	            .header("Idempotency-Key", idempotencyKey)
	            .body(requestJson)
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(201)
	            .extract()
	            .path("transactionId");
	        
	        assertNotNull(transactionId1, "First response must contain a transaction ID");
	        assertFalse(transactionId1.isBlank(), "Transaction ID must not be blank");
	        assertEquals(transactionId2, transactionId1,
	            "Duplicate request should return same transaction ID");
	    }
	    
	    @Test(groups = {"api", "payment", "contract"})
	    public void testPaymentResponseSchema() {
	        // Contract testing - ensure response structure is stable
	        paymentRequest()
	            .body(validPayment(100.00).toJson())
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(201)
	            // Validate against JSON schema
	            .body(matchesJsonSchemaInClasspath("payment-response-schema.json"))
	            // Validate nested objects
	            .body("statusDetails.processedAt", notNullValue())
	            .body("statusDetails.processedBy", notNullValue());
	    }
}
