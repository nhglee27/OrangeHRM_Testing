package com.example.demowebshop._21130349_doxuanhau_lab7;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    // Không còn protected static WebDriver driver nữa
    // Mỗi test class tự quản lý driver của nó

    @BeforeAll
    static void setupDriverManager() {
        WebDriverManager.firefoxdriver().setup();
    }

    // Helper method để tạo driver khi cần
    protected WebDriver createDriver() {
        FirefoxOptions options = new FirefoxOptions();
        // options.addArguments("--headless"); // bật nếu cần
        WebDriver driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        // delete all data of browser
        driver.manage().deleteAllCookies();

        System.out.println("KHỞI TẠO FIREFOX DRIVER THÀNH CÔNG!");
        return driver;
    }

    protected void sleep(int seconds) {
        try { Thread.sleep(seconds * 1000L); } catch (InterruptedException ignored) {}
    }
}