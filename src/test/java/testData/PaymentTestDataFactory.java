package testData;

import java.util.Arrays;
import java.util.List;

// Test data factory
	public class PaymentTestDataFactory {
	    public static List<Object[]> getPaymentScenarios() {
	        return Arrays.asList(new Object[][] {
	        	
	            // accountType, amount, paymentType, expectedStatus
	            {"Savings", "100.00", "Faster Payment", "CONFIRMED"},
	            {"Savings", "50000.00", "Faster Payment", "PENDING_REVIEW"}, // High amount
	            {"Current", "500.00", "BACS", "CONFIRMED"},
	            {"Current", "1000.00", "International", "PENDING_FX"},
	            {"Joint", "250.00", "Faster Payment", "CONFIRMED"},
	            {"Business", "10000.00", "International", "PENDING_APPROVAL"},
	            {"Savings", "0.01", "Faster Payment", "CONFIRMED"}, // Boundary
	            {"Savings", "1000000.00", "Faster Payment", "REJECTED"}, // Limit exceeded
	            {"Savings", "-100.00", "Faster Payment", "VALIDATION_ERROR"}, // Negative
	            {"Current", "null", "Faster Payment", "VALIDATION_ERROR"}, // Missing field
	            
	            
	        });
	        
	        
	    }
	    
	    
	    
	}

	
	   
