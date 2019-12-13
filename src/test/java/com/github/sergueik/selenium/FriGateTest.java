package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
* @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
*/

public class FriGateTest extends BaseTest {

	private String baseURL = "https://www.aliexpress.com/";
	private static List<String> chromeExtensions = new ArrayList<>();
	static {
		chromeExtensions.add("mdnmhbnbebabimcjggckeoibchhckemm");
		// filename without .crx extension
	}

	@BeforeClass
	public void beforeClass() throws IOException {
		super.setExtensionDir("C:\\Users\\Serguei\\Downloads");
		for (String chromeExtention : chromeExtensions) {
			System.err
					.println("Adding extension from file " + chromeExtention + ".crx");
			super.addChromeExtension(chromeExtention);
		}
		super.beforeClass();
		assertThat(driver, notNullValue());
		String eulaURL = "chrome-extension://mdnmhbnbebabimcjggckeoibchhckemm/eula.html";
		try {
			new App(driver);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		driver.get(eulaURL);

		Screen screen = new Screen();
		String imagePath = getResourcePath("frigate.png");
		try {
			screen.find(imagePath);
			screen.click(imagePath);
			System.err.println("Clicked on extension icon");
		} catch (FindFailed e) {
			e.printStackTrace();
		}
		imagePath = getResourcePath("accept.png");
		try {
			screen.find(imagePath);
			screen.click(imagePath);
			System.err.println("Clicked on button");
		} catch (FindFailed e) {
			e.printStackTrace();
		}
		System.err.println("Title of the window: " + driver.getTitle());
		System.err.println("Source page: " + driver.getPageSource());
		sleep(120000);
		driver.get(baseURL);
	}

	@Test(priority = 1, enabled = true)
	public void openExtensionPopupTest() {
		sleep(100);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.numberOfWindowsToBe(2));

		String parentWindow = driver.getWindowHandle();
		Set<String> allWindows = driver.getWindowHandles();
		for (String curWindow : allWindows) {
			if (!parentWindow.equals(curWindow)) {
				driver.switchTo().window(curWindow);
				System.err.println("Title of the window" + driver.getTitle());
			}
		}
	}

	private static class App implements Runnable {
		public static WebDriver driver;
		private static Set<String> windowHandles;
		Thread thread;

		App(WebDriver driver) throws InterruptedException {
			App.driver = driver;
			thread = new Thread(this, "test");
			thread.start();
		}

		public void run() {
			String currentHandle = null;

			try {
				System.err.println("Thread: sleep 3 sec.");
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("Thread: wake.");
			// With modal window, WebDriver appears to be hanging on [get current
			// window handle]
			try {
				currentHandle = driver.getWindowHandle();
				System.err.println("Thread: Current Window handle" + currentHandle);
			} catch (NoSuchWindowException e) {

			}
			while (true) {
				try {
					System.out.println("Thread: wait .5 sec");
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Thread: inspecting all Window handles");
				// when a modal window is created by Javascript window.showModalDialog
				// WebDriver appears to be hanging on [get current window handle], [get
				// window handles]
				// Node console shows no Done: [get current window handle] or Done: [get
				// window handles]
				// if the window is closed manually, and cleater again, the problem goes
				// away
				windowHandles = driver.getWindowHandles();
				if (windowHandles.size() > 1) {
					System.err.println(
							"Found " + (windowHandles.size() - 1) + " additional Windows");
					break;
				} else {
					System.out.println("Thread: no other Windows");
				}

			}

			Iterator<String> windowHandleIterator = windowHandles.iterator();
			while (windowHandleIterator.hasNext()) {
				String handle = (String) windowHandleIterator.next();
				if (!handle.equals(currentHandle)) {
					System.out.println("Switch to window handle: " + handle);
					driver.switchTo().window(handle);
					System.err.println("Title of the window" + driver.getTitle());
					// move, print attributes
					// SIKULI
					System.out.println("Switch to main window.");
					driver.switchTo().defaultContent();
				}
				driver.switchTo().window(currentHandle);
			}

			/*
			// the rest of example commented out
			String nextHandle = driver.getWindowHandle();
			System.out.println("nextHandle" + nextHandle);
			
			driver.findElement(By.xpath("//input[@type='button'][@value='Close']")).click();
			
			// Switch to main window
			for (String handle : driver.getWindowHandles()) {
			    driver.switchTo().window(handle);
			}
			// Accept alert
			driver.switchTo().alert().accept();
			*/
		}

		public static void main(String args[])
				throws InterruptedException, MalformedURLException {

			/*
			System.setProperty("webdriver.chrome.driver", "c:/java/selenium/chromedriver.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			LoggingPreferences logging_preferences = new LoggingPreferences();
			logging_preferences.enable(LogType.BROWSER, Level.ALL);
			capabilities.setCapability(CapabilityType.LOGGING_PREFS, logging_preferences);
			//  prefs.js:user_pref("extensions.logging.enabled", true);
			//  user.js:user_pref("extensions.logging.enabled", true);
			driver = new ChromeDriver(capabilities);
			*/
			// driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"),
			// capabilities);

			new App(null);
			// non-modal windows are handled successfully.
			// driver.get("http://www.naukri.com/");
			driver.get(
					"https://developer.mozilla.org/samples/domref/showModalDialog.html");
			// following two locator do not work with IE
			// driver.findElement(By.xpath("//input[@value='Open modal
			// dialog']")).click();
			// driver.findElement(By.cssSelector("input[type='button']")).click();
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.pollingEvery(500, TimeUnit.MILLISECONDS);
			Actions actions = new Actions(driver);

			wait.until(ExpectedConditions
					.visibilityOf(driver.findElement(By.xpath("html/body"))));

			WebElement body = driver.findElement(By.xpath("html/body"));
			body.findElement(By.xpath("input")).click();

			System.out.println("main: sleeping 10 sec");

			Thread.sleep(20000);
			System.out.println("main: close");
			driver.close();
			driver.quit();
		}
	}

}
