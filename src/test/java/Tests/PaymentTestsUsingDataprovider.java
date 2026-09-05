package Tests;

import static org.testng.Assert.assertEquals;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import Pages.PaymentPage;
import testData.PaymentTestDataFactory;

//Parameterized test
public class PaymentTestsUsingDataprovider {
	private WebDriver driver;
	private PaymentPage paymentPage;

	@BeforeMethod
	public void setUp() {
		driver = new ChromeDriver();
		driver.get("https://lloyds.com/banking");
	}

	@DataProvider(name = "paymentScenarios")
	public Object[][] getPaymentScenarios() {
		return PaymentTestDataFactory.getPaymentScenarios().toArray(new Object[0][]);
	}

	@DataProvider(name = "secondarydata")
	public Object[][] getpayment() {
		return PaymentTestDataFactory.getPaymentScenarios().toArray(new Object[0][]);
		
	}
	
	
	
	
	@Test(dataProvider = "paymentScenarios", groups = { "smoke", "payment" })
	public void testPaymentWithDifferentScenarios(String accountType, String amount, String paymentType,
			String expectedStatus) {

		// Login
		// LoginPage loginPage = new LoginPage(driver);
		// loginPage.login("testuser@lloyds.com", "password123");

		// Select account type

		paymentPage = new PaymentPage(driver);
		paymentPage.selectAccount("Savings");
		// Enter payment details
		paymentPage.enterRecipientName("Test Recipient").enterRecipientAccount("12345678").enterAmount(amount)
				.selectPaymentType(paymentType).submitPayment();

		// Verify result
		assertEquals(expectedStatus, paymentPage.isPaymentConfirmed());
	}

	@AfterMethod
	public void tearDown() {
		// Cleanup: Reverse transaction if needed
		if (driver != null) {
			driver.quit();
		}
	}
}
