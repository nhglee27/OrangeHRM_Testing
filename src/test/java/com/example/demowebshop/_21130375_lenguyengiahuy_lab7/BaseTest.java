package com.example.demowebshop._21130375_lenguyengiahuy_lab7;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static String baseUrl;
    protected static JsonNode testData;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();

        try {
            ObjectMapper mapper = new ObjectMapper();
            testData = mapper.readTree(new File("src/test/resources/_21130375_lenguyengiahuy_lab7_testdata.json")); // Nhớ sửa tên file nếu cần
            baseUrl = testData.get("baseUrl").asText();
        } catch (IOException e) {
            throw new RuntimeException("❌ Không đọc được file json");
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.setBinary("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe"); // Uncomment nếu dùng Brave

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        // Giảm implicit wait xuống vì ta sẽ dùng Explicit Wait là chính
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3)); 
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        System.out.println("✅ WebDriver started!");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Driver closed!");
        }
    }

    protected static void loginAsAdmin() {
        // Kiểm tra xem Avatar User (góc trên phải) có hiện không?
        // Nếu có -> Nghĩa là đã đăng nhập rồi -> Return luôn.
        try {
            if (!driver.findElements(By.className("oxd-userdropdown-img")).isEmpty()) {
                // System.out.println("ℹ️ Đã đăng nhập sẵn (Session active).");
                return;
            }
        } catch (Exception ignored) {}

        // Nếu chưa thấy Avatar, thử check URL kỹ hơn chút nữa
        String url = driver.getCurrentUrl();
        if (url.contains("/dashboard") || url.contains("/admin/") || url.contains("/pim/") || url.contains("/leave/")) {
            return;
        }

        driver.get(baseUrl);

        try {
            // Đợi ô username xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            
            String u = testData.get("admin").get("username").asText();
            String p = testData.get("admin").get("password").asText();

            driver.findElement(By.name("username")).sendKeys(u);
            driver.findElement(By.name("password")).sendKeys(p);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            // Đợi vào được Dashboard
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("oxd-userdropdown-img")));
            System.out.println("✅ Đăng nhập thành công!");
            
        } catch (Exception e) {
            // Nếu đang ở trang login mà bị redirect thẳng vào trong (trường hợp hiếm)
            if (driver.getCurrentUrl().contains("dashboard")) {
                return;
            }
            throw new RuntimeException("❌ Lỗi đăng nhập: " + e.getMessage());
        }
    }
}