package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

	WebDriver driver;
	//constructor
	public LoginPage(WebDriver driver) {
		this.driver = driver;
	}
	
	//locators
	By txt_username = By.xpath("//*[@placeholder='Username']");
	By txt_password = By.xpath("//*[@placeholder='Password']");
	By login = By.xpath("//button[normalize-space()='Login']");
	
	
	//action mwethods
	
	public void setUsername(String user) {
		driver.findElement(txt_username).sendKeys(user);
	}
	
	public void setPassword(String pwd) {
		driver.findElement(txt_password).sendKeys(pwd);
	}
	public void loginbutton() {
		driver.findElement(login).click();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
