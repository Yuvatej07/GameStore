package com.example.Pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HomePage {

    WebDriver driver;
    private final WebDriverWait wait;

    // ===== Locators =====
    private By navBar = By.id("nav");
    private By pageTitle = By.className("page-title");
    private By subtitle = By.className("page-subtitle");

    private By searchBox = By.id("search");
    private By resetButton = By.id("reset-search");

    private By gamesGrid = By.id("games-grid");
    private By emptyMessage = By.id("empty");

    private By toastContainer = By.id("toasts");

    // ===== Constructor =====
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ===== Actions =====

    public boolean isNavBarDisplayed() {
        return driver.findElement(navBar).isDisplayed();
    }

    public String getPageTitleText() {
        return driver.findElement(pageTitle).getText();
    }

    public boolean isSearchBoxDisplayed() {
        return driver.findElement(searchBox).isDisplayed();
    }

    public boolean isResetButtonDisplayed() {
        return driver.findElement(resetButton).isDisplayed();
    }

    public boolean isSearchBoxEnabled() {
        return driver.findElement(searchBox).isEnabled();
    }

    public void searchGame(String text) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].value = arguments[1];"
                                + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                                + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                        search, text
                );
                return;
            } catch (StaleElementReferenceException | ElementNotInteractableException ignored) {
                // The app re-renders frequently; retry by resolving the element again.
            }
        }
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                        + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                search, text
        );
    }

    public void clickReset() {
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
    }

    public boolean isGamesGridDisplayed() {
        return driver.findElement(gamesGrid).isDisplayed();
    }

    public boolean isEmptyMessageVisible() {
        Object visible = ((JavascriptExecutor) driver).executeScript(
                "const el = document.getElementById('empty');"
                        + "if (!el) return false;"
                        + "const s = window.getComputedStyle(el);"
                        + "return s.display !== 'none' && s.visibility !== 'hidden' && s.opacity !== '0';"
        );
        return Boolean.TRUE.equals(visible);
    }

    public boolean isToastContainerPresent() {
        return !driver.findElements(toastContainer).isEmpty();
    }

    public String getSearchValue() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(searchBox))
                .getAttribute("value");
    }

    public String getSubtitleText() {
        return driver.findElement(subtitle).getText();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
