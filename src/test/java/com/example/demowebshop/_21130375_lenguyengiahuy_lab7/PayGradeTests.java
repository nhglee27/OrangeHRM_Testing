package com.example.demowebshop._21130375_lenguyengiahuy_lab7;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PayGradeTests extends BaseTest {

    private static String payGradeName;

    @BeforeEach
    void preCondition() {
        if (driver == null) setup();
        loginAsAdmin();
        navigateToPayGrades();
    }

    void navigateToPayGrades() {
        if (driver.getCurrentUrl().contains("viewPayGrades")) return;
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(), 'Job')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Pay Grades']"))).click();
    }

    @Test @Order(1)
    void testAddPayGradeSuccess() {
        payGradeName = "Grade_" + System.currentTimeMillis();
        addPayGrade(payGradeName);
        verifySuccessMessage();
    }

    @Test @Order(2)
    void testAddPayGradeDuplicate() {
        addPayGrade(payGradeName);
        
        // Assert nội dung lỗi "Already exists"
        String expectedMsg = testData.get("messages").get("alreadyExists").asText();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(@class, 'oxd-input-group__message')]")));
        
        assertEquals(expectedMsg, error.getText(), "Thông báo lỗi trùng tên không đúng!");
        
        driver.findElement(By.xpath("//button[contains(., 'Cancel')]")).click();
    }

    // --- CASE 3: Validation để trống (Thiếu cái này) ---
    @Test @Order(3)
    void testAddPayGradeEmpty() {
        driver.findElement(By.xpath("//button[contains(., 'Add')]")).click();
        
        // Bấm Save ngay mà không nhập tên
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
        
        // Assert: Phải hiện chữ "Required"
        String expectedMsg = testData.get("messages").get("required").asText();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='" + expectedMsg + "']")));
        assertTrue(error.isDisplayed());
        
        // Cancel để quay về
        driver.findElement(By.xpath("//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(4)
    void testAddCurrencyLogicError() {
        goToEditPage(payGradeName);
        openAddCurrencyForm();

        // Chọn Currency (Logic chuẩn: Click dropdown -> Click option)
        selectCurrencyOption(1); // Chọn option thứ 2 trong list

        // Nhập Min > Max
        String min = testData.get("payGrade").get("minSalary").asText(); 
        String invalidMax = testData.get("payGrade").get("invalidMaxSalary").asText();

        driver.findElement(By.xpath("//label[text()='Minimum Salary']/../following-sibling::div//input")).sendKeys(min);
        driver.findElement(By.xpath("//label[text()='Maximum Salary']/../following-sibling::div//input")).sendKeys(invalidMax);
        
        driver.findElement(By.xpath("//h6[text()='Add Currency']/../..//button[@type='submit']")).click();

        // Assert lỗi logic lương
        String expectedMsg = testData.get("messages").get("salaryError").asText();
        
        // Tìm lỗi cụ thể dưới ô Max Salary
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
             By.xpath("//label[text()='Maximum Salary']/ancestor::div[contains(@class,'oxd-input-group')]//span")));
        
        assertTrue(error.getText().contains(expectedMsg), "Nội dung lỗi lương không khớp!");

        driver.findElement(By.xpath("//h6[text()='Add Currency']/../..//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(5)
    void testAddCurrencySuccess() {
        goToEditPage(payGradeName);
        openAddCurrencyForm();

        selectCurrencyOption(2); // Chọn option khác đi một chút

        driver.findElement(By.xpath("//label[text()='Minimum Salary']/../following-sibling::div//input")).sendKeys("2000");
        driver.findElement(By.xpath("//label[text()='Maximum Salary']/../following-sibling::div//input")).sendKeys("8000");
        
        driver.findElement(By.xpath("//h6[text()='Add Currency']/../..//button[@type='submit']")).click();
        verifySuccessMessage();
    }

    @Test @Order(6)
    void testDeletePayGrade() {
        driver.get(baseUrl.replace("/auth/login", "/admin/viewPayGrades"));
        // Đợi bảng load (Presence thay vì sleep)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".oxd-table-card")));
        
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(), '" + payGradeName + "')]/../..//button[i[contains(@class, 'bi-trash')]]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();
        verifySuccessMessage();
    }

    // --- Helpers (Tách hàm để code gọn) ---
    void addPayGrade(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Name']/../following-sibling::div//input"))).sendKeys(name);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
    }

    void goToEditPage(String name) {
        if (!driver.getCurrentUrl().contains("payGrade")) {
             driver.get(baseUrl.replace("/auth/login", "/admin/viewPayGrades"));
             wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".oxd-table-card")));
             wait.until(ExpectedConditions.elementToBeClickable(
                 By.xpath("//div[contains(text(), '" + name + "')]/../..//button[i[contains(@class, 'bi-pencil')]]"))).click();
        }
    }

    void openAddCurrencyForm() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h6[text()='Currencies']/..//button[contains(., 'Add')]"))).click();
        // Đợi loader biến mất
        try { wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader"))); } catch (Exception ignored) {}
    }

    void selectCurrencyOption(int index) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()='Currency']/../following-sibling::div//div[@class='oxd-select-text-input']")));
        dropdown.click();
        // Chọn bằng cách click vào option trong listbox (Thay vì Keys)
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='option'][" + (index + 1) + "]"))).click();
    }

    private void verifySuccessMessage() {
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")),
                ExpectedConditions.urlMatches(".*(payGrade|viewPayGrades).*")
            ));
            // Đóng toast ngay lập tức
            WebElement close = driver.findElement(By.cssSelector(".oxd-toast-close"));
            if (close.isDisplayed()) close.click();
        } catch (Exception ignored) {}
    }
}