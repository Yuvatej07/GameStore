package com.example.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class PaymentPage {

    private final WebDriver driver;

    private final By pageTitle = By.className("page-title");
    private final By paymentForm = By.id("payment-form");
    private final By noCartMessage = By.id("no-cart");
    private final By checkoutButton = By.id("checkout");
    private final By orderItems = By.cssSelector("#order-items .order-item");
    private final By orderTotal = By.id("order-total");
    private final By payButton = By.id("pay-btn");
    private final By payMessage = By.id("pay-msg");

    private final By cardName = By.id("card-name");
    private final By cardNumber = By.id("card-number");
    private final By exp = By.id("exp");
    private final By cvv = By.id("cvv");
    private final By billing = By.id("billing");
    private final By city = By.id("city");
    private final By zip = By.id("zip");
    private final By country = By.id("country");

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
    }

    public void setLoggedInUser(String userId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "localStorage.setItem('gs_session_v1', JSON.stringify({userId: arguments[0], name: 'Test User', email: 'test@example.com'}));",
                userId
        );
    }

    public void clearCartForUser(String userId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "localStorage.setItem('gs_cart_v1_user_' + arguments[0], JSON.stringify([]));",
                userId
        );
    }

    public void openPath(String baseUrl, String path) {
        driver.get(baseUrl + path);
    }

    public void addGameToCartFromGamePage(String baseUrl, String gameId) {
        driver.get(baseUrl + "/game.html?id=" + gameId);
        driver.findElement(By.id("add-to-cart")).click();
    }

    public void goToPaymentFromCart(String baseUrl) {
        driver.get(baseUrl + "/cart.html");
        driver.findElement(checkoutButton).click();
    }

    public String getPageTitleText() {
        return driver.findElement(pageTitle).getText();
    }

    public boolean isPaymentFormDisplayed() {
        return driver.findElement(paymentForm).isDisplayed();
    }

    public boolean isNoCartMessageDisplayed() {
        return driver.findElement(noCartMessage).isDisplayed();
    }

    public int getOrderItemsCount() {
        return driver.findElements(orderItems).size();
    }

    public String getOrderTotalText() {
        return driver.findElement(orderTotal).getText();
    }

    public void enterCardNumber(String value) {
        driver.findElement(cardNumber).clear();
        driver.findElement(cardNumber).sendKeys(value);
    }

    public String getCardNumberValue() {
        return driver.findElement(cardNumber).getAttribute("value");
    }

    public void enterExpiry(String value) {
        driver.findElement(exp).clear();
        driver.findElement(exp).sendKeys(value);
    }

    public String getExpiryValue() {
        return driver.findElement(exp).getAttribute("value");
    }

    public void enterCvv(String value) {
        driver.findElement(cvv).clear();
        driver.findElement(cvv).sendKeys(value);
    }

    public String getCvvValue() {
        return driver.findElement(cvv).getAttribute("value");
    }

    public void enterCardholderName(String value) {
        driver.findElement(cardName).clear();
        driver.findElement(cardName).sendKeys(value);
    }

    public void enterBillingAddress(String value) {
        driver.findElement(billing).clear();
        driver.findElement(billing).sendKeys(value);
    }

    public void enterCity(String value) {
        driver.findElement(city).clear();
        driver.findElement(city).sendKeys(value);
    }

    public void enterZip(String value) {
        driver.findElement(zip).clear();
        driver.findElement(zip).sendKeys(value);
    }

    public void selectCountry(String visibleText) {
        Select select = new Select(driver.findElement(country));
        select.selectByVisibleText(visibleText);
    }

    public void clickPayNow() {
        driver.findElement(payButton).click();
    }

    public String getPayMessage() {
        return driver.findElement(payMessage).getText();
    }

    public void fillValidPaymentDetails() {
        enterCardholderName("Alex Gamer");
        enterCardNumber("4111111111111111");
        enterExpiry("12/30");
        enterCvv("123");
        enterBillingAddress("221B Baker Street");
        enterCity("Hyderabad");
        enterZip("500001");
        selectCountry("India");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
