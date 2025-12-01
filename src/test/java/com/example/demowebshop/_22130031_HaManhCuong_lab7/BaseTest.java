package com.example.demowebshop._22130031_HaManhCuong_lab7;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class BaseTest {

    protected static WebDriver driver;
    protected static JsonNode testData;

    @BeforeMethod
    public void setup() {
        // CÁCH 1: Xóa dòng này đi nếu dùng Selenium 4.6 trở lên
        // WebDriverManager.chromedriver().setup();

        // CÁCH 2: Nếu vẫn muốn dùng WebDriverManager, hãy thử clear cache của nó trong máy tính
        // (Xóa thư mục C:\Users\mcuon\.cache\selenium) để nó tải lại bản driver phù hợp.

        try {
            ObjectMapper mapper = new ObjectMapper();
            testData = mapper.readTree(new File("src/test/resources/_22130031_HaManhCuong_lab7_testdata.json"));
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file data json: " + e.getMessage());
        }

        // Khởi tạo driver (Selenium Manager sẽ tự lo driver)
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Mở trang web
        String baseUrl = testData.get("baseUrl").asText();
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}