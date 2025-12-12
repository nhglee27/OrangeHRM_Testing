package com.example.demowebshop._21130349_doxuanhau_lab7;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    @BeforeAll
    static void setupDriverManager() {
        WebDriverManager.chromedriver().setup();
    }

    protected WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new"); // Bật nếu muốn chạy headless (Chrome 109+)

        WebDriver driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().deleteAllCookies();

        System.out.println("KHỞI TẠO CHROME DRIVER THÀNH CÔNG!");
        return driver;
    }

    protected void sleep(int seconds) {
        try { Thread.sleep(seconds * 1000L); } catch (InterruptedException ignored) {}
    }
}
