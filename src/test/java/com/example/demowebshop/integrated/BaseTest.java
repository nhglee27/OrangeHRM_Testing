package com.example.demowebshop.integrated;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class BaseTest {

    // --- CẤU HÌNH ---
    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static JsonNode testData;
    protected static String baseUrl;

    // Đường dẫn lưu file (Dùng dấu / cuối cùng để chắc chắn là thư mục)
    private static final String RECORDING_PATH = "test-recordings/";
    private static final String SCREENSHOT_PATH = "test-screenshots/";
    private static final String DATA_JSON_PATH = "src/test/resources/testdata.json";

    private ScreenRecorder screenRecorder;

    // --- 1. SETUP & TEARDOWN TOÀN CỤC ---

    @BeforeAll
    public static void setupSuite() {
        WebDriverManager.firefoxdriver().setup();

        try {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File(DATA_JSON_PATH);
            if (jsonFile.exists()) {
                testData = mapper.readTree(jsonFile);
                baseUrl = testData.has("baseUrl") ? testData.get("baseUrl").asText() : "https://opensource-demo.orangehrmlive.com/";
                System.out.println("✅ Đã load dữ liệu test từ JSON.");
            } else {
                System.out.println("⚠️ Không tìm thấy file JSON, dùng URL mặc định.");
                baseUrl = "https://opensource-demo.orangehrmlive.com/";
            }
        } catch (IOException e) {
            System.err.println("❌ Lỗi đọc file JSON: " + e.getMessage());
        }

        FirefoxOptions options = new FirefoxOptions();
        
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().deleteAllCookies();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("✅ KHỞI TẠO FIREFOX DRIVER THÀNH CÔNG!");
    }

    @AfterAll
    public static void tearDownSuite() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Driver closed!");
        }
    }

    // --- 2. QUAY VIDEO & SETUP ---

    @BeforeEach
    public void startRecording(TestInfo testInfo) {
        // Đảm bảo thư mục tồn tại
        createDir(RECORDING_PATH);
        createDir(SCREENSHOT_PATH);

        try {
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();

            // QUAN TRỌNG: Làm sạch tên file để tránh lỗi hệ thống (xóa dấu : / \ space...)
            String testName = sanitizeFilename(testInfo.getDisplayName());

            screenRecorder = new SpecializedScreenRecorder(gc,
                    new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                            FrameRateKey, Rational.valueOf(30)),
                    null, 
                    new File(RECORDING_PATH),
                    testName);

            screenRecorder.start();
            System.out.println("🎥 Bắt đầu quay: " + testName);
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi start quay video: " + e.getMessage());
        }
    }

    @AfterEach
    public void stopRecordingAndScreenshot(TestInfo testInfo) {
        // Làm sạch tên file trước khi dùng
        String testName = sanitizeFilename(testInfo.getDisplayName());
        
        // 1. Chụp màn hình
        captureScreenshot(testName);

        // 2. Dừng quay video
        try {
            if (screenRecorder != null) {
                screenRecorder.stop();
                // Không cần in đường dẫn ở đây vì SpecializedScreenRecorder đã xử lý file
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi stop quay video: " + e.getMessage());
        }
    }

    // --- 3. TIỆN ÍCH ---

    /**
     * Hàm làm sạch tên file: Chuyển các ký tự đặc biệt thành dấu gạch dưới
     * Ví dụ: "TC1: Login Test" -> "TC1__Login_Test"
     */
    private String sanitizeFilename(String name) {
        // Thay thế: dấu cách, ngoặc đơn, hai chấm, gạch chéo... bằng dấu _
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    protected void captureScreenshot(String fileName) {
        try {
            if (driver == null) return;

            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String finalName = fileName + "_" + timestamp + ".png";
            File destination = new File(SCREENSHOT_PATH + finalName);
            
            FileUtils.copyFile(source, destination);
            System.out.println("📸 Screenshot saved: " + destination.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Lỗi chụp màn hình: " + e.getMessage());
        }
    }

    protected void sleep(int seconds) {
        try { Thread.sleep(seconds * 1000L); } catch (InterruptedException ignored) {}
    }

    protected void loginAsAdmin() {
        try {
            if (!driver.findElements(By.className("oxd-userdropdown-img")).isEmpty()) return;
            if (driver.getCurrentUrl().contains("/dashboard")) return;
        } catch (Exception ignored) {}

        driver.get(baseUrl);

        try {
            String u = "Admin";
            String p = "admin123";
            if (testData != null && testData.has("admin")) {
                u = testData.get("admin").get("username").asText();
                p = testData.get("admin").get("password").asText();
            }

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            driver.findElement(By.name("username")).sendKeys(u);
            driver.findElement(By.name("password")).sendKeys(p);
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("oxd-userdropdown-img")));
            System.out.println("✅ Đã đăng nhập: " + u);
        } catch (Exception e) {
            throw new RuntimeException("❌ Lỗi đăng nhập: " + e.getMessage());
        }
    }

    // --- 4. RECORDER CLASS ---
    public static class SpecializedScreenRecorder extends ScreenRecorder {
        private String name;

        public SpecializedScreenRecorder(GraphicsConfiguration cfg, Format fileFormat, Format screenFormat, Format mouseFormat, Format audioFormat, File movieFolder, String name) throws IOException, AWTException {
            super(cfg, fileFormat, screenFormat, mouseFormat, audioFormat);
            this.name = name;
            this.movieFolder = movieFolder;
        }

        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            if (!movieFolder.exists()) {
                movieFolder.mkdirs();
            } else if (!movieFolder.isDirectory()) {
                throw new IOException("\"" + movieFolder + "\" is not a directory.");
            }
            
            // Ép buộc đuôi .avi vào tên file
            File f = new File(movieFolder, name + ".avi");
            System.out.println("📼 File video sẽ được lưu tại: " + f.getAbsolutePath());
            return f;
        }
    }
}