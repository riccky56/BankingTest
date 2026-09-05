package Tests;

import org.testng.annotations.BeforeClass;

public class PaymentAPITest {
	    private static final String BASE_URL = "https://api.lloyds.com";
	    private static String authToken;
	    
	    @BeforeClass
	    public static void setUp() {
	        // Get authentication token
	        authToken = getAuthToken("testuser", "password");
	        
	        RestAssured.baseURI = BASE_URL;
	    }
	    
	    private static String getAuthToken(String username, String password) {
	        return given()
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
	    
	    @Test(groups = {"api", "payment"})
	    public void testSuccessfulPaymentTransfer() {
	        // Arrange: Build payment request
	        PaymentRequest request = new PaymentRequest()
	            .setRecipientName("John Doe")
	            .setRecipientAccount("12345678")
	            .setAmount(500.00)
	            .setCurrency("GBP")
	            .setPaymentType("FasterPayment");
	        
	        // Act: Make API call
	        given()
	            .header("Authorization", "Bearer " + authToken)
	            .contentType(ContentType.JSON)
	            .body(request)
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
	        given()
	            .header("Authorization", "Bearer " + authToken)
	            .contentType(ContentType.JSON)
	            .body(new PaymentRequest().setAmount(-100)) // Invalid
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
	        given()
	            .header("Authorization", "Bearer " + authToken)
	            .contentType(ContentType.JSON)
	            .body(new PaymentRequest()
	                .setAmount(999999.99) // Account doesn't have this much
	                .setAccountId("ACC123"))
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(422) // Unprocessable Entity
	            .body("status", equalTo("REJECTED"))
	            .body("reason", equalTo("INSUFFICIENT_FUNDS"));
	    }
	    
	    @Test(groups = {"api", "payment"})
	    public void testConcurrentPaymentAttempts() {
	        // For Lloyds: Test duplicate transaction detection
	        String paymentRequest = new PaymentRequest()
	            .setIdempotencyKey("unique-key-123")
	            .setAmount(250.00)
	            .toString();
	        
	        // First request succeeds
	        String transactionId1 = given()
	            .header("Authorization", "Bearer " + authToken)
	            .header("Idempotency-Key", "unique-key-123")
	            .contentType(ContentType.JSON)
	            .body(paymentRequest)
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(201)
	            .extract()
	            .path("transactionId");
	        
	        // Duplicate request with same idempotency key returns same result
	        String transactionId2 = given()
	            .header("Authorization", "Bearer " + authToken)
	            .header("Idempotency-Key", "unique-key-123")
	            .contentType(ContentType.JSON)
	            .body(paymentRequest)
	        .when()
	            .post("/payments/transfer")
	        .then()
	            .statusCode(201)
	            .extract()
	            .path("transactionId");
	        
	        assertEquals(transactionId1, transactionId2, 
	            "Duplicate request should return same transaction ID");
	    }
	    
	    @Test(groups = {"api", "payment", "contract"})
	    public void testPaymentResponseSchema() {
	        // Contract testing - ensure response structure is stable
	        given()
	            .header("Authorization", "Bearer " + authToken)
	            .contentType(ContentType.JSON)
	            .body(new PaymentRequest().setAmount(100.00))
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

	// Test data class
	public class PaymentRequest {
	    private String recipientName;
	    private String recipientAccount;
	    private Double amount;
	    private String currency = "GBP";
	    private String paymentType = "FasterPayment";
	    private String idempotencyKey;
	    
	    // Fluent setters
	    public PaymentRequest setRecipientName(String name) {
	        this.recipientName = name;
	        return this;
	    }
	    
	    public PaymentRequest setAmount(Double amount) {
	        this.amount = amount;
	        return this;
	    }
	    
	    // ... more setters
	    
	    @Override
	    public String toString() {
	        return new JSONObject()
	            .put("recipientName", recipientName)
	            .put("recipientAccount", recipientAccount)
	            .put("amount", amount)
	            .put("currency", currency)
	            .put("paymentType", paymentType)
	            .toString();
	    }
	}
}
