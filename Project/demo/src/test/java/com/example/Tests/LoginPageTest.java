package com.example.Tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.Base.BaseTest;

public class LoginPageTest extends BaseTest {

    private final By email = By.id("login-email");
    private final By password = By.id("login-password");
    private final By loginBtn = By.cssSelector("#login-form button");
    private final By errorMsg = By.id("login-msg");
    private final By signupTab = By.id("tab-signup");
    private WebDriverWait wait;

    @Override
    protected String getStartPath() {
        return "/login.html";
    }

    @BeforeMethod
    public void initWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 1️⃣ Verify login page loads
    @Test
    public void verifyLoginPageLoads() {
        Assert.assertTrue(driver.getTitle().contains("Game Store"));
    }

    // 2️⃣ Empty email & password
    @Test
    public void loginWithEmptyFields() {
        click(loginBtn);
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg)) != null);
    }

    // 3️⃣ Invalid email format
    @Test
    public void loginWithInvalidEmail() {
        type(email, "abc");
        type(password, "password123");
        click(loginBtn);
    }

    // 4️⃣ Password less than 8 chars
    @Test
    public void loginWithShortPassword() {
        type(email, "test@gmail.com");
        type(password, "123");
        click(loginBtn);
    }

    // 5️⃣ Valid email + wrong password
    @Test
    public void loginWithWrongPassword() {
        type(email, "user@test.com");
        type(password, "wrongpass");
        click(loginBtn);
    }

    // 6️⃣ Check email field accepts input
    @Test
    public void verifyEmailFieldInput() {
        clearAndType(email, "hello@test.com");
        wait.until(ExpectedConditions.attributeToBe(email, "value", "hello@test.com"));
        String value = wait.until(ExpectedConditions.visibilityOfElementLocated(email)).getAttribute("value");
        Assert.assertEquals(value, "hello@test.com");
    }

    // 7️⃣ Password field masked
    @Test
    public void verifyPasswordIsHidden() {
        String type = wait.until(ExpectedConditions.visibilityOfElementLocated(password))
                .getAttribute("type");

        Assert.assertEquals(type, "password");
    }



    // 8️⃣ Signup tab clickable
    @Test
    public void verifySignupTabSwitch() {
        click(signupTab);
        Assert.assertTrue(wait.until(d -> isVisible(By.id("signup-form"))));
    }

    // 9️⃣ Login button enabled
    @Test
    public void verifyLoginButtonEnabled() {
        Assert.assertTrue(
            wait.until(ExpectedConditions.elementToBeClickable(loginBtn)).isEnabled()
        );
    }

    // 🔟 Multiple login attempts
    @Test
    public void multipleInvalidLoginAttempts() {

        for(int i=0;i<3;i++) {
            clearAndType(email, "wrong@test.com");
            clearAndType(password, "wrong1234");
            click(loginBtn);
        }

        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg)).isDisplayed());
    }

    private void click(By locator) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
                return;
            } catch (WebDriverException ignored) {
                // Retry on transient DOM replacement from tab switch/render.
            }
        }
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void type(By locator, String text) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
                setValue(el, text);
                wait.until(ExpectedConditions.attributeToBe(locator, "value", text));
                return;
            } catch (WebDriverException ignored) {
                // Retry on transient re-render.
            }
        }
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        setValue(el, text);
        wait.until(ExpectedConditions.attributeToBe(locator, "value", text));
    }

    private void clearAndType(By locator, String text) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
                setValue(el, text);
                wait.until(ExpectedConditions.attributeToBe(locator, "value", text));
                return;
            } catch (WebDriverException ignored) {
                // Retry on transient re-render.
            }
        }
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        setValue(el, text);
        wait.until(ExpectedConditions.attributeToBe(locator, "value", text));
    }

    private void setValue(WebElement el, String text) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                el, text
        );
    }

    private boolean isVisible(By locator) {
        String selector = locator.toString().replace("By.id: ", "#");
        Object visible = ((JavascriptExecutor) driver).executeScript(
                "const el = document.querySelector(arguments[0]);"
                        + "if (!el) return false;"
                        + "const s = window.getComputedStyle(el);"
                        + "return s.display !== 'none' && s.visibility !== 'hidden' && s.opacity !== '0';",
                selector
        );
        return Boolean.TRUE.equals(visible);
    }
}
