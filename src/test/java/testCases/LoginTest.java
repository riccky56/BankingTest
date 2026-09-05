package testCases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.SessionNotCreatedException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import Pages.LoginPage;

public class LoginTest {

	WebDriver driver;
	Path tempProfile;

	@BeforeClass
	void setup() {
		driver = createDriverWithFallback();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
	}

	private WebDriver createDriverWithFallback() {
		try {
			return new ChromeDriver(buildOptions(false));
		} catch (SessionNotCreatedException primaryFailure) {
			// Fallback for machines where regular Chrome UI startup fails.
			return new ChromeDriver(buildOptions(true));
		}
	}

	private ChromeOptions buildOptions(boolean headless) {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-debugging-port=0");
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");
		options.addArguments("--no-first-run");
		options.addArguments("--no-default-browser-check");
		options.addArguments("--disable-extensions");

		if (headless) {
			options.addArguments("--headless=new");
			options.addArguments("--window-size=1920,1080");
		} else {
			options.addArguments("--start-maximized");
		}

		// Use a fresh profile each run to avoid profile lock / startup crashes.
		try {
			tempProfile = Files.createTempDirectory("chrome-profile-");
			options.addArguments("--user-data-dir=" + tempProfile.toAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException("Unable to create temporary Chrome profile", e);
		}

		return options;
	}

	@Test
	void testLogin() {

		LoginPage lp = new LoginPage(driver);
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

		if (tempProfile != null) {
			try {
				Files.walk(tempProfile)
					.sorted((a, b) -> b.compareTo(a))
					.forEach(path -> {
						try {
							Files.deleteIfExists(path);
						} catch (IOException ignored) {
							// Best effort cleanup.
						}
					});
			} catch (IOException ignored) {
				// Best effort cleanup.
			}
		}
	}

}