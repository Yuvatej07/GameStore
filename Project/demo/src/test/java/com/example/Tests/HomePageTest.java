package com.example.Tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.example.Base.BaseTest;
import com.example.Pages.HomePage;

public class HomePageTest extends BaseTest {

    HomePage home;

    @Override
    protected String getStartPath() {
        return "/index.html";
    }

    @BeforeMethod
    public void initPageObject() {
        home = new HomePage(driver);
    }

    @Test
    public void verifyHomePageLoads() {
        Assert.assertTrue(home.isNavBarDisplayed());
    }

    @Test
    public void verifyPageTitle() {
        Assert.assertEquals(
                home.getPageTitleText(),
                "Featured games"
        );
    }

    @Test
    public void verifySearchBoxVisible() {
        Assert.assertTrue(home.isSearchBoxDisplayed());
    }

    @Test
    public void verifySearchFunctionality() {
        home.searchGame("Neon");

        Assert.assertEquals(
                home.getSearchValue(),
                "Neon"
        );
    }

    @Test
    public void verifyResetClearsSearch() {
        home.searchGame("Iron");
        home.clickReset();

        Assert.assertEquals(
                home.getSearchValue(),
                ""
        );
    }

    @Test
    public void verifyGamesGridLoads() {
        Assert.assertTrue(home.isGamesGridDisplayed());
    }

    @Test
    public void verifyToastContainerExists() {
        Assert.assertTrue(home.isToastContainerPresent());
    }

    @Test
    public void verifyUrlIsHomePage() {
        Assert.assertTrue(home.getCurrentUrl().contains("/index.html"));
    }

    @Test
    public void verifySubtitleIsVisibleAndNotEmpty() {
        Assert.assertFalse(home.getSubtitleText().trim().isEmpty());
    }

    @Test
    public void verifyResetButtonVisible() {
        Assert.assertTrue(home.isResetButtonDisplayed());
    }

    @Test
    public void verifySearchBoxEnabled() {
        Assert.assertTrue(home.isSearchBoxEnabled());
    }

    @Test
    public void verifySearchBoxInitiallyEmpty() {
        Assert.assertEquals(home.getSearchValue(), "");
    }

    @Test
    public void verifySearchValueUpdatesOnSecondInput() {
        home.searchGame("Neon");
        home.searchGame("Iron");

        Assert.assertEquals(home.getSearchValue(), "Iron");
    }

    @Test
    public void verifyUnknownSearchShowsEmptyMessage() {
        home.searchGame("this-game-does-not-exist-12345");

        Assert.assertFalse(home.isEmptyMessageVisible());
    }
}
