package com.example.demowebshop.integrated;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MyInfoChangeNameTests extends BaseTest {

    private static final By MY_INFO_MENU = By.xpath("//span[normalize-space()='My Info']");
    private static final By SAVE_BTN     = By.xpath("//div[@class='orangehrm-horizontal-padding orangehrm-vertical-padding']//button[@type='submit']");
    private static final By TOAST_SUCCESS = By.xpath("//div[contains(@class,'oxd-toast') and contains(.,'Successfully Updated')]");
    private static final By VALIDATION_ERRORS = By.xpath("//span[contains(@class,'oxd-input-group__message')]");
    private static final By FORM_LOADER = By.cssSelector("div.oxd-form-loader");
    private static final String FULLNAME_JSON_PATH = "src/test/resources/_21130349_doxuanhau_testdata.json";

    private static List<FullNameCase> fullNameCases;

    @BeforeAll
    void setupOnce() {
        // Load full name cases from JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            File jsonFile = new File(FULLNAME_JSON_PATH);
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(jsonFile);
            fullNameCases = mapper.convertValue(root.get("fullNameTestCases"), new TypeReference<List<FullNameCase>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Không thể load _21130349_doxuanhau_testdata.json: " + e.getMessage(), e);
        }

        // 1. Tự động login từ BaseTest
        loginAsAdmin();

        // 2. Vào My Info
        wait.until(ExpectedConditions.elementToBeClickable(MY_INFO_MENU)).click();
        
        // Chờ form Personal Details load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));

        System.out.println("✅ ĐÃ LOGIN + VÀO MY INFO (SẴN SÀNG EDIT)");
    }

    @ParameterizedTest
    @MethodSource("fullNameData")
    void changeFullName_MultipleTimes(FullNameCase tc) {

        System.out.println("\n=== Running " + tc.testCaseId + " | " + tc.firstName + " | " + tc.middleName + " | " + tc.lastName + " ===");

        // Đảm bảo đang ở tab Personal Details
        // (Trong trường hợp chạy nhiều test xen kẽ, có thể thêm 0bước click lại MyInfo ở đây nếu cần)

        // Chờ mọi loader biến mất trước khi thao tác form
        wait.until(ExpectedConditions.invisibilityOfElementLocated(FORM_LOADER));

        clearAndSend(By.name("firstName"), tc.firstName);
        clearAndSend(By.name("middleName"), tc.middleName);
        clearAndSend(By.name("lastName"), tc.lastName);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(FORM_LOADER));
        wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(FORM_LOADER));

        boolean hasToast;
        boolean hasError;
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            shortWait.until(d -> !d.findElements(TOAST_SUCCESS).isEmpty() || !d.findElements(VALIDATION_ERRORS).isEmpty());
        } catch (Exception ignored) {
            // fall through to evaluate what is actually present
        }

        hasToast = !driver.findElements(TOAST_SUCCESS).isEmpty();
        hasError = !driver.findElements(VALIDATION_ERRORS).isEmpty();

        if ("success".equalsIgnoreCase(tc.expectedResult)) {
            if (hasError) {
                Assertions.fail(tc.testCaseId + " mong đợi thành công nhưng thấy lỗi validation.");
            }

            // Với case thành công, xác nhận giá trị thực tế đã lưu (không phụ thuộc toast)
            String expectedFirst = sanitize(tc.firstName);
            String expectedMiddle = sanitize(tc.middleName);
            String expectedLast = sanitize(tc.lastName);

            String actualFirst = driver.findElement(By.name("firstName")).getAttribute("value").trim();
            String actualMiddle = driver.findElement(By.name("middleName")).getAttribute("value").trim();
            String actualLast = driver.findElement(By.name("lastName")).getAttribute("value").trim();

            Assertions.assertEquals(expectedFirst, actualFirst, tc.testCaseId + " firstName không khớp sau khi lưu");
            Assertions.assertEquals(expectedMiddle, actualMiddle, tc.testCaseId + " middleName không khớp sau khi lưu");
            Assertions.assertEquals(expectedLast, actualLast, tc.testCaseId + " lastName không khớp sau khi lưu");

            System.out.println(tc.testCaseId + " → PASS (Lưu thành công, giá trị khớp)");
        } else {
            if (hasToast) {
                Assertions.fail(tc.testCaseId + " mong đợi lỗi nhưng lại xuất hiện toast thành công!");
            }
            Assertions.assertTrue(hasError, tc.testCaseId + " mong đợi lỗi nhưng lại không thấy message lỗi!");
            System.out.println(tc.testCaseId + " → PASS (Có lỗi validation như mong đợi)");
        }
    }

    private String sanitize(String value) {
        if (value == null) return "";
        String cleaned = value.replace("\"", "").trim();
        return cleaned.isBlank() ? "" : cleaned;
    }

    private static Stream<FullNameCase> fullNameData() {
        return fullNameCases.stream();
    }

    private void clearAndSend(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        
        // Dùng Ctrl+A Delete để clear sạch hơn .clear()
        el.sendKeys(Keys.CONTROL + "a");
        el.sendKeys(Keys.DELETE);

        if (text != null && !text.isBlank() && !"\"\"".equals(text.trim())) {
            // Xử lý dữ liệu CSV đôi khi có dấu ngoặc kép thừa
            el.sendKeys(text.replace("\"", "").trim());
        }
    }

    // --- DATA CLASS ---
    private static class FullNameCase {
        public String testCaseId;
        public String firstName;
        public String middleName;
        public String lastName;
        public String expectedResult;
    }
}