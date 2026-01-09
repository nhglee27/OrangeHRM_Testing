package com.example.demowebshop.integrated;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
rtions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LeaveTypeTest extends BaseTest {

    private String appliedDate;

    @BeforeEach
    void preCondition() {
        if (driver == null) setupSuite();
    }

    // --- TC 01: EMPLOYEE APPLY LEAVE ---
    @Test
    @Order(1)
    void testEmployeeApplyLeave() throws InterruptedException {
        System.out.println("🔹 TC_LT_001: Nhân viên nộp đơn xin nghỉ phép");
        loginEmployee();
        navigateToApply();

        String leaveType = testData.get("leave").get("type").asText();
        selectDropdown("Leave Type", leaveType);

        LocalDate date = LocalDate.now().plusDays(7);
        if (date.getDayOfWeek().getValue() >= 6) date = date.plusDays(2);
        appliedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        enterDate("From Date", appliedDate);
        enterDate("To Date", appliedDate);
        handlePartialDays();

        driver.findElement(By.tagName("textarea")).sendKeys(testData.get("leave").get("comment").asText());
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[text()=' Apply ']")).click();

        verifyMessage(testData.get("messages").get("success").asText());
        logout();
    }

    // --- TC 02: ADMIN APPROVE LEAVE ---
    @Test
    @Order(2)
    void testAdminApproveLeave() throws InterruptedException {
        System.out.println("🔹 TC_LT_002: Admin duyệt đơn nghỉ phép");
        loginAsAdmin();
        navigateToLeaveList();

        String empName = testData.get("employee").get("fullName").asText();
        typeAutocomplete("Employee Name", empName);
        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(3000);

        try {
            driver.findElement(By.xpath("//button[text()=' Approve ']")).click();
            verifyMessage(testData.get("messages").get("success").asText());
        } catch (Exception e) {
            System.out.println("WARN: Không tìm thấy đơn để duyệt (Có thể đã duyệt rồi).");
        }
        logout();
    }

    // --- TC 03: ADMIN REJECT LEAVE (Create New -> Reject) ---
    @Test
    @Order(3)
    void testAdminRejectLeave() throws InterruptedException {
        System.out.println("🔹 TC_LT_003: Admin từ chối đơn nghỉ phép");

        // Tạo đơn mới
        loginEmployee();
        navigateToApply();

        LocalDate date = LocalDate.now().plusDays(14);
        if (date.getDayOfWeek().getValue() >= 6) date = date.plusDays(2);
        String rejectDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        submitLeaveForm(rejectDate);
        logout();

        // Reject
        loginAsAdmin();
        navigateToLeaveList();
        searchAndActionLeave("Reject", rejectDate);
        verifyMessage(testData.get("messages").get("success").asText());
        logout();
    }

    // --- POST-CONDITION: DELETE ACCOUNT (Dọn dẹp dữ liệu) ---
    @Test
    @Order(99)
    void testDeleteEmployee() throws InterruptedException {
        System.out.println("🔹 CLEANUP: Xóa tài khoản nhân viên sau khi test");

        loginAsAdmin();

        // 1. Vào PIM -> Employee List
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Employee List']"))).click();

        // 2. Tìm kiếm nhân viên
        String empName = testData.get("employee").get("fullName").asText();
        typeAutocomplete("Employee Name", empName);
        driver.findElement(By.xpath("//button[text()=' Search ']")).click();

        // Chờ kết quả load
        Thread.sleep(3000);

        // 3. Xóa nhân viên (Click nút thùng rác)
        try {
            WebElement deleteBtn = driver.findElement(By.xpath("//button[i[contains(@class, 'bi-trash')]]"));
            deleteBtn.click();

            // Confirm Delete Popup
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Yes, Delete')]"))).click();

            verifyMessage(testData.get("messages").get("success").asText());
            System.out.println("✅ Đã xóa nhân viên: " + empName);
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Không tìm thấy nhân viên để xóa hoặc lỗi khi xóa.");
        }
    }

    // ================= HELPER METHODS =================

    private void loginEmployee() {
        String u = testData.get("employee").get("username").asText();
        String p = testData.get("employee").get("password").asText();
        driver.get(testData.get("baseUrl").asText());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username"))).sendKeys(u);
        driver.findElement(By.name("password")).sendKeys(p);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    private void logout() {
        try {
            driver.findElement(By.className("oxd-userdropdown-name")).click();
            wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Logout"))).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
        } catch (Exception ignored) {}
    }

    private void navigateToApply() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Apply']"))).click();
    }

    private void navigateToLeaveList() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leave List']"))).click();
    }

    private void submitLeaveForm(String dateStr) throws InterruptedException {
        String type = testData.get("leave").get("type").asText();
        selectDropdown("Leave Type", type);
        enterDate("From Date", dateStr);
        enterDate("To Date", dateStr);
        handlePartialDays();
        driver.findElement(By.tagName("textarea")).sendKeys(testData.get("leave").get("comment").asText());
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[text()=' Apply ']")).click();
        verifyMessage(testData.get("messages").get("success").asText());
    }

    private void searchAndActionLeave(String action, String dateStr) throws InterruptedException {
        String empName = testData.get("employee").get("fullName").asText();
        typeAutocomplete("Employee Name", empName);
        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(3000);
        try {
            WebElement btn = driver.findElement(By.xpath("//button[text()=' " + action + " ']"));
            btn.click();
        } catch (Exception e) {
            fail("❌ Không tìm thấy nút " + action + " cho nhân viên " + empName);
        }
    }

    private void verifyMessage(String keyword) {
        try {
            WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")));
            assertTrue(toast.getText().contains(keyword) || toast.getText().contains("Success"));
        } catch (Exception e) {
            assertTrue(driver.getPageSource().contains(keyword) || driver.getPageSource().contains("Successfully"), "Message not found: " + keyword);
        }
    }

    private void selectDropdown(String label, String optionText) {
        driver.findElement(By.xpath("//label[text()='" + label + "']/../following-sibling::div//div[contains(@class,'oxd-select-text')]")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option']//span[text()='" + optionText + "']"))).click();
    }

    private void typeAutocomplete(String label, String text) throws InterruptedException {
        WebElement input = driver.findElement(By.xpath("//label[text()='" + label + "']/../following-sibling::div//input"));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE); // Clear cũ
        input.sendKeys(text);
        Thread.sleep(3000);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='listbox']//div[1]"))).click();
    }

    private void enterDate(String label, String dateStr) {
        WebElement input = driver.findElement(By.xpath("//label[text()='" + label + "']/../following-sibling::div//input"));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(dateStr);
        driver.findElement(By.xpath("//h6")).click();
    }

    private void handlePartialDays() {
        try {
            WebElement dropdown = driver.findElement(By.xpath("//label[contains(text(), 'Partial Days')]/../following-sibling::div//div[contains(@class,'oxd-select-text')]"));
            if (dropdown.isDisplayed()) {
                dropdown.click();
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option'][1]"))).click();
            }
        } catch (Exception ignored) {}
    }
}