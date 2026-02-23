package com.example.Base;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;
    private static final String BASE_URL = System.getProperty("base.url", "http://127.0.0.1:5501");

    @BeforeMethod
    public void setup() {
        // Silence known CDP compatibility warnings when Chrome version moves ahead of Selenium.
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.SEVERE);
        Logger.getLogger("org.openqa.selenium.chromium.ChromiumDriver").setLevel(Level.SEVERE);

        ChromeOptions options = new ChromeOptions();
        if ("true".equalsIgnoreCase(System.getenv("CI"))) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));

        driver.get(BASE_URL + getStartPath());

        
    }

    protected String getStartPath() {
        return "/";
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
