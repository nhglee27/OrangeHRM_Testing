package com.example.demowebshop._22130031_HaManhCuong_lab7;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static String baseUrl;
    protected static JsonNode testData;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Đọc file JSON
            testData = mapper.readTree(new File("src/test/resources/_22130031_HaManhCuong_lab7_testdata.json"));
            baseUrl = testData.get("baseUrl").asText();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể đọc file data json: " + e.getMessage());
        }

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở trang web
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}