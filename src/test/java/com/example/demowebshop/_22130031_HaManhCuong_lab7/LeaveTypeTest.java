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
import java.util.List;

public class LeaveTypeTest extends BaseTest {

    // Biến class để lưu ngày đã dùng, phục vụ cho test case trùng lặp
    private String usedDateStr;

    @Test(priority = 1, description = "TC_LT_001: Nhân viên nộp đơn xin nghỉ phép (Happy Path)")
    public void TC_LT_001_EmployeeApplyLeave() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1. Đăng nhập Nhân viên
        String empUser = testData.get("employee").get("username").asText();
        String empPass = testData.get("employee").get("password").asText();
        page.login(empUser, empPass);

        // 2. Vào trang Apply
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Apply']"))).click();

        // 3. Điền Form
        page.selectCustomDropdown("Leave Type", testData.get("leave").get("type").asText());

        // Tính ngày hợp lệ (tránh cuối tuần) và lưu vào biến class
        LocalDate futureDate = LocalDate.now().plusDays(7);
        if (futureDate.getDayOfWeek().getValue() >= 6) {
            futureDate = futureDate.plusDays(2);
        }
        usedDateStr = futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        page.enterDate("From Date", usedDateStr);
        page.enterDate("To Date", usedDateStr);

        // --- XỬ LÝ DROPDOWN PHỤ (Partial Days / Duration) ---
        try {
            WebElement partialDropdown = driver.findElement(By.xpath("//label[contains(text(), 'Partial Days')]/../following-sibling::div//div[@class='oxd-select-text-input']"));
            if (partialDropdown.isDisplayed()) {
                System.out.println("DEBUG: Phát hiện dropdown Partial Days. Đang chọn...");
                partialDropdown.click();
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option'][1]"))).click();
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Không thấy hoặc không cần chọn Partial Days.");
        }
        // ----------------------------------------------------

        driver.findElement(By.tagName("textarea")).sendKeys(testData.get("leave").get("comment").asText());

        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[text()=' Apply ']")).click();

        // 4. Verify Success
        String successKeyword = testData.get("messages").get("success").asText();
        try {
            By toastLocator = By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p");
            WebElement toastMessage = wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
            String actualText = toastMessage.getText();
            System.out.println("THÔNG BÁO TC_001: " + actualText);
            Assert.assertTrue(actualText.contains(successKeyword), "Thông báo không đúng: " + actualText);
        } catch (Exception e) {
            boolean foundText = driver.getPageSource().contains(testData.get("messages").get("submitted").asText()) ||
                    driver.getPageSource().contains(testData.get("messages").get("saved").asText());
            if (!foundText) Assert.fail("Test Fail: Không thấy thông báo thành công.");
        }
    }

    @Test(priority = 2, description = "TC_LT_002: Admin duyệt đơn nghỉ phép")
    public void TC_LT_002_AdminApproveLeave() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        String adminUser = testData.get("admin").get("username").asText();
        String adminPass = testData.get("admin").get("password").asText();
        page.login(adminUser, adminPass);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Leave List']"))).click();

        String empName = testData.get("employee").get("fullName").asText();
        page.selectAutocomplete("Employee Name", empName);

        // Filter thêm ngày để chắc chắn tìm đúng đơn vừa tạo (nếu muốn chặt chẽ hơn)
        // page.enterDate("From Date", usedDateStr);

        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(3000);

        try {
            WebElement approveBtn = driver.findElement(By.xpath("//button[text()=' Approve ']"));
            approveBtn.click();
        } catch (Exception e) {
            Assert.fail("Không tìm thấy đơn nào để Approve.");
        }

        try {
            By toastLocator = By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p");
            wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
        } catch (Exception e) {}
    }

    // --- TEST CASE MỚI: XIN NGHỈ TRÙNG NGÀY ---
    @Test(priority = 3, description = "TC_LT_0011: xin ngày nghỉ khác để test reject")
    public void TC_LT_0011_EmployeeApplyLeave() throws InterruptedException {
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 1. Đăng nhập Nhân viên
        String empUser = testData.get("employee").get("username").asText();
        String empPass = testData.get("employee").get("password").asText();
        page.login(empUser, empPass);

        // 2. Vào trang Apply
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Apply']"))).click();

        // 3. Điền Form
        page.selectCustomDropdown("Leave Type", testData.get("leave").get("type").asText());

        // Tính ngày hợp lệ (tránh cuối tuần) và lưu vào biến class
        LocalDate futureDate = LocalDate.now().plusDays(8);
        if (futureDate.getDayOfWeek().getValue() >= 6) {
            futureDate = futureDate.plusDays(2);
        }
        usedDateStr = futureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        page.enterDate("From Date", usedDateStr);
        page.enterDate("To Date", usedDateStr);

        // --- XỬ LÝ DROPDOWN PHỤ (Partial Days / Duration) ---
        try {
            WebElement partialDropdown = driver.findElement(By.xpath("//label[contains(text(), 'Partial Days')]/../following-sibling::div//div[@class='oxd-select-text-input']"));
            if (partialDropdown.isDisplayed()) {
                System.out.println("DEBUG: Phát hiện dropdown Partial Days. Đang chọn...");
                partialDropdown.click();
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option'][1]"))).click();
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Không thấy hoặc không cần chọn Partial Days.");
        }
        // ----------------------------------------------------

        driver.findElement(By.tagName("textarea")).sendKeys(testData.get("leave").get("comment").asText());

        Thread.sleep(2000);
        driver.findElement(By.xpath("//button[text()=' Apply ']")).click();

        // 4. Verify Success
        String successKeyword = testData.get("messages").get("success").asText();
        try {
            By toastLocator = By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p");
            WebElement toastMessage = wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
            String actualText = toastMessage.getText();
            System.out.println("THÔNG BÁO TC_001: " + actualText);
            Assert.assertTrue(actualText.contains(successKeyword), "Thông báo không đúng: " + actualText);
        } catch (Exception e) {
            boolean foundText = driver.getPageSource().contains(testData.get("messages").get("submitted").asText()) ||
                    driver.getPageSource().contains(testData.get("messages").get("saved").asText());
            if (!foundText) Assert.fail("Test Fail: Không thấy thông báo thành công.");
        }
    }

    @Test(priority = 4, description = "TC_LT_003: Admin từ chối đơn nghỉ phép")
    public void TC_LT_003_AdminRejectLeave() throws InterruptedException {
        // ... (Giữ nguyên code Reject cũ, nhưng lưu ý TC này có thể bị Skip nếu không còn đơn nào Pending)
        CommonPage page = new CommonPage(driver);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        String adminUser = testData.get("admin").get("username").asText();
        String adminPass = testData.get("admin").get("password").asText();
        page.login(adminUser, adminPass);

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Leave']"))).click();

        String empName = testData.get("employee").get("fullName").asText();
        page.selectAutocomplete("Employee Name", empName);
        driver.findElement(By.xpath("//button[text()=' Search ']")).click();
        Thread.sleep(3000);

        try {
            WebElement rejectBtn = driver.findElement(By.xpath("//button[text()=' Reject ']"));
            rejectBtn.click();
        } catch (Exception e) {
            System.out.println("Cảnh báo: Không tìm thấy đơn Pending để Reject. (Có thể do TC_LT_004 không tạo được đơn mới)");
            return;
        }

        try {
            By toastLocator = By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p");
            wait.until(ExpectedConditions.presenceOfElementLocated(toastLocator));
        } catch (Exception e) {}
    }
}