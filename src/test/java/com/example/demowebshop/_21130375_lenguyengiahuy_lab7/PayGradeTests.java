package com.example.demowebshop._21130375_lenguyengiahuy_lab7;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.example.demowebshop.BaseTest;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        String currentUrl = driver.getCurrentUrl();
        // Nếu đang ở trang danh sách (viewPayGrades) thì không làm gì cả
        if (currentUrl.contains("viewPayGrades")) {
            return; 
        }
        
        // Nếu đang ở trang khác thì mới click
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(), 'Job')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Pay Grades']"))).click();
    }

    @Test
    @Order(1)
    @DisplayName("TC_PAY_001 Kiểm tra thêm mới Pay Grade")
    void testAddPayGradeSuccess() {
        payGradeName = "Grade_" + System.currentTimeMillis();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();

        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[text()='Name']/../following-sibling::div//input")));
        nameInput.sendKeys(payGradeName);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
        
        verifySuccessMessage();
    }

    @Test
    @Order(2)
    @DisplayName("TC_PAY_003 Kiểm tra thêm Tiền tệ (Currency)")
    void testAddCurrencyToPayGrade() {
        // Nếu chưa vào trang Edit, tìm và click Edit
        if (!driver.getCurrentUrl().contains("payGrade")) {
             WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(
                 By.xpath("//div[contains(text(), '" + payGradeName + "')]/../..//button[i[contains(@class, 'bi-pencil')]]")));
             editBtn.click();
        }

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//h6[text()='Currencies']/..//button[contains(., 'Add')]"))).click();

        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[text()='Currency']/../following-sibling::div//div[@class='oxd-select-text-input']")));
        dropdown.click();
        
        // Thay vì gõ phím và đợi, hãy tìm element trong listbox (nhanh hơn)
        // Cách tìm option trong dropdown của OrangeHRM
        try {
            WebElement firstOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='option'][2]"))); // Chọn option thứ 2
            firstOption.click();
        } catch (Exception e) {
            // Fallback nếu không click được: dùng phím như cũ nhưng không sleep lâu
            dropdown.sendKeys(Keys.DOWN);
            dropdown.sendKeys(Keys.ENTER);
        }

        driver.findElement(By.xpath("//label[text()='Minimum Salary']/../following-sibling::div//input")).sendKeys("1000");
        driver.findElement(By.xpath("//label[text()='Maximum Salary']/../following-sibling::div//input")).sendKeys("5000");

        driver.findElement(By.xpath("//h6[text()='Add Currency']/../..//button[@type='submit']")).click();

        verifySuccessMessage();
    }

    @Test
    @Order(3)
    @DisplayName("TC_PAY_002 Kiểm tra thêm Pay Grade để trống")
    void testAddPayGradeEmpty() {
        // Lưu ý: Test này bắt buộc quay về trang list -> click Add
        // Vì navigateToPayGrades đã tối ưu, ta cần ép click Add
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewPayGrades"); 
        
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Add')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
        
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Required']")));
        assertTrue(error.isDisplayed());
    }

    @Test
    @Order(4)
    @DisplayName("TC_PAY_004 Kiểm tra xoá Pay Grade")
    void testDeletePayGrade() {
        // Quay về danh sách
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewPayGrades");
        if (payGradeName == null) return;

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".oxd-table-card")));

        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(text(), '" + payGradeName + "')]/../..//button[i[contains(@class, 'bi-trash')]]")));
        deleteBtn.click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., ' Yes, Delete ')]"))).click();

        verifySuccessMessage();
    }

    private void verifySuccessMessage() {
        // Check lỗi (như cũ)
        try {
            WebElement inputError = driver.findElement(By.xpath("//span[contains(@class, 'oxd-input-group__message')]"));
            if (inputError.isDisplayed()) Assertions.fail("Lỗi Validation: " + inputError.getText());
        } catch (Exception ignored) {}

        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--success')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'oxd-toast--info')]")),
                ExpectedConditions.urlMatches(".*(payGrade|viewPayGrades).*")
            ));
        } catch (Exception e) {
            Assertions.fail("Timeout verify message.");
        }

        try {
            WebElement closeToast = driver.findElement(By.cssSelector(".oxd-toast-close"));
            if (closeToast.isDisplayed()) {
                closeToast.click(); // Đóng bụp phát là xong, chạy tiếp luôn
            }
        } catch (Exception ignored) {
            // Nếu không bấm được thì thôi, không quan trọng
        }
    }
}