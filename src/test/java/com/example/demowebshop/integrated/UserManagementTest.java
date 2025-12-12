package com.example.demowebshop.integrated;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration; // Import thêm để dùng cho Wait
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserManagementTest extends BaseTest {

    private String createdUsername;

    @BeforeAll
    void setupData() {
        createdUsername = "UserTest_" + System.currentTimeMillis();
        System.out.println("🔹 UserManagementTest initialized with username: " + createdUsername);
    }

    @BeforeEach
    void preCondition() {
        loginAsAdmin();
        navigateToUserManagement();
    }

    void navigateToUserManagement() {
        if (driver.getCurrentUrl().contains("/admin/viewSystemUsers")) return;

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[normalize-space()='System Users']")));
    }

    @Test @Order(1)
    void testAddNewUserSuccess() {
        String pass = testData.get("user").get("defaultPassword").asText();
        fillUserForm(createdUsername, pass, pass, true);
        verifySuccessMessage();
    }

    @Test @Order(2)
    void testAddUserDuplicate() {
        String pass = testData.get("user").get("defaultPassword").asText();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        
        // Điền lại form với Username ĐÃ TỒN TẠI
        fillUserFormLogic(createdUsername, pass, pass, true);
        
        String expected = testData.get("messages").get("alreadyExists").asText();
        
        // Không dùng Thread.sleep, dùng Wait có điều kiện
        // Verify lỗi cụ thể dưới trường 'Username'
        String actualError = getFieldError("Username");
        
        assertTrue(actualError.contains(expected), 
            "Expected error '" + expected + "' under Username, but found: '" + actualError + "'");
                
        clickCancel();
    }

    @Test @Order(3)
    void testAddUserPasswordMismatch() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();

        fillUserFormLogic("fail_" + System.currentTimeMillis(), "Pass123", "Pass456", true);
        
        String expected = testData.get("messages").get("passwordMismatch").asText();
        
        String actualError = getFieldError("Confirm Password");
        
        assertTrue(actualError.contains(expected), 
            "Expected error '" + expected + "' under Confirm Password, but found: '" + actualError + "'");
            
        clickCancel();
    }

    @Test @Order(4)
    void testAddUserWeakPassword() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();

        String weakPass = testData.get("user").get("weakPassword").asText();
        fillUserFormLogic("weak_" + System.currentTimeMillis(), weakPass, weakPass, true);
        
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".orangehrm-password-chip")));
        } catch (Exception e) {
            fail("❌ Password strength chip (Very Weak) not found.");
        }

        String expected = testData.get("messages").get("passwordWeak").asText();
        String actualError = getFieldError("Password");
        
        assertTrue(actualError.contains(expected), 
            "Expected error '" + expected + "' under Password, but found: '" + actualError + "'");
        
        clickCancel();
    }

    @Test @Order(5)
    void testAddUserInvalidEmployee() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        
        String invalidName = testData.get("user").get("invalidEmployee").asText();
        
        // 1. Nhập tên nhân viên sai
        clearAndType("Employee Name", invalidName);
        
        // --- SỬA LỖI TẠI ĐÂY ---
        // Đợi một chút để UI cập nhật giá trị vào ô input
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        // Mẹo: Click vào một chỗ trống khác (ví dụ: Label) để trigger sự kiện "blur"
        // Điều này giúp hệ thống chốt giá trị trong ô input trước khi bấm Save
        driver.findElement(By.xpath("//label[text()='Employee Name']")).click();
        
        // 2. Click Save
        clickSave();

        String expected = testData.get("messages").get("invalid").asText();
        
        // 3. Verify
        String actualError = getFieldError("Employee Name");
        
        assertTrue(actualError.contains(expected), 
            "Expected error '" + expected + "' under Employee Name, but found: '" + actualError + "'");
            
        clickCancel();
    }

    @Test @Order(6)
    void testSearchUserValid() {
        searchUser(createdUsername);
        try {
            WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='oxd-table-card']//div[contains(text(), '" + createdUsername + "')]")));
            assertTrue(row.isDisplayed());
        } catch (Exception e) {
            fail("❌ Search failed: User " + createdUsername + " not found in results.");
        }
    }

    @Test @Order(7)
    void testSearchUserNotFound() {
        searchUser("User_NotExist_" + System.currentTimeMillis());
        boolean isNotFound = false;
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(), 'Records Found') or text()='No Records Found']")));
            List<WebElement> cards = driver.findElements(By.className("oxd-table-card"));
            if (cards.isEmpty()) isNotFound = true;
        } catch (Exception e) {
            isNotFound = true;
        }
        assertTrue(isNotFound, "Search should have returned no results!");
    }

    @Test @Order(8)
    void testDeleteUser() {
        searchUser(createdUsername);
        try {
            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(), '" + createdUsername + "')]/ancestor::div[@role='row']//button[i[contains(@class, 'bi-trash')]]")));
            deleteBtn.click();
        } catch (Exception e) {
            fail("❌ Delete button not found for user: " + createdUsername);
        }
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();
        verifySuccessMessage();
    }

    // --- HELPER METHODS ---

    private void fillUserForm(String username, String pass, String confirmPass, boolean selectEmployee) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        fillUserFormLogic(username, pass, confirmPass, selectEmployee);
    }

    private void fillUserFormLogic(String username, String pass, String confirmPass, boolean selectEmployee) {
        clickDropdownAndSelect("User Role", "ESS");
        clickDropdownAndSelect("Status", "Enabled");

        if (selectEmployee) {
            WebElement empInput = driver.findElement(By.xpath("//label[text()='Employee Name']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
            
            empInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            empInput.sendKeys("a"); 
            
            // Wait ngắn để API suggest chạy (có thể giữ lại cái này nếu cần thiết)
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {} 
            
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='listbox']")));
                selectOptionSafe(By.xpath("//div[@role='listbox']//div[@role='option'][1]"));
            } catch (Exception e) {
                System.out.println("⚠️ Warning: Employee suggestion list did not appear.");
            }
        }

        clearAndType("Username", username);
        clearAndType("Password", pass);
        clearAndType("Confirm Password", confirmPass);
        
        clickSave();
        
        // QUAN TRỌNG: Đợi loader biến mất sau khi save
        waitForLoader(); 
    }

    private void clearAndType(String label, String value) {
        WebElement input = driver.findElement(By.xpath("//label[text()='" + label + "']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
        try { input.clear(); } catch (Exception ignored) {}
        
        String val = input.getAttribute("value");
        if (val != null && !val.isEmpty()) {
             input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        }
        input.sendKeys(value);
    }

    private String getFieldError(String fieldLabel) {
        try {
            // Tăng thời gian chờ riêng cho việc tìm lỗi (đề phòng mạng chậm)
            // Dùng shortWait hoặc wait thường nhưng bọc try-catch
            WebElement errorSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='" + fieldLabel + "']/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-group__message')]")
            ));
            return errorSpan.getText();
        } catch (TimeoutException e) {
            return "No Error Found";
        }
    }

    private void selectOptionSafe(By locator) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return;
            } catch (StaleElementReferenceException e) {
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
            attempts++;
        }
        throw new RuntimeException("❌ Failed to click element after 3 attempts: " + locator);
    }

    private void clickDropdownAndSelect(String label, String optionText) {
        driver.findElement(By.xpath("//label[text()='" + label + "']/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class, 'oxd-select-text')]")).click();
        selectOptionSafe(By.xpath("//div[@role='option']//span[text()='" + optionText + "']"));
    }

    private void clickSave() {
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
    
    private void clickCancel() {
        WebElement btn = driver.findElement(By.xpath("//button[contains(., 'Cancel')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[normalize-space()='System Users']")));
        } catch (Exception e) {
            System.out.println("⚠️ Cảnh báo: Không thể xác nhận đã quay về trang System Users sau khi Cancel.");
        }
    }

    private void searchUser(String username) {
        waitForLoader();
        clearAndType("Username", username);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForLoader();
    }

    private void waitForLoader() {
        // Đợi loader xuất hiện (nếu có) và sau đó đợi nó biến mất
        try {
            // Chỉ đợi loader xuất hiện trong 2s, nếu không có nghĩa là mạng quá nhanh hoặc không có loader
            wait.withTimeout(Duration.ofSeconds(2))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("oxd-form-loader")));
            
            // Nếu đã xuất hiện, thì đợi nó biến mất (với timeout mặc định dài hơn của biến 'wait')
            wait.withTimeout(Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader")));
        } catch (Exception ignored) {
            // Bỏ qua nếu loader không bao giờ xuất hiện
        }
    }

    private void verifySuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")));
        } catch (Exception ignored) {}
    }
}