package Pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

	
	public abstract class BasePage {
	    protected WebDriver driver;
	    protected WebDriverWait wait;
	    
	    public BasePage(WebDriver driver) {
	        this.driver = driver;
	        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    }
	    
	    protected WebElement waitForElement(By locator) {
	        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	    }
	    
	    protected void click(By locator) {
	        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
	    }
	    
	    protected void type(By locator, String text) {
	        WebElement element = waitForElement(locator);
	        element.clear();
	        element.sendKeys(text);
	    }
	}	