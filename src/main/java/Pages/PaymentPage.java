package Pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;	

public class PaymentPage extends BasePage {
	    // Locators as private constants
	    private static final By RECIPIENT_NAME = By.id("recipientName");
	    private static final By RECIPIENT_ACCOUNT = By.id("recipientAccount");
	    private static final By AMOUNT_FIELD = By.id("amount");
	    private static final By PAYMENT_TYPE = By.xpath("//select[@name='paymentType']");
	    private static final By SUBMIT_BUTTON = By.id("submitPayment");
	    private static final By SUCCESS_MESSAGE = By.className("payment-confirmation");
	    private static final By ERROR_MESSAGE = By.className("error-alert");
	    
	    // Dynamic element: Processing spinner
	    private static final By PROCESSING_SPINNER = By.className("spinner");
	    
	    public PaymentPage(WebDriver driver) {
	        super(driver);
	    }
	    
	    // Fluent builder pattern for cleaner test code
	    public PaymentPage enterRecipientName(String name) {
	        type(RECIPIENT_NAME, name);
	        return this;
	    }
	    
	    public PaymentPage enterRecipientAccount(String account) {
	        type(RECIPIENT_ACCOUNT, account);
	        return this;
	    }
	    
	    public PaymentPage enterAmount(String amount) {
	        type(AMOUNT_FIELD, amount);
	        return this;
	    }
	    
	    public PaymentPage selectPaymentType(String type) {
	        Select dropdown = new Select(driver.findElement(PAYMENT_TYPE));
	        dropdown.selectByVisibleText(type);
	        return this;
	    }	
	    
	    public void submitPayment() {
	        click(SUBMIT_BUTTON);
	        // Wait for processing spinner to appear and disappear
	        waitForProcessingComplete();
	    }
	    
	    // Handle dynamic processing spinner
	    private void waitForProcessingComplete() {
	        // Wait for spinner to appear
	        wait.until(ExpectedConditions.presenceOfElementLocated(PROCESSING_SPINNER));
	        // Wait for spinner to disappear
	        wait.until(ExpectedConditions.stalenessOf(
	            driver.findElement(PROCESSING_SPINNER)
	        ));
	    }
	    
	    public String getSuccessMessage() {
	        return waitForElement(SUCCESS_MESSAGE).getText();
	    }
	    
	    public String getErrorMessage() {
	        return waitForElement(ERROR_MESSAGE).getText();
	    }
	    
	    public boolean isPaymentConfirmed() {
	        try {
	            return wait.until(ExpectedConditions.visibilityOfElementLocated(
	                SUCCESS_MESSAGE
	            )) != null;
	        } catch (TimeoutException e	) {
	            return false;
	        }
	    }
	}


