package com.example.demowebshop.integrated;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeaveAssignmentTest extends BaseTest {

    @BeforeEach
    void preCondition() {
        if (driver == null) setupSuite();

        // Đảm bảo đang login admin
        if (!driver.getCurrentUrl().contains("dashboard")) {
            loginAsAdmin();
        }

        // Điều hướng và reset form trước mỗi test case

    }

    // --- TC 00: TẠO TÀI KHOẢN NHÂN VIÊN (PRE-CONDITION) ---
    @Test
    @Order(0)
    void testCreateEmployee() throws InterruptedException {
        System.out.println("🔹 TC_LA_000: Tạo tài khoản nhân viên mới (Pre-condition)");

        // 1. Vào PIM -> Add Employee
        navigateToPIM();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        waitForLoader();
        // 2. Điền thông tin (Lấy từ JSON)
        String firstName = testData.get("employee").get("firstName").asText();
        String middleName = testData.get("employee").get("middleName").asText();
        String lastName = testData.get("employee").get("lastName").asText();
        String username = testData.get("employee").get("username").asText();
        String password = testData.get("employee").get("password").asText();
        String employeeId = testData.get("employee").get("employeeId").asText();

        // Nhập First Name, Middle Name, Last Name
        driver.findElement(By.name("firstName")).sendKeys(firstName);
        driver.findElement(By.name("middleName")).sendKeys(middleName);
        driver.findElement(By.name("lastName")).sendKeys(lastName);
        WebElement idInput = driver.findElement(By.xpath("//label[text()='Employee Id']/../following-sibling::div//input"));
        idInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        idInput.sendKeys(employeeId);

        // Bật nút "Create Login Details"
        driver.findElement(By.xpath("//span[contains(@class, 'oxd-switch-input')]")).click();

        // Điền Login Details
        WebElement userInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Username']/../following-sibling::div//input")));
        userInput.sendKeys(username);
        // Password fields
        WebElement passInput = driver.findElement(By.xpath("//label[text()='Password']/../following-sibling::div//input"));
        WebElement confirmPassInput = driver.findElement(By.xpath("//label[text()='Confirm Password']/../following-sibling::div//input"));

        passInput.sendKeys(password);
        confirmPassInput.sendKeys(password);

        // Save
        driver.findElement(By.xpath("//button[text()=' Save ']")).click();
        verifySuccessMessage();

        System.out.println("✅ Đã tạo nhân viên: " + firstName + " " + middleName + " " + lastName);

        // Quay lại Dashboard để chuẩn bị cho các TC sau
        driver.get(testData.get("baseUrl").asText());
    }

    @Test
    @Order(1)
    void testAdminAssignLeaveSuccess() throws InterruptedException {

        System.out.println("🔹 TC_LA_001: Admin gán ngày phép thành công");
        navigateToLeaveEntitlements();
        String empName = testData.get("employee").get("fullName").asText();
        String leaveType = testData.get("leave").get("type").asText();
        String amount = testData.get("leave").get("entitlementAmount").asText();

        typeAutocomplete("Employee Name", empName);
        selectDropdown("Leave Type", leaveType);

        WebElement entInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Entitlement']/../following-sibling::div//input")));
        entInput.sendKeys(amount);

        clickSave();
    }

    @Test
    @Order(2)
    void testAssignLeaveMissingName() {
        System.out.println("🔹 TC_LA_002: Gán phép thiếu tên nhân viên (Negative)");
        navigateToLeaveEntitlements();
        String leaveType = testData.get("leave").get("type").asText();
        String amount = testData.get("leave").get("partialAmount").asText();

        // Chỉ chọn loại phép và số lượng, bỏ trống tên
        selectDropdown("Leave Type", leaveType);
        driver.findElement(By.xpath("//label[text()='Entitlement']/../following-sibling::div//input")).sendKeys(amount);

        // Click Save (Nút save có thể click được ngay cả khi form chưa valid)
        driver.findElement(By.xpath("//button[text()=' Save ']")).click();

        // Verify Lỗi Required
        String expectedError = testData.get("messages").get("required").asText();
        String actualError = getFieldError("Employee Name");
        assertEquals(expectedError, actualError, "Lỗi hiển thị không đúng!");
    }

    @Test
    @Order(3)
    void testAssignLeaveNegativeDays() throws InterruptedException {
        System.out.println("🔹 TC_LA_003: Gán phép số âm (Negative)");
        navigateToLeaveEntitlements();
        String empName = testData.get("employee").get("fullName").asText();
        String leaveType = testData.get("leave").get("type").asText();
        String negativeAmount = testData.get("leave").get("negativeAmount").asText();

        typeAutocomplete("Employee Name", empName);
        selectDropdown("Leave Type", leaveType);

        driver.findElement(By.xpath("//label[text()='Entitlement']/../following-sibling::div//input")).sendKeys(negativeAmount);

        driver.findElement(By.xpath("//button[text()=' Save ']")).click();

        // Verify Lỗi Format
        String expectedError = testData.get("messages").get("formatError").asText();
        String actualError = getFieldError("Entitlement");
        assertEquals(expectedError, actualError, "Thông báo lỗi số âm không đúng!");
    }

    // ================= HELPER METHODS =================
    private void navigateToPIM() {
        if (driver.getCurrentUrl().contains("viewEmployeeList")) return;

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[normalize-space()='Employee Information']")));
    }
    private void navigateToLeaveEntitlements() {
        // Nếu đang ở trang này rồi thì Refresh để reset form
        if (driver.getCurrentUrl().contains("addLeaveEntitlement")) {
            driver.navigate().refresh();
        } else {
            // Nếu chưa ở trang này thì điều hướng vào
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[normalize-space()='Entitlements']"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Add Entitlements']"))).click();
        }
    }
    private void waitForLoader() {
        try {
            wait.withTimeout(Duration.ofSeconds(2)).until(ExpectedConditions.visibilityOfElementLocated(By.className("oxd-form-loader")));
            wait.withTimeout(Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader")));
        } catch (Exception ignored) {}
    }
    private void typeAutocomplete(String label, String text) throws InterruptedException {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='" + label + "']/../following-sibling::div//input")));
        // Xóa dữ liệu cũ
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(text);
        Thread.sleep(3000);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='listbox']//div[1]"))).click();
        } catch (TimeoutException e) {
            // Nếu không thấy gợi ý, thử Enter (cho trường hợp Invalid Employee)
            input.sendKeys(Keys.ENTER);
        }
    }

    private void selectDropdown(String label, String optionText) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()='" + label + "']/../following-sibling::div//div[@class='oxd-select-text-input']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='option']//span[text()='" + optionText + "']"))).click();
    }

    private void clickSave() {
        driver.findElement(By.xpath("//button[text()=' Save ']")).click();
        try {
            wait.withTimeout(Duration.ofSeconds(2))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()=' Confirm ']")))
                    .click();
        } catch (Exception ignored) {}
    }

    private String getFieldError(String fieldLabel) {
        try {
            return driver.findElement(By.xpath("//label[text()='" + fieldLabel + "']/../following-sibling::span")).getText();
        } catch (Exception e) { return ""; }
    }

    private void verifySuccessMessage() {
        String expectedMsg = testData.get("messages").get("saved").asText();
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")));
            assertTrue(toast.getText().contains(expectedMsg) || toast.getText().contains("Success"));
        } catch (Exception e) {
            assertTrue(driver.getPageSource().contains(expectedMsg) || driver.getPageSource().contains("Successfully"), "Không thấy thông báo thành công!");
        }
    }
}