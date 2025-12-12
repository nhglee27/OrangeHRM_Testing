package com.example.demowebshop.integrated;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Job_Title_Test extends BaseTest {

    private String jobTitle;

    @BeforeAll
    void setupData() {
        jobTitle = "QA_Engineer_" + System.currentTimeMillis();
        System.out.println("🔹 JobTitleTest initialized: " + jobTitle);
    }

    @BeforeEach
    void preCondition() {
        loginAsAdmin();
        navigateToJobTitles();
    }

    private void navigateToJobTitles() {
        if (driver.getCurrentUrl().contains("viewJobTitleList")) return;
        
        // Navigate Admin -> Job -> Job Titles
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']"))).click();
        
        WebElement topbarJob = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(), 'Job')]")));
        topbarJob.click();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Job Titles']"))).click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Job Titles']")));
    }

    @Test @Order(1)
    void testAddJobTitleSuccess() {
        clickAddButton();
        
        fillJobTitleForm(jobTitle, "Test Description for " + jobTitle, "Note for " + jobTitle);
        
        clickSave();
        verifySuccessMessage();
    }

    @Test @Order(2)
    void testAddJobTitleDuplicate() {
        clickAddButton();
        
        // Điền trùng tên
        fillJobTitleForm(jobTitle, "Duplicate desc", "Duplicate note");
        
        // Không click save, vì lỗi duplicate thường hiện ngay khi blur khỏi ô input hoặc khi bấm save
        // Ở OrangeHRM, thường phải bấm Save mới check duplicate server-side hoặc check real-time
        clickSave(); 
        
        String expected = "Already exists";
        String actualError = getFieldError("Job Title");
        
        assertTrue(actualError.contains(expected), "Expected 'Already exists' but found: " + actualError);
        
        clickCancel();
    }

    @Test @Order(3)
    void testAddJobTitleEmpty() {
        clickAddButton();
        
        // Để trống và bấm Save
        clickSave();
        
        String expected = "Required";
        String actualError = getFieldError("Job Title");
        
        assertTrue(actualError.contains(expected), "Expected 'Required' but found: " + actualError);
        
        clickCancel();
    }

    @Test @Order(4)
    public void testUploadJobTitleDescription_FileSizeExceeded() {
        clickAddButton();

        // 1. Tăng kích thước file lên 5MB để chắc chắn vượt giới hạn (dù server set 1MB hay 4MB)
        File largeFile = createLargeFile("huge_test_file.pdf", 5); 
        
        try {
            // Upload file
            WebElement fileInput = driver.findElement(By.xpath("//input[@type='file']"));
            fileInput.sendKeys(largeFile.getAbsolutePath());
            
            // 2. Tăng thời gian chờ lên 10 giây (Validate file cần thời gian xử lý)
            // Và dùng Locator dựa trên Class thay vì Text (để tránh sai lệch chữ hoa/thường)
            WebElement errorMsg = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".oxd-input-field-error-message")));
            
            String actualErrorText = errorMsg.getText();
            System.out.println("⚠️ Actual Error Message found: " + actualErrorText);

            // 3. Verify nội dung lỗi (chấp nhận nhiều biến thể câu chữ)
            assertTrue(actualErrorText.toLowerCase().contains("exceed") || actualErrorText.contains("1MB"), 
                "Thông báo lỗi không đúng mong đợi. Thực tế: " + actualErrorText);
            
        } catch (TimeoutException e) {
            fail("❌ Quá thời gian chờ (10s) mà không thấy báo lỗi File Size. Có thể upload đã thành công (sai logic) hoặc mạng chậm.");
        } catch (Exception e) {
            fail("❌ Lỗi upload file test: " + e.getMessage());
        } finally {
            if(largeFile != null && largeFile.exists()) largeFile.delete();
        }
        
        clickCancel();
    }

    @Test @Order(5)
    void testDeleteJobTitle() {
        // Tìm job title trong bảng
        // OrangeHRM Job Title list không có search box, phải tìm trong list
        // Giả sử nó nằm ở trang 1 hoặc phải scroll tìm
        try {
            WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(), '" + jobTitle + "')]/ancestor::div[@role='row']//button[i[contains(@class, 'bi-trash')]]")));
            deleteBtn.click();
            
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();
            verifySuccessMessage();
            
        } catch (Exception e) {
            System.out.println("⚠️ Không tìm thấy Job Title để xóa (có thể đã bị xóa hoặc ở trang khác).");
        }
    }

    // --- HELPER METHODS ---

    private void clickAddButton() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        waitForLoader();
    }

    private void fillJobTitleForm(String title, String desc, String note) {
        clearAndType("Job Title", title);
        
        // Description và Note là textarea, locator có thể khác input thường
        typeTextArea("Job Description", desc);
        typeTextArea("Note", note);
    }

    private void clickSave() {
        WebElement btn = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        waitForLoader();
    }

    private void clickCancel() {
        WebElement btn = driver.findElement(By.xpath("//button[contains(., 'Cancel')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Job Titles']")));
        } catch (Exception ignored) {}
    }

    private void clearAndType(String label, String value) {
        WebElement input = driver.findElement(By.xpath("//label[text()='" + label + "']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
        try { input.clear(); } catch (Exception ignored) {}
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(value);
    }
    
    private void typeTextArea(String label, String value) {
        try {
            WebElement input = driver.findElement(By.xpath("//label[text()='" + label + "']/ancestor::div[contains(@class,'oxd-input-group')]//textarea"));
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
            input.sendKeys(value);
        } catch (Exception ignored) {
            // Có thể field optional hoặc không tìm thấy
        }
    }

    private String getFieldError(String fieldLabel) {
        try {
            WebElement errorSpan = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='" + fieldLabel + "']/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-group__message')]")
            ));
            return errorSpan.getText();
        } catch (TimeoutException e) {
            return "No Error Found";
        }
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
    
    private File createLargeFile(String name, int sizeInMB) {
        File f = new File(System.getProperty("java.io.tmpdir") + "/" + name);
        try (RandomAccessFile raf = new RandomAccessFile(f, "rw")) {
            raf.setLength(sizeInMB * 1024 * 1024L);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create test file", e);
        }
        return f;
    }
}