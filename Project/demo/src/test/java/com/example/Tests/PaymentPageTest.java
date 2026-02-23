package com.example.Tests;

import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.Base.BaseTest;
import com.example.Pages.PaymentPage;

public class PaymentPageTest extends BaseTest {

    private static final String TEST_USER_ID = "payment-test-user";
    private static final String BASE_URL = System.getProperty("base.url", "http://127.0.0.1:5501");

    private PaymentPage payment;

    @Override
    protected String getStartPath() {
        return "/index.html";
    }

    @BeforeMethod
    public void initPageObject() {
        payment = new PaymentPage(driver);
    }

    @Test
    public void verifyPaymentPageLoadsAfterCheckout() {
        openPaymentWithCartItems("neon-drift");

        Assert.assertEquals(payment.getPageTitleText(), "Payment");
        Assert.assertTrue(payment.isPaymentFormDisplayed());
    }

    @Test
    public void verifyOrderSummaryShowsAddedItems() {
        openPaymentWithCartItems("neon-drift");

        Assert.assertEquals(payment.getOrderItemsCount(), 1);
    }

    @Test
    public void verifyOrderTotalForSingleItem() {
        openPaymentWithCartItems("neon-drift");

        Assert.assertTrue(payment.getOrderTotalText().contains("19.99"));
    }

    @Test
    public void verifyCardNumberAutoFormatting() {
        openPaymentWithCartItems("neon-drift");
        payment.enterCardNumber("4111111111111111");

        Assert.assertEquals(payment.getCardNumberValue(), "4111 1111 1111 1111");
    }

    @Test
    public void verifyExpiryAutoFormatting() {
        openPaymentWithCartItems("neon-drift");
        payment.enterExpiry("1230");

        Assert.assertEquals(payment.getExpiryValue(), "12/30");
    }

    @Test
    public void verifyCvvAllowsOnlyDigitsAndMaxLengthFour() {
        openPaymentWithCartItems("neon-drift");
        payment.enterCvv("12a45");

        Assert.assertEquals(payment.getCvvValue(), "1245");
    }

    @Test
    public void verifyValidationForEmptyCardholderName() {
        openPaymentWithCartItems("neon-drift");
        payment.fillValidPaymentDetails();
        payment.enterCardholderName("");
        payment.clickPayNow();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> !payment.getPayMessage().trim().isEmpty());

        Assert.assertEquals(payment.getPayMessage(), "Please enter the cardholder name.");
    }

    @Test
    public void verifyValidationForInvalidCardNumber() {
        openPaymentWithCartItems("neon-drift");
        payment.fillValidPaymentDetails();
        payment.enterCardNumber("123456789012");
        payment.clickPayNow();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> !payment.getPayMessage().trim().isEmpty());

        Assert.assertEquals(payment.getPayMessage(), "Please enter a valid card number.");
    }

    @Test
    public void verifyValidationForInvalidExpiry() {
        openPaymentWithCartItems("neon-drift");
        payment.fillValidPaymentDetails();
        payment.enterExpiry("01/20");
        payment.clickPayNow();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> !payment.getPayMessage().trim().isEmpty());

        Assert.assertEquals(payment.getPayMessage(), "Please enter a valid expiry date (MM/YY).");
    }

    @Test
    public void verifyValidationForInvalidCvv() {
        openPaymentWithCartItems("neon-drift");
        payment.fillValidPaymentDetails();
        payment.enterCvv("12");
        payment.clickPayNow();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> !payment.getPayMessage().trim().isEmpty());

        Assert.assertTrue(payment.getPayMessage().contains("valid CVV"));
    }

    @Test
    public void verifyValidationForMissingCountry() {
        openPaymentWithCartItems("neon-drift");
        payment.enterCardholderName("Alex Gamer");
        payment.enterCardNumber("4111111111111111");
        payment.enterExpiry("12/30");
        payment.enterCvv("123");
        payment.enterBillingAddress("221B Baker Street");
        payment.enterCity("Hyderabad");
        payment.enterZip("500001");
        payment.clickPayNow();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> !payment.getPayMessage().trim().isEmpty());

        Assert.assertEquals(payment.getPayMessage(), "Please select your country.");
    }

    @Test
    public void verifySuccessfulPaymentRedirectsToOrders() {
        openPaymentWithCartItems("neon-drift");
        payment.fillValidPaymentDetails();
        payment.clickPayNow();

        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(d -> payment.getCurrentUrl().contains("/orders.html?success=1"));

        Assert.assertTrue(payment.getCurrentUrl().contains("/orders.html?success=1"));
    }

    @Test
    public void verifyOrderSummaryForMultipleItems() {
        openPaymentWithCartItems("neon-drift", "iron-legion");

        Assert.assertEquals(payment.getOrderItemsCount(), 2);
    }

    @Test
    public void verifyOrderTotalForMultipleItems() {
        openPaymentWithCartItems("neon-drift", "iron-legion");

        Assert.assertTrue(payment.getOrderTotalText().contains("49.98"));
    }

    @Test
    public void verifyNoCartMessageWhenOpeningPaymentWithEmptyCart() {
        payment.setLoggedInUser(TEST_USER_ID);
        payment.clearCartForUser(TEST_USER_ID);
        payment.openPath(BASE_URL, "/payment.html");

        Assert.assertTrue(payment.isNoCartMessageDisplayed());
        Assert.assertFalse(payment.isPaymentFormDisplayed());
    }

    private void openPaymentWithCartItems(String... gameIds) {
        payment.setLoggedInUser(TEST_USER_ID);
        payment.clearCartForUser(TEST_USER_ID);
        payment.openPath(BASE_URL, "/index.html");

        for (String gameId : gameIds) {
            payment.addGameToCartFromGamePage(BASE_URL, gameId);
        }

        payment.goToPaymentFromCart(BASE_URL);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> payment.getCurrentUrl().contains("/payment.html"));
    }
}
