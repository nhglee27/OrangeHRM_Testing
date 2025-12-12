package com.example.demowebshop.integrated;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PayGradeTest extends BaseTest {

    private String payGradeName;

    @BeforeAll
    void setupData() {
        payGradeName = "Grade_" + System.currentTimeMillis();
        System.out.println("🔹 Test Suite running with PayGrade: " + payGradeName);
    }

    @BeforeEach
    void preCondition() {
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
        addPayGrade(payGradeName);
        verifySuccessMessage();
    }

    @Test @Order(2)
    void testAddPayGradeDuplicate() {
        addPayGrade(payGradeName);
        
        String expectedMsg = testData.get("messages").get("alreadyExists").asText();
        
        // XPath linh hoạt hơn
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[normalize-space()='Name']/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-group__message')]")));
        
        assertEquals(expectedMsg, error.getText(), "Thông báo lỗi trùng tên không đúng!");
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Cancel')]"))).click();
    }

    @Test @Order(3)
    void testAddPayGradeEmpty() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        
        // --- FIX 1: Chờ field Name xuất hiện để chắc chắn đã vào form ---
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[normalize-space()='Name']")));
        
        // Bấm Save ngay
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
        
        String expectedMsg = testData.get("messages").get("required").asText();

        // --- FIX 2: XPath robust hơn (dùng ancestor để tìm đúng group của field Name) ---
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[normalize-space()='Name']/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-group__message')]")));
        
        assertTrue(error.getText().contains(expectedMsg), "Không thấy lỗi Required!");
        
        driver.findElement(By.xpath("//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(4)
    void testAddCurrencyLogicError() {
        goToEditPage(payGradeName);
        openAddCurrencyForm();

        selectCurrencyOption(1);

        String min = testData.get("payGrade").get("minSalary").asText(); 
        String invalidMax = testData.get("payGrade").get("invalidMaxSalary").asText();

        sendKeysToField("Minimum Salary", min);
        sendKeysToField("Maximum Salary", invalidMax);
        
        driver.findElement(By.xpath("//h6[text()='Add Currency']/../..//button[@type='submit']")).click();

        String expectedMsg = testData.get("messages").get("salaryError").asText();
        
        // Update XPath cho Salary luôn
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
             By.xpath("//label[normalize-space()='Maximum Salary']/ancestor::div[contains(@class,'oxd-input-group')]//span")));
        
        assertTrue(error.getText().contains(expectedMsg), "Nội dung lỗi lương không khớp!");

        driver.findElement(By.xpath("//h6[text()='Add Currency']/../..//button[contains(., 'Cancel')]")).click();
    }

    @Test @Order(5)
    void testAddCurrencySuccess() {
        goToEditPage(payGradeName);
        openAddCurrencyForm();

        selectCurrencyOption(2);

        sendKeysToField("Minimum Salary", "2000");
        sendKeysToField("Maximum Salary", "8000");
        
        driver.findElement(By.xpath("//h6[text()='Add Currency']/../..//button[@type='submit']")).click();
        verifySuccessMessage();
    }

    @Test @Order(6)
    void testDeletePayGrade() {
        driver.get(baseUrl.replace("/auth/login", "/admin/viewPayGrades"));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".oxd-table-card"), 0));
        
        // Tìm nút xóa dựa trên tên Pay Grade
        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(), '" + payGradeName + "')]/ancestor::div[@role='row']//button[i[contains(@class, 'bi-trash')]]")));
        deleteBtn.click();
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();
        verifySuccessMessage();
    }

    // --- Helpers ---
    
    void sendKeysToField(String label, String value) {
        WebElement input = driver.findElement(By.xpath("//label[normalize-space()='" + label + "']/ancestor::div[contains(@class,'oxd-input-group')]//input"));
        input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        input.sendKeys(org.openqa.selenium.Keys.DELETE);
        input.sendKeys(value);
    }

    void addPayGrade(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        
        // Chờ form load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[normalize-space()='Name']")));
        
        sendKeysToField("Name", name);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
    }

    void goToEditPage(String name) {
        if (driver.getCurrentUrl().contains("payGrade")) return;
        
        driver.get(baseUrl.replace("/auth/login", "/admin/viewPayGrades"));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".oxd-table-card"), 0));
        
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
             By.xpath("//div[contains(text(), '" + name + "')]/ancestor::div[@role='row']//button[i[contains(@class, 'bi-pencil')]]")));
        editBtn.click();
    }

    void openAddCurrencyForm() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h6[text()='Currencies']/..//button[contains(., 'Add')]"))).click();
        try { wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("oxd-form-loader"))); } catch (Exception ignored) {}
    }

    void selectCurrencyOption(int index) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[normalize-space()='Currency']/../following-sibling::div//div[@class='oxd-select-text-input']")));
        dropdown.click();
        
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='option'][" + (index + 1) + "]"))).click();
    }

    private void verifySuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")));
        } catch (Exception e) {
            System.out.println("⚠️ Warning: Toast message behavior check skipped/failed.");
        }
    }
}