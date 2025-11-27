package com.example.demowebshop;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static String baseUrl = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    @BeforeAll
    protected static void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("✅ WebDriver started!");
    }

    @AfterAll
    protected static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Driver closed!");
        }
    }

    protected static void loginAsAdmin() {
        // 1. Nếu URL hiện tại đã chứa dashboard hoặc viewSystemUsers... -> Đã login
        try {
            if (driver.getCurrentUrl().contains("/dashboard") || 
                driver.getCurrentUrl().contains("/admin/") || 
                driver.getCurrentUrl().contains("/pim/")) {
                return;
            }
        } catch (Exception ignored) {}

        // 2. Vào trang login
        driver.get(baseUrl);

        // 3. Xử lý trường hợp đã login session cũ -> Bị redirect thẳng vào Dashboard
        try {
            // Chờ một chút để xem nó có redirect không hoặc hiện username
            // Dùng ExpectedConditions.or để bắt cả 2 trường hợp:
            // Case A: Hiện ô username (Chưa login)
            // Case B: Hiện menu dashboard (Đã login rồi, bị redirect)
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.name("username")),
                ExpectedConditions.urlContains("/dashboard")
            ));
        } catch (Exception e) {
            System.out.println("⚠️ Timeout chờ load trang Login/Dashboard");
        }

        // 4. Nếu đang ở Dashboard rồi thì return luôn, không cần nhập pass
        if (driver.getCurrentUrl().contains("/dashboard")) {
            System.out.println("ℹ️ Đã đăng nhập sẵn (Session active).");
            return;
        }

        // 5. Nếu vẫn ở màn hình Login (thấy username), thực hiện đăng nhập
        try {
            WebElement userField = driver.findElement(By.name("username"));
            if (userField.isDisplayed()) {
                userField.sendKeys("Admin");
                driver.findElement(By.name("password")).sendKeys("admin123");
                driver.findElement(By.cssSelector("button[type='submit']")).click();
                wait.until(ExpectedConditions.urlContains("/dashboard"));
                System.out.println("✅ Đăng nhập Admin thành công.");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi trong quá trình đăng nhập: " + e.getMessage());
        }
    }
}