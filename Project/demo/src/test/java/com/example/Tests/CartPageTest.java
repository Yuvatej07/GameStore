package com.example.Tests;

import java.time.Duration;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.Base.BaseTest;
import com.example.Pages.CartPage;

public class CartPageTest extends BaseTest {

    private static final String TEST_USER_ID = "cart-test-user";
    private static final String BASE_URL = System.getProperty("base.url", "http://127.0.0.1:5501");
    private CartPage cart;

    @Override
    protected String getStartPath() {
        return "/cart.html";
    }

    @BeforeMethod
    public void initPageObject() {
        cart = new CartPage(driver);
    }

    @Test
    public void verifyCartPageLoads() {
        prepareEmptyCart();

        Assert.assertEquals(cart.getPageTitleText(), "Your cart");
        Assert.assertTrue(cart.isCartListPresent());
    }

    @Test
    public void verifyEmptyCartMessageShown() {
        prepareEmptyCart();

        Assert.assertTrue(cart.isEmptyCartMessageDisplayed());
    }

    @Test
    public void verifyFooterHiddenWhenCartIsEmpty() {
        prepareEmptyCart();

        Assert.assertFalse(cart.isCartFooterDisplayed());
    }

    @Test
    public void verifySingleItemIsRendered() {
        prepareCartByAddingGames("neon-drift");

        Assert.assertEquals(cart.getCartItemsCount(), 1);
        Assert.assertTrue(cart.isCartItemPresent("neon-drift"));
    }

    @Test
    public void verifyGrandTotalForSingleItem() {
        prepareCartByAddingGames("neon-drift");

        Assert.assertTrue(cart.getGrandTotalText().contains("19.99"));
    }

    @Test
    public void verifyIncreaseQuantityUpdatesCount() {
        prepareCartByAddingGames("neon-drift");
        cart.clickIncrease("neon-drift");

        Assert.assertEquals(cart.getQuantityForItem("neon-drift"), 2);
    }

    @Test
    public void verifyDecreaseQuantityDoesNotGoBelowOne() {
        prepareCartByAddingGames("neon-drift");
        cart.clickDecrease("neon-drift");

        Assert.assertEquals(cart.getQuantityForItem("neon-drift"), 1);
    }

    @Test
    public void verifyRemoveItemMakesCartEmpty() {
        prepareCartByAddingGames("neon-drift");
        cart.clickRemove("neon-drift");

        Assert.assertEquals(cart.getCartItemsCount(), 0);
        Assert.assertTrue(cart.isEmptyCartMessageDisplayed());
    }

    @Test
    public void verifyGrandTotalForMultipleItems() {
        prepareCartByAddingGames("neon-drift", "neon-drift", "iron-legion");

        Assert.assertTrue(cart.getGrandTotalText().contains("69.97"));
    }

    @Test
    public void verifyCheckoutNavigatesToPaymentPage() {
        prepareCartByAddingGames("neon-drift");
        cart.clickCheckout();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> cart.getCurrentUrl().contains("/payment.html"));

        Assert.assertTrue(cart.getCurrentUrl().contains("/payment.html"));
    }

    @Test
    public void verifyFooterVisibleWhenCartHasItems() {
        prepareCartByAddingGames("neon-drift");

        Assert.assertTrue(cart.isCartFooterDisplayed());
    }

    @Test
    public void verifyRemovingSingleItemHidesFooter() {
        prepareCartByAddingGames("neon-drift");
        cart.clickRemove("neon-drift");

        Assert.assertFalse(cart.isCartFooterDisplayed());
    }

    @Test
    public void verifyRemovingOneItemKeepsOtherItemInCart() {
        prepareCartByAddingGames("neon-drift", "iron-legion");
        cart.clickRemove("neon-drift");

        Assert.assertEquals(cart.getCartItemsCount(), 1);
        Assert.assertTrue(cart.isCartItemPresent("iron-legion"));
    }

    private void prepareEmptyCart() {
        cart.setLoggedInUser(TEST_USER_ID);
        cart.clearCartForUser(TEST_USER_ID);
        cart.openPath(BASE_URL, "/cart.html");
    }

    private void prepareCartByAddingGames(String... gameIds) {
        cart.setLoggedInUser(TEST_USER_ID);
        cart.clearCartForUser(TEST_USER_ID);
        cart.openPath(BASE_URL, "/index.html");

        for (String gameId : gameIds) {
            cart.addGameToCartFromGamePage(BASE_URL, gameId);
        }

        cart.openPath(BASE_URL, "/cart.html");
    }
}
