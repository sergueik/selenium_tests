package com.github.sergueik.selenium;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.TimeoutException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// based on: https://software-testing.ru/forum/index.php?/topic/29059-vvod-login-and-password-v-splyvaiuschej-forme/page-2
public class KomusTest extends BaseTest {

	private static final String url = "http://www.komus.ru/myoffice/login";
	private static final String new_pass = "qwerty1234";
	private static final String old_pass = "qwerty1234";
	private static final String login = "marvin@yandex.ru";
	private static final String message_text = "Пароль успешно изменен!";

	@BeforeClass
	public void beforeClass() throws IOException {
		super.beforeClass();
		assertThat(driver, notNullValue());
		driver.manage().window().maximize();
	}

	@BeforeMethod
	public void loadBaseURL() {
		driver.get(url);
	}

	@Test
	public void test() {

		firstEnter(login, old_pass, driver);
		// changePass(old_pass, new_pass, driver);
		// logout(driver);
		// popupAuthorization(login, new_pass, driver);
		// changePass(new_pass, old_pass, driver);
		// logout(driver);

	}

	@Override
	@AfterClass
	public void afterClass() {
		try {
			driver.close();
		} catch (NoSuchWindowException e) {

		}
		driver.quit();
		driver = null;
	}

	@SuppressWarnings("deprecation")
	private void firstEnter(String login, String pass, WebDriver driver) {

		WebElement label = wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("form#loginForm div.control-group label")));
		WebElement input = driver.findElement(
				By.cssSelector(String.format("input#%s", label.getAttribute("for"))));
		assertThat(input, notNullValue());
		input.clear();
		input.sendKeys(login);
		System.err.println(input.getAttribute("outerHTML"));
		label = wait
				.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
						By.cssSelector("form#loginForm div.control-group label")))
				.stream().filter(e -> {
					System.err.println(
							String.format("Label class %s:\n%s", e.getAttribute("class"),
									BaseTest.Translit.toAscii(e.getAttribute("outerHTML"))));
					// NOTE: problem with codepage Пароль:
					return (BaseTest.Translit.toAscii(e.getAttribute("outerHTML"))
							.contains((CharSequence) "Parol:"));
				}).collect(Collectors.toList()).get(0);

		input = driver.findElement(By.id(label.getAttribute("for")));

		input.clear();
		input.sendKeys(old_pass);
		input = driver
				.findElement(By.cssSelector("form#loginForm button[type=\"submit\"]"));
		input.click();

		wait.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver d) {
				Boolean result = false;
				WebElement element = d
						.findElement(By.cssSelector("#globalMessages div.b-message__text"));

				String value = (String) BaseTest.Translit.toAscii(element.getText());
				System.err.println("current value: " + value);
				if (value.contains((CharSequence) "e-mail")) {
					result = true;
				}
				return result;
			}
		});

		wait.until(ExpectedConditions.textToBePresentInElement(
				By.cssSelector("#globalMessages div.b-message__text"),
				"e-mail") /*  Неверный e-mail или пароль. */);

	}

	@SuppressWarnings("deprecation")
	private void changePass(String oldPass, String newPass, WebDriver driver) {

		// Смена пароля

		By link_pass = By.linkText("Смена пароля");
		By password_old = By.name("password_old");
		By password = By.name("password");
		By password_check = By.name("password_check");
		By change_password = By.name("change_password");
		By success_message = By.xpath(".//*[contains(@class,'message_ok')]");

		driver.findElement(link_pass).click();

		driver.findElement(password_old).clear();
		driver.findElement(password_old).sendKeys(old_pass);

		driver.findElement(password).clear();
		driver.findElement(password).sendKeys(new_pass);

		driver.findElement(password_check).clear();
		driver.findElement(password_check).sendKeys(new_pass);

		driver.findElement(change_password).click();

		// Проверка смены пароля

		new WebDriverWait(driver, 15).until(ExpectedConditions
				.textToBePresentInElement(success_message, message_text));

	}

	private void logout(WebDriver driver) {
		driver.findElement(By.linkText("Выход")).click();

	}

	private void popupAuthorization(String login2, String pass2,
			WebDriver driver) {

		By auth_form = By.name("auth");
		By start_auth_link = By.cssSelector("div.t24_vhod_link");
		By email_input = By.cssSelector("#email");
		By pass_input = By.cssSelector("#password");
		By submit = By.cssSelector("div.t24_form_links * input");

		Actions actions = new Actions(driver);
		actions.moveToElement(driver.findElement(start_auth_link)).perform();
		new WebDriverWait(driver, 15).until(
				ExpectedConditions.visibilityOfAllElementsLocatedBy(email_input));
		WebElement login_input = driver.findElement(auth_form)
				.findElement(email_input);
		login_input.clear();
		login_input.sendKeys(login);
		WebElement password_input = driver.findElement(auth_form)
				.findElement(pass_input);
		password_input.clear();
		password_input.sendKeys(new_pass);

		driver.findElement(auth_form).findElement(submit).click();
	}

}