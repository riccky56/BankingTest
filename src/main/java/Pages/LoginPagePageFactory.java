package Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPagePageFactory {

	WebDriver driver;
	// constructor

	public LoginPagePageFactory(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	// locators

	@FindBy(xpath = ("//*[@placeholder='Username']"))
	WebElement username;

	@FindBy(xpath = ("//*[@placeholder='Password']"))
	WebElement password;

	@FindBy(xpath = ("//button[normalize-space()='Login']"))
	WebElement loginbuton;

	// action methods

	public void setUsername(String user) {
		username.sendKeys(user);
	}

	public void setPassword(String pwd) {
		password.sendKeys(pwd);
	}

	public void loginbutton() {
		loginbuton.click();
	}

}