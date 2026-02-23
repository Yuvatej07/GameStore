package com.example.Tests;

import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.Base.BaseTest;
import com.example.Pages.OrdersPage;
import com.example.Pages.PaymentPage;

public class OrdersPageTest extends BaseTest {

    private static final String TEST_USER_ID = "orders-test-user";
    private static final String BASE_URL = System.getProperty("base.url", "http://127.0.0.1:5501");

    private OrdersPage orders;
    private PaymentPage payment;

    @Override
    protected String getStartPath() {
        return "/orders.html";
    }

    @BeforeMethod
    public void initPages() {
        orders = new OrdersPage(driver);
        payment = new PaymentPage(driver);
    }

    @Test
    public void verifyOrdersPageLoads() {
        orders.setLoggedInUser(TEST_USER_ID);
        orders.openPath(BASE_URL, "/orders.html");

        Assert.assertEquals(orders.getPageTitleText(), "Purchase history");
    }

    @Test
    public void verifyNoOrdersMessageWhenOrderListIsEmpty() {
        orders.setLoggedInUser(TEST_USER_ID);
        orders.clearOrdersForUser(TEST_USER_ID);
        orders.clearLastSuccessForUser(TEST_USER_ID);
        orders.openPath(BASE_URL, "/orders.html");

        Assert.assertTrue(orders.isNoOrdersMessageDisplayed());
        Assert.assertEquals(orders.getRenderedOrderCount(), 0);
    }

    @Test
    public void verifyOrdersListVisibleWhenOrdersExist() {
        ensureAtLeastOneOrder();

        Assert.assertFalse(orders.isNoOrdersMessageDisplayed());
        Assert.assertTrue(orders.getRenderedOrderCount() > 0);
    }

    @Test
    public void verifyAtLeastOneOrderCardRendered() {
        ensureAtLeastOneOrder();

        Assert.assertTrue(orders.getRenderedOrderCount() >= 1);
    }

    @Test
    public void verifyOrderIdFormatStartsWithGS() {
        ensureAtLeastOneOrder();

        Assert.assertTrue(orders.getFirstOrderIdText().startsWith("GS-"));
    }

    @Test
    public void verifyOrderMetaContainsPurchasedLabel() {
        ensureAtLeastOneOrder();

        Assert.assertTrue(orders.getFirstOrderMetaText().contains("Purchased:"));
    }

    @Test
    public void verifyOrderTotalPriceIsDisplayed() {
        ensureAtLeastOneOrder();

        Assert.assertFalse(orders.getFirstOrderPriceText().trim().isEmpty());
    }

    @Test
    public void verifyOrderContainsLineItems() {
        ensureAtLeastOneOrder();

        Assert.assertTrue(orders.getFirstOrderItemsCount() > 0);
    }

    @Test
    public void verifySuccessBannerShownAfterPaymentRedirect() {
        completePurchaseFlow("neon-drift");

        Assert.assertTrue(orders.getCurrentUrl().contains("/orders.html?success=1"));
        Assert.assertTrue(orders.isSuccessBoxDisplayed());
        Assert.assertTrue(orders.getSuccessMessageText().contains("Order GS-"));
    }

    @Test
    public void verifyNewPurchaseIncreasesStoredOrderCount() {
        orders.setLoggedInUser(TEST_USER_ID);
        int before = orders.getStoredOrdersCount(TEST_USER_ID);

        completePurchaseFlow("neon-drift");
        int after = orders.getStoredOrdersCount(TEST_USER_ID);

        Assert.assertEquals(after, before + 1);
    }

    @Test
    public void verifyOrdersPageUrlIsCorrect() {
        ensureAtLeastOneOrder();

        Assert.assertTrue(orders.getCurrentUrl().contains("/orders.html"));
    }

    @Test
    public void verifyStoredOrdersCountIsPositive() {
        ensureAtLeastOneOrder();

        Assert.assertTrue(orders.getStoredOrdersCount(TEST_USER_ID) > 0);
    }

    @Test
    public void verifyAnotherPurchaseAddsAnotherRenderedOrder() {
        ensureAtLeastOneOrder();
        orders.openPath(BASE_URL, "/orders.html");
        int before = orders.getRenderedOrderCount();

        completePurchaseFlow("iron-legion");
        orders.openPath(BASE_URL, "/orders.html");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> orders.getRenderedOrderCount() >= before + 1);

        Assert.assertTrue(orders.getRenderedOrderCount() >= before + 1);
    }

    private void ensureAtLeastOneOrder() {
        orders.setLoggedInUser(TEST_USER_ID);
        if (orders.getStoredOrdersCount(TEST_USER_ID) == 0) {
            completePurchaseFlow("neon-drift");
        }
        orders.openPath(BASE_URL, "/orders.html");
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> orders.getRenderedOrderCount() > 0);
    }

    private void completePurchaseFlow(String gameId) {
        payment.setLoggedInUser(TEST_USER_ID);
        payment.clearCartForUser(TEST_USER_ID);
        payment.openPath(BASE_URL, "/index.html");
        payment.addGameToCartFromGamePage(BASE_URL, gameId);
        payment.goToPaymentFromCart(BASE_URL);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> payment.getCurrentUrl().contains("/payment.html"));

        payment.fillValidPaymentDetails();
        payment.clickPayNow();

        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> orders.getCurrentUrl().contains("/orders.html?success=1"));
    }
}
