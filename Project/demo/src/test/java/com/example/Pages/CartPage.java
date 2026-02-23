package com.example.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class CartPage {

    private final WebDriver driver;

    private final By pageTitle = By.className("page-title");
    private final By cartList = By.id("cart-list");
    private final By cartFooter = By.id("cart-footer");
    private final By emptyCart = By.id("empty-cart");
    private final By grandTotal = By.id("grand-total");
    private final By checkoutButton = By.id("checkout");
    private final By cartItems = By.cssSelector(".cart-item");

    public CartPage(WebDriver driver) {
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

    public void reload() {
        driver.navigate().refresh();
    }

    public String getPageTitleText() {
        return driver.findElement(pageTitle).getText();
    }

    public boolean isCartListDisplayed() {
        return driver.findElement(cartList).isDisplayed();
    }

    public boolean isCartListPresent() {
        return !driver.findElements(cartList).isEmpty();
    }

    public boolean isEmptyCartMessageDisplayed() {
        return driver.findElement(emptyCart).isDisplayed();
    }

    public boolean isCartFooterDisplayed() {
        return driver.findElement(cartFooter).isDisplayed();
    }

    public int getCartItemsCount() {
        return driver.findElements(cartItems).size();
    }

    public boolean isCartItemPresent(String gameId) {
        return !driver.findElements(By.cssSelector(".cart-item[data-id='" + gameId + "']")).isEmpty();
    }

    public String getGrandTotalText() {
        return driver.findElement(grandTotal).getText();
    }

    public int getQuantityForItem(String gameId) {
        String qty = driver.findElement(
                By.cssSelector(".cart-item[data-id='" + gameId + "'] .count")
        ).getText();
        return Integer.parseInt(qty.trim());
    }

    public void clickIncrease(String gameId) {
        driver.findElement(
                By.cssSelector(".cart-item[data-id='" + gameId + "'] button[data-act='inc']")
        ).click();
    }

    public void clickDecrease(String gameId) {
        driver.findElement(
                By.cssSelector(".cart-item[data-id='" + gameId + "'] button[data-act='dec']")
        ).click();
    }

    public void clickRemove(String gameId) {
        driver.findElement(
                By.cssSelector(".cart-item[data-id='" + gameId + "'] button[data-act='rm']")
        ).click();
    }

    public void clickCheckout() {
        driver.findElement(checkoutButton).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
