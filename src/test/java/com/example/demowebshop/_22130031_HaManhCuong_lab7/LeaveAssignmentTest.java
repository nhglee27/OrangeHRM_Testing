package com.example.demowebshop._22130031_HaManhCuong_lab7;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class LeaveAssignmentTest extends BaseTest {

    @Test(priority = 1, description = "TC_LA_001: Admin gán ngày phép thành công")
    public void TC_LA_001_AdminAssignLeaveSuccess() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Đăng nhập Admin
        String adminUser = testData.get("admin").get("username").asText();
        String adminPass = testData.get("admin").get("password").asText();
        page.login(adminUser, adminPass);

        // 2. Điều hướng
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='Entitlements']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[normalize-space()='Add Entitlements']"))).click();

        // 3. Điền Form
        String empName = testData.get("employee").get("fullName").asText();
        String leaveType = testData.get("leave").get("type").asText();
        String entitlement = testData.get("leave").get("entitlementAmount").asText();

        page.selectAutocomplete("Employee Name", empName);
        page.selectCustomDropdown("Leave Type", leaveType);

        WebElement entInput = driver.findElement(By.xpath("//label[text()='Entitlement']/following::input[1]"));
        entInput.sendKeys(entitlement);

        driver.findElement(By.xpath("//button[text()=' Save ']")).click();

        // Xử lý Confirm
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement confirmBtn = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()=' Confirm ']")
            ));
            confirmBtn.click();
        } catch (Exception e) {}

        // 4. Verify
        String successMsgText = testData.get("messages").get("saved").asText();
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class, 'oxd-text--toast-message') and text()='" + successMsgText + "']")
        ));
        Assert.assertTrue(successMsg.isDisplayed(), "Lỗi: Không thấy thông báo thành công!");
    }

    @Test(priority = 2, description = "TC_LA_002: Gán phép thiếu tên nhân viên")
    public void TC_LA_002_AssignLeaveMissingName() {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Login Admin
        page.login(testData.get("admin").get("username").asText(), testData.get("admin").get("password").asText());

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='Entitlements']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[normalize-space()='Add Entitlements']"))).click();

        // Chỉ chọn Leave Type, nhập số lượng
        page.selectCustomDropdown("Leave Type", testData.get("leave").get("type").asText());
        driver.findElement(By.xpath("//label[text()='Entitlement']/following::input[1]"))
                .sendKeys(testData.get("leave").get("partialAmount").asText());

        driver.findElement(By.xpath("//button[text()=' Save ']")).click();

        // Verify lỗi Required
        String expectedError = testData.get("messages").get("required").asText();
        String errorText = driver.findElement(By.xpath("//label[text()='Employee Name']/parent::div/following-sibling::span")).getText();
        Assert.assertEquals(errorText, expectedError, "Lỗi hiển thị không đúng!");
    }

    @Test(priority = 3, description = "TC_LA_003: Gán phép số âm")
    public void TC_LA_003_AssignLeaveNegativeDays() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        page.login(testData.get("admin").get("username").asText(), testData.get("admin").get("password").asText());

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='Entitlements']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[normalize-space()='Add Entitlements']"))).click();

        page.selectAutocomplete("Employee Name", testData.get("employee").get("fullName").asText());
        page.selectCustomDropdown("Leave Type", testData.get("leave").get("type").asText());

        // Nhập số âm từ JSON
        driver.findElement(By.xpath("//label[text()='Entitlement']/following::input[1]"))
                .sendKeys(testData.get("leave").get("negativeAmount").asText());

        driver.findElement(By.xpath("//button[text()=' Save ']")).click();

        // Verify lỗi format
        String expectedError = testData.get("messages").get("formatError").asText();
        String errorText = driver.findElement(By.xpath("//label[text()='Entitlement']/parent::div/following-sibling::span")).getText();
        Assert.assertEquals(errorText, expectedError, "Thông báo lỗi số âm không đúng!");
    }
}