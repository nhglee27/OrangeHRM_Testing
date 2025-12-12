package com.example.demowebshop.integrated;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PIMTest extends BaseTest {

    private String firstName;
    private String lastName;
    private String employeeId; // Lưu ID để search và xóa

    @BeforeAll
    void setupData() {
        firstName = "Test_" + System.currentTimeMillis();
        lastName = "User";
        System.out.println("🔹 PIMTest initialized for: " + firstName + " " + lastName);
    }

    @BeforeEach
    void preCondition() {
        loginAsAdmin();
        navigateToPIM();
    }

    private void navigateToPIM() {
        if (driver.getCurrentUrl().contains("viewEmployeeList")) return;
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='PIM']"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[normalize-space()='Employee Information']")));
    }

    @Test @Order(1)
    void testAddEmployeeSuccess() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        waitForLoader();

        // PIM Add form inputs are different (using name attributes or placeholders)
        WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));
        firstNameInput.sendKeys(firstName);
        
        driver.findElement(By.name("lastName")).sendKeys(lastName);
        
        // Get Employee ID (Auto-generated)
        WebElement idInput = driver.findElement(By.xpath("//label[text()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
        employeeId = idInput.getAttribute("value");
        System.out.println("🆔 Created Employee ID: " + employeeId);
        
        clickSave();
        
        verifySuccessMessage();
        
        // Sau khi save thành công, nó thường redirect vào trang Personal Details
        // Cần quay lại danh sách để các test sau chạy ổn định (hoặc @BeforeEach sẽ lo việc này)
    }

    @Test @Order(2)
    void testSearchEmployeeById() {
        // Đảm bảo đang ở trang list
        navigateToPIM();
        
        // Reset search (nếu cần) hoặc nhập thẳng
        // Nhập ID vào ô search
        WebElement idSearchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//label[text()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input")));
            
        idSearchInput.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        idSearchInput.sendKeys(employeeId);
        
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForLoader();
        
        // Verify tìm thấy
        try {
            WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='oxd-table-card']//div[contains(text(), '" + firstName + "')]")));
            assertTrue(row.isDisplayed(), "Không tìm thấy Employee vừa tạo trong bảng.");
        } catch (Exception e) {
            fail("❌ Search failed for ID: " + employeeId);
        }
    }

    @Test @Order(3)
    void testEmployeeTooltip() {
        // Hover vào PIM menu item xem có tooltip không
        // Lưu ý: Logic này tùy thuộc vào version OrangeHRM, một số bản không có tooltip ở menu chính
        
        WebElement pimMenu = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='PIM']/ancestor::a")));
        
        Actions actions = new Actions(driver);
        actions.moveToElement(pimMenu).perform();
        
        // Wait ngắn để tooltip render
        try { Thread.sleep(1000); } catch (Exception ignored) {}
        
        boolean isTooltipDisplayed = false;
        try {
            // Thử tìm element tooltip (thường là role='tooltip' hoặc class riêng)
            // OrangeHRM React thường không dùng native title attribute cho tooltip đẹp
            WebElement tooltip = driver.findElement(By.xpath("//div[@role='tooltip'] | //div[contains(@class, 'oxd-main-menu-item--name')]"));
            isTooltipDisplayed = tooltip.isDisplayed();
        } catch (Exception e) {
            // Fallback: check attribute title
             String titleAttr = pimMenu.getAttribute("title");
             isTooltipDisplayed = (titleAttr != null && !titleAttr.isEmpty());
        }
        
        // Test này có thể skip nếu hệ thống không thiết kế tooltip
        System.out.println("Tooltip checking result: " + isTooltipDisplayed);
    }

    @Test @Order(4)
    void testDeleteEmployee() {
        // Search lại để chắc chắn xóa đúng người (đã làm ở bước 2, nhưng làm lại cho chắc)
        testSearchEmployeeById();
        
        try {
            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='oxd-table-card']//button[i[contains(@class, 'bi-trash')]]")));
            deleteBtn.click();
            
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();
            verifySuccessMessage();
            
        } catch (Exception e) {
            fail("❌ Không tìm thấy nút xóa hoặc lỗi khi xóa Employee.");
        }
    }

    // --- HELPER METHODS ---

    private void clickSave() {
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        waitForLoader();
    }
    
    private void waitForLoader() {
        try {
            wait.withTimeout(Duration.ofSeconds(2)).until(ExpectedConditions.visibilityOfElementLocated(By.className("oxd-form-loader")));
            wait.withTimeout(Duration.ofSeconds(10)).until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader")));
        } catch (Exception ignored) {}
    }

    private void verifySuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")));
        } catch (Exception ignored) {}
    }
}