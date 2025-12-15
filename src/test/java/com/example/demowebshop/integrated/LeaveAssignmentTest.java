package com.example.demowebshop.integrated;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
        navigateToLeaveEntitlements();
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
    @Test
    @Order(1)
    void testAssignLeaveMissingName() {
        System.out.println("🔹 TC_LA_002: Gán phép thiếu tên nhân viên (Negative)");

        String leaveType = testData.get("leave").get("type").asText();
        String amount = testData.get("leave").get("partialAmount").asText();

        // Bỏ trống Employee Name, chỉ chọn loại phép và số lượng
        selectDropdown("Leave Type", leaveType);
        driver.findElement(By.xpath("//label[text()='Entitlement']/../following-sibling::div//input")).sendKeys(amount);

        // Click Save (Nút save có thể click được ngay cả khi form chưa valid)
        // Dùng xpath chính xác tới nút Save
        driver.findElement(By.xpath("//button[text()=' Save ']")).click();

        // Verify Lỗi Required
        String expectedError = testData.get("messages").get("required").asText();
        String actualError = getFieldError("Employee Name");
        assertEquals(expectedError, actualError, "Lỗi hiển thị không đúng!");
    }

    @Test
    @Order(2)
    void testAssignLeaveNegativeDays() throws InterruptedException {
        System.out.println("🔹 TC_LA_003: Gán phép số âm (Negative)");

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


    @Test
    @Order(3)
    void testAdminAssignLeaveSuccess() throws InterruptedException {
        System.out.println("🔹 TC_LA_001: Admin gán ngày phép thành công");

        String empName = testData.get("employee").get("fullName").asText();
        String leaveType = testData.get("leave").get("type").asText();
        String amount = testData.get("leave").get("entitlementAmount").asText();

        typeAutocomplete("Employee Name", empName);
        selectDropdown("Leave Type", leaveType);

        WebElement entInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Entitlement']/../following-sibling::div//input")));
        entInput.sendKeys(amount);

        clickSave();
        verifySuccessMessage();
    }

    // ================= HELPER METHODS =================

    private void typeAutocomplete(String label, String text) throws InterruptedException {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='" + label + "']/../following-sibling::div//input")));
        input.sendKeys(text);
        Thread.sleep(3000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='listbox']//div[1]"))).click();
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
            wait.withTimeout(java.time.Duration.ofSeconds(2))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()=' Confirm ']")))
                    .click();
        } catch (Exception ignored) {}
    }

    private String getFieldError(String fieldLabel) {
        return driver.findElement(By.xpath("//label[text()='" + fieldLabel + "']/../following-sibling::span")).getText();
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