package com.example.demowebshop._21130375_lenguyengiahuy_lab7;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.example.demowebshop.BaseTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserManagementTests extends BaseTest {

    private static String createdUsername;

    @BeforeEach
    void preCondition() {
        if (driver == null) setup();
        loginAsAdmin();
        navigateToUserManagement();
    }

    void navigateToUserManagement() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']"))).click();
        // Đợi title System Users xuất hiện để chắc chắn trang đã load
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[text()='System Users']")));
        } catch (Exception e) {
            // Retry click if needed
            driver.findElement(By.xpath("//span[text()='Admin']")).click();
        }
    }

    @Test
    @Order(1)
    @DisplayName("TC_USER_002 Kiểm tra thêm mới User")
    void testAddNewUserSuccess() throws InterruptedException {
        createdUsername = "user_" + System.currentTimeMillis(); // Username unique

        driver.findElement(By.xpath("//button[contains(., 'Add')]")).click();

        // 1. User Role: ESS
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[text()='User Role']/../following-sibling::div//div[@class='oxd-select-text-input']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option']//span[text()='ESS']"))).click();

        // 2. Status: Enabled
        driver.findElement(By.xpath("//label[text()='Status']/../following-sibling::div//div[@class='oxd-select-text-input']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option']//span[text()='Enabled']"))).click();

        // 3. Employee Name: Gõ 'a' và chọn người đầu tiên
        WebElement empInput = driver.findElement(By.xpath("//label[text()='Employee Name']/../following-sibling::div//input"));
        empInput.sendKeys("a");
        Thread.sleep(2000); // Đợi gợi ý load (bắt buộc vì animation của OrangeHRM hơi chậm)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']//span"))).click();

        // 4. User & Pass
        driver.findElement(By.xpath("//label[text()='Username']/../following-sibling::div//input")).sendKeys(createdUsername);
        driver.findElement(By.xpath("//label[text()='Password']/../following-sibling::div//input")).sendKeys("TestPass123!");
        driver.findElement(By.xpath("//label[text()='Confirm Password']/../following-sibling::div//input")).sendKeys("TestPass123!");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        verifySuccessMessage();
    }

    @Test
    @Order(2)
    @DisplayName("TC_USER_001 Kiểm tra tìm kiếm User hợp lệ")
    void testSearchUserValid() {
        if (createdUsername == null) createdUsername = "Admin";

        // Reset nút search bằng cách click nút Reset trước cho sạch form
        try {
            driver.findElement(By.xpath("//button[contains(., 'Reset')]")).click();
            Thread.sleep(1000); // Đợi reset
        } catch (Exception ignored) {}

        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Username']/../following-sibling::div//input")));
        searchInput.sendKeys(createdUsername);

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verify
        WebElement resultCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='oxd-table-card']//div[contains(text(), '" + createdUsername + "')]")));
        assertTrue(resultCell.isDisplayed());
    }

    @Test
    @Order(3)
    @DisplayName("TC_USER_003 Kiểm tra thêm mới User (để trống)")
    void testAddUserEmptyFields() {
        driver.findElement(By.xpath("//button[contains(., 'Add')]")).click();
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        List<WebElement> errors = driver.findElements(By.xpath("//span[text()='Required']"));
        assertTrue(errors.size() >= 1, "Phải có ít nhất 1 lỗi Required");
    }

    @Test
    @Order(4)
    @DisplayName("TC_USER_004 Kiểm tra xoá User")
    void testDeleteUser() throws InterruptedException {
        if (createdUsername == null) return;
        testSearchUserValid(); // Tìm lại user để xóa

        // FIX LỖI STALE ELEMENT: Tìm lại element ngay lúc cần click
        Thread.sleep(1500); // Đợi bảng load ổn định hẳn
        
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(), '" + createdUsername + "')]/../..//button[i[contains(@class, 'bi-trash')]]")));
        deleteBtn.click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();

        verifySuccessMessage();
    }

    private void verifySuccessMessage() {
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("oxd-toaster_1")));
        assertTrue(toast.getText().contains("Success") || toast.getText().contains("Saved") || toast.getText().contains("Deleted"));
        wait.until(ExpectedConditions.invisibilityOf(toast));
    }
}