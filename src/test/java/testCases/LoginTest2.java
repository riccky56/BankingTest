package testCases;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import Pages.LoginPagePageFactory;

public class LoginTest2 {

	WebDriver driver;

	@BeforeClass
	void setup() {
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
	}

	@Test
	void testLogin() {

		LoginPagePageFactory lp = new LoginPagePageFactory(driver);
		lp.setUsername("Admin");
		lp.setPassword("admin123");
		lp.loginbutton();

		Assert.assertEquals(driver.getTitle(), "OrangeHRM");

	}

	@AfterClass
	void tearDown() {
		if (driver != null) {
			driver.quit();
		}

	}
}
