package Tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import Pages.PaymentPage;

public class PaymentTest {
	
	WebDriver driver = new ChromeDriver();
	// Test usage - clean and readable
	@Test
	public void testSuccessfulPaymentTransfer() {
		
	    PaymentPage paymentPage = new PaymentPage(driver);
	    
	    paymentPage
	        .enterRecipientName("John Doe")
	        .enterRecipientAccount("12345678")
	        .enterAmount("500.00")
	        .selectPaymentType("Faster Payment")
	        .submitPayment();
	    
	   
	    
	    assertTrue(paymentPage.isPaymentConfirmed());
	    assertEquals("Payment confirmed", paymentPage.getSuccessMessage());
	}
	
}
	