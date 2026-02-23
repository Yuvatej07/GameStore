package com.example.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class OrdersPage {

    private final WebDriver driver;

    private final By pageTitle = By.className("page-title");
    private final By noOrders = By.id("no-orders");
    private final By orderCards = By.cssSelector(".order");
    private final By firstOrderId = By.cssSelector(".order .order-id");
    private final By firstOrderMeta = By.cssSelector(".order .order-meta");
    private final By firstOrderPrice = By.cssSelector(".order .price");
    private final By firstOrderItems = By.cssSelector(".order .order-item");
    private final By successBox = By.id("orders-success");
    private final By successMessage = By.id("orders-success-msg");

    public OrdersPage(WebDriver driver) {
        this.driver = driver;
    }

    public void setLoggedInUser(String userId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "localStorage.setItem('gs_session_v1', JSON.stringify({userId: arguments[0], name: 'Test User', email: 'test@example.com'}));",
                userId
        );
    }

    public void clearOrdersForUser(String userId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "localStorage.setItem('gs_orders_v1_user_' + arguments[0], JSON.stringify([]));",
                userId
        );
    }

    public void clearLastSuccessForUser(String userId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "localStorage.removeItem('gs_last_success_v1_user_' + arguments[0]);",
                userId
        );
    }

    public int getStoredOrdersCount(String userId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object value = js.executeScript(
                "const raw = localStorage.getItem('gs_orders_v1_user_' + arguments[0]);"
                        + "if (!raw) return 0;"
                        + "try { const arr = JSON.parse(raw); return Array.isArray(arr) ? arr.length : 0; }"
                        + "catch (e) { return 0; }",
                userId
        );
        return ((Number) value).intValue();
    }

    public void openPath(String baseUrl, String path) {
        driver.get(baseUrl + path);
    }

    public String getPageTitleText() {
        return driver.findElement(pageTitle).getText();
    }

    public boolean isNoOrdersMessageDisplayed() {
        return driver.findElement(noOrders).isDisplayed();
    }

    public int getRenderedOrderCount() {
        return driver.findElements(orderCards).size();
    }

    public String getFirstOrderIdText() {
        return driver.findElement(firstOrderId).getText();
    }

    public String getFirstOrderMetaText() {
        return driver.findElement(firstOrderMeta).getText();
    }

    public String getFirstOrderPriceText() {
        return driver.findElement(firstOrderPrice).getText();
    }

    public int getFirstOrderItemsCount() {
        return driver.findElements(firstOrderItems).size();
    }

    public boolean isSuccessBoxDisplayed() {
        return driver.findElement(successBox).isDisplayed();
    }

    public String getSuccessMessageText() {
        return driver.findElement(successMessage).getText();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
