package com.example.demowebshop;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    // Biến driver toàn cục để các test class khác kế thừa
    protected static WebDriver driver;

    // Có thể đổi browser bằng System property: mvn test -Dbrowser=firefox
    private String browser = System.getProperty("browser", "chrome").toLowerCase();

    @BeforeAll
    static void setupDriverManager() {
        // Chỉ khởi tạo 1 lần duy nhất
        // WebDriverManager sẽ tự tải driver mới nhất phù hợp với Chrome/Edge/Firefox của bạn
    }

    @BeforeEach
    void setup() {
        driver = createDriver(browser);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        System.out.println("Khởi động " + browser.toUpperCase() + " thành công!");
    }

    private WebDriver createDriver(String browserName) {
        return switch (browserName) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--start-maximized");
                // options.addArguments("--headless"); // bỏ comment nếu chạy CI/CD
                yield new ChromeDriver(options);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--disable-notifications");
                yield new EdgeDriver(options);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                yield new FirefoxDriver(options);
            }
            default -> throw new IllegalArgumentException("Browser không hỗ trợ: " + browserName);
        };
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Đã đóng browser");
        }
    }

    // Helper để các test class dùng nhanh (tùy chọn)
    protected void sleep(int seconds) {
        try { Thread.sleep(seconds * 1000L); } catch (InterruptedException ignored) {}
    }
}