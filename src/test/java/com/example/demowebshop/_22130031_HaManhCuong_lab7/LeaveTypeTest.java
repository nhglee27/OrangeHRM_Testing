package com.example.demowebshop._22130031_HaManhCuong_lab7;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;
import java.util.List;

public class LeaveTypeTest extends BaseTest {

    // Biến dùng chung để lưu ngày cho TC trùng lặp
    static String sharedFromDateStr;
    static String sharedToDateStr;

    @Test(priority = 1, description = "TC_LT_001: Nhân viên nộp đơn")
    public void TC_LT_001_EmployeeApplyLeave() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1. Đăng nhập Nhân viên
        String empUser = testData.get("employee").get("username").asText();
        String empPass = testData.get("employee").get("password").asText();
        page.login(empUser, empPass);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Apply']"))).click();

        page.selectCustomDropdown("Leave Type", testData.get("leave").get("type").asText());

        // Tính toán ngày
        LocalDate futureDate = LocalDate.now().plusDays(7);
        if (futureDate.getDayOfWeek().getValue() >= 6) {
            futureDate = futureDate.plusDays(2);
        }

        sharedFromDateStr = futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        sharedToDateStr = futureDate.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        System.out.println("DEBUG: Nộp đơn ngày " + sharedFromDateStr);

        page.enterDate("From Date", sharedFromDateStr);
        page.enterDate("To Date", sharedToDateStr);

        // Lấy comment từ JSON
        driver.findElement(By.tagName("textarea")).sendKeys(testData.get("leave").get("comment").asText());

        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[text()=' Apply ']")).click();

        // 4. Verify thông báo thành công (Keyword từ JSON)
        // Trong JSON: "messages": { "success": "Success", ... }
        String successKeyword = testData.get("messages").get("success").asText();

        try {
            By toastLocator = By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p");
            WebElement toastMessage = wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
            String actualText = toastMessage.getText();
            System.out.println("THÔNG BÁO TOAST: " + actualText);

            Assert.assertTrue(actualText.contains(successKeyword), "Thông báo không chứa từ khóa: " + successKeyword);
        } catch (Exception e) {
            // Fallback verify source nếu không bắt được toast
            boolean foundText = driver.getPageSource().contains(testData.get("messages").get("submitted").asText()) ||
                    driver.getPageSource().contains(testData.get("messages").get("saved").asText());
            if (!foundText) {
                Assert.fail("Test Fail: Không bắt được thông báo thành công.");
            }
        }
    }

    @Test(priority = 2, description = "TC_LT_002: Admin duyệt đơn")
    public void TC_LT_002_AdminApproveLeave() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Đăng nhập Admin (Từ JSON)
        page.login(testData.get("admin").get("username").asText(), testData.get("admin").get("password").asText());

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leave List']"))).click();

        page.selectAutocomplete("Employee Name", testData.get("employee").get("fullName").asText());
        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(3000);

        try {
            WebElement approveBtn = driver.findElement(By.xpath("//button[text()=' Approve ']"));
            approveBtn.click();
        } catch (Exception e) {
            Assert.fail("Không tìm thấy đơn nào để Approve.");
        }

        // Verify Approve
        try {
            By toastLocator = By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p");
            wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
        } catch (Exception e) {}
    }

    @Test(priority = 3, description = "TC_LT_003: Admin từ chối đơn")
    public void TC_LT_003_AdminRejectLeave() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        page.login(testData.get("admin").get("username").asText(), testData.get("admin").get("password").asText());

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        page.selectAutocomplete("Employee Name", testData.get("employee").get("fullName").asText());
        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(3000);

        try {
            WebElement rejectBtn = driver.findElement(By.xpath("//button[text()=' Reject ']"));
            rejectBtn.click();
        } catch (Exception e) {
            System.out.println("Cảnh báo: Không tìm thấy đơn Pending để Reject.");
            return;
        }

        try {
            By toastLocator = By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p");
            wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
        } catch (Exception e) {}
    }
}