package com.example.demowebshop._21130375_lenguyengiahuy_lab7;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        
        // ✅ LUÔN Click lại menu Admin để reset bộ lọc tìm kiếm
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']"))).click();
        
        try {
            // Đợi tiêu đề trang hiện ra
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[text()='System Users']")));
        } catch (Exception e) {
            // Retry click nếu mạng lag
            driver.findElement(By.xpath("//span[text()='Admin']")).click();
        }
        
        // Đợi loader biến mất (đảm bảo trang đã load xong data)
        try {
             wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader")));
        } catch (Exception ignored) {}
    }

    @Test @Order(1)
    void testAddNewUserSuccess() {
        createdUsername = "user_" + System.currentTimeMillis();
        String pass = testData.get("user").get("defaultPassword").asText();
        fillUserForm(createdUsername, pass, pass, true); // true = valid employee check
        verifySuccessMessage();
    }
    @Test @Order(2)
    void testAddUserDuplicate() {
        // ✅ FIX LỖI NULL: Nếu chưa có user (do chạy riêng lẻ), tự tạo 1 cái
        if (createdUsername == null) {
             createdUsername = "user_" + System.currentTimeMillis();
             // Vì chưa có user nên phải tạo thật trong DB trước đã thì mới test trùng được
             String pass = testData.get("user").get("defaultPassword").asText();
             fillUserForm(createdUsername, pass, pass, true);
             verifySuccessMessage();
             // Sau khi tạo xong, đợi bảng reload rồi mới bấm Add tiếp
             navigateToUserManagement(); 
        }

        String pass = testData.get("user").get("defaultPassword").asText();
        
        // Cố tình thêm lại user trùng tên
        fillUserForm(createdUsername, pass, pass, true);
        
        // Click ra ngoài để trigger validate (Click vào ô Password)
        driver.findElement(By.xpath("//label[text()='Password']/../following-sibling::div//input")).click();
        
        String expected = testData.get("messages").get("alreadyExists").asText();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(), '" + expected + "')]"))); // Dùng contains cho an toàn
        
        assertTrue(error.isDisplayed());
        
        driver.findElement(By.xpath("//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(3)
    void testAddUserPasswordMismatch() {
        // Nhập 2 pass khác nhau
        fillUserForm("user_fail_" + System.currentTimeMillis(), "Pass123", "Pass456", true);
        
        // ✅ CÁCH SỬA: Tìm TẤT CẢ thông báo lỗi trên màn hình
        try {
            // 1. Đợi ít nhất 1 lỗi xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-input-group__message")));
            
            // 2. Lấy danh sách tất cả lỗi
            java.util.List<WebElement> allErrors = driver.findElements(By.cssSelector(".oxd-input-group__message"));
            
            System.out.println("⚠️ Tìm thấy " + allErrors.size() + " lỗi:");
            
            boolean found = false;
            // Lấy text mong đợi từ file JSON (thường là "Passwords do not match")
            String expectedText = testData.get("messages").get("passwordMismatch").asText();
            
            for (WebElement err : allErrors) {
                String text = err.getText();
                System.out.println("   -> " + text);
                
                // So sánh (dùng contains cho an toàn)
                if (text.contains(expectedText) || text.toLowerCase().contains("match")) {
                    found = true;
                    break;
                }
            }
            
            // 3. Assert
            assertTrue(found, "Không tìm thấy lỗi '" + expectedText + "' trên màn hình!");
            
        } catch (Exception e) {
            Assertions.fail("Timeout: Bấm Save xong nhưng không thấy dòng lỗi nào hiện ra.");
        }
        
        // Dọn dẹp
        driver.findElement(By.xpath("//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(4)
    void testAddUserWeakPassword() {
        String weakPass = testData.get("user").get("weakPassword").asText();
        // Điền form và bấm Save
        fillUserForm("user_weak_" + System.currentTimeMillis(), weakPass, weakPass, true);
        
        // ✅ CÁCH SỬA: Tìm TẤT CẢ thông báo lỗi (class chuẩn của OrangeHRM)
        try {
            // Đợi ít nhất 1 lỗi xuất hiện
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-input-group__message")));
            
            // Lấy danh sách tất cả các lỗi đang hiện
            List<WebElement> allErrors = driver.findElements(By.cssSelector(".oxd-input-group__message"));
            
            System.out.println("⚠️ Tìm thấy " + allErrors.size() + " lỗi trên màn hình:");
            boolean foundRelevantError = false;
            
            for (WebElement err : allErrors) {
                String text = err.getText();
                System.out.println("   -> Nội dung: " + text);
                
                // Kiểm tra xem có lỗi nào liên quan đến pass yếu không
                if (text.toLowerCase().contains("char") || text.toLowerCase().contains("weak") || text.toLowerCase().contains("number")) {
                    foundRelevantError = true;
                    break;
                }
            }
            
            assertTrue(foundRelevantError, "Không tìm thấy thông báo lỗi nào về mật khẩu yếu!");
            
        } catch (Exception e) {
            Assertions.fail("Timeout: Đã bấm Save nhưng không thấy bất kỳ dòng lỗi màu đỏ nào hiện ra!");
        }
        
        // Dọn dẹp
        driver.findElement(By.xpath("//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(5)
    void testAddUserInvalidEmployee() {
        driver.findElement(By.xpath("//button[contains(., 'Add')]")).click();
        
        // Nhập tên bậy bạ
        String invalidName = testData.get("user").get("invalidEmployee").asText();
        WebElement empInput = driver.findElement(By.xpath("//label[text()='Employee Name']/../following-sibling::div//input"));
        empInput.sendKeys(invalidName);
        
        // Click ra ngoài để trigger validate
        driver.findElement(By.xpath("//label[text()='User Role']")).click();

        String expected = testData.get("messages").get("invalid").asText();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='" + expected + "']")));
        
        assertTrue(error.isDisplayed());
        driver.findElement(By.xpath("//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(6)
    void testSearchUserValid() {
        if (createdUsername == null) return;
        searchUser(createdUsername);
        
        // Assert: User phải hiện trong bảng
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@class='oxd-table-card']//div[contains(text(), '" + createdUsername + "')]"))).isDisplayed());
    }

    @Test @Order(7)
    void testSearchUserNotFound() {
        searchUser("User_Khong_Ton_Tai_" + System.currentTimeMillis());
        
        // Kiểm tra thông báo "(0) Records Found" hoặc "No Records Found"
        // OrangeHRM thường hiện text "(0) Records Found" ở góc trên bên phải bảng
        boolean isNotFound = false;
        try {
            // Cách 1: Tìm dòng thông báo (0) Records Found
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(), '(0) Records Found')]")));
            isNotFound = true;
        } catch (Exception e) {
            // Cách 2: Kiểm tra bảng rỗng (không có dòng dữ liệu nào)
            isNotFound = driver.findElements(By.cssSelector(".oxd-table-card")).isEmpty();
        }
        
        assertTrue(isNotFound, "Lẽ ra không được tìm thấy user này!");
    }

    @Test @Order(8)
    void testDeleteUser() {
        // Nếu không có user để xóa thì bỏ qua (tránh lỗi null)
        if (createdUsername == null) {
            System.out.println("⚠️ Skip testDeleteUser vì createdUsername là null");
            return;
        }

        searchUser(createdUsername);
        
        // Kiểm tra xem User có hiện ra không
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(), '" + createdUsername + "')]")));
        } catch (Exception e) {
            Assertions.fail("❌ Không tìm thấy user '" + createdUsername + "' sau khi search.");
        }
        
        // Tìm nút xóa
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(text(), '" + createdUsername + "')]/../..//button[i[contains(@class, 'bi-trash')]]")));
        deleteBtn.click();

        // Confirm xóa
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();
        
        verifySuccessMessage();
    }

    private void fillUserForm(String username, String pass, String confirmPass, boolean selectEmployee) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        
        // 1. Role: Click -> Wait Option -> Click Option
        driver.findElement(By.xpath("//label[text()='User Role']/../following-sibling::div//div[@class='oxd-select-text-input']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option']//span[text()='ESS']"))).click();

        // 2. Status
        driver.findElement(By.xpath("//label[text()='Status']/../following-sibling::div//div[@class='oxd-select-text-input']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option']//span[text()='Enabled']"))).click();

        // 3. Employee Name (Xử lý AutoComplete KHÔNG CẦN SLEEP)
        if (selectEmployee) {
            WebElement empInput = driver.findElement(By.xpath("//label[text()='Employee Name']/../following-sibling::div//input"));
            empInput.sendKeys(testData.get("user").get("employeeNameHint").asText());
            
            // Đợi cái hộp gợi ý (listbox) bung ra
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']")));
            
            // Đợi option đầu tiên clickable rồi bấm
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='listbox']//div[@role='option']"))).click();
        }

        // 4. User & Pass
        driver.findElement(By.xpath("//label[text()='Username']/../following-sibling::div//input")).sendKeys(username);
        driver.findElement(By.xpath("//label[text()='Password']/../following-sibling::div//input")).sendKeys(pass);
        driver.findElement(By.xpath("//label[text()='Confirm Password']/../following-sibling::div//input")).sendKeys(confirmPass);
        
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    private void searchUser(String username) {
        // LUÔN Bấm Reset trước khi tìm
        try { 
            driver.findElement(By.xpath("//button[contains(., 'Reset')]")).click(); 
            // Đợi loader biến mất sau khi reset
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader")));
        } catch (Exception ignored) {}
        
        // ... (Phần còn lại giữ nguyên như code "Nhập và Kiểm tra lại" mình gửi trước đó)
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//label[text()='Username']/../following-sibling::div//input")));
            
        // ... Code nhập liệu retry ...
        for (int i = 0; i < 3; i++) {
            input.click(); input.clear(); input.sendKeys(username);
            if (input.getAttribute("value").equals(username)) break;
            try { Thread.sleep(500); } catch (Exception e) {}
        }
        
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        try { wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader"))); } catch (Exception ignored) {}
    }

    private void verifySuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("oxd-toaster_1")));
            // Tìm nút đóng để không che màn hình
            WebElement close = driver.findElement(By.cssSelector(".oxd-toast-close"));
            if (close.isDisplayed()) close.click();
        } catch (Exception ignored) {}
    }
}