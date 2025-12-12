package com.example.demowebshop.integrated;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MyInfoChangeNameTests extends BaseTest {

    private static final By MY_INFO_MENU = By.xpath("//span[normalize-space()='My Info']");
    private static final By SAVE_BTN     = By.xpath("//div[@class='orangehrm-horizontal-padding orangehrm-vertical-padding']//button[@type='submit']");
    private static final By TOAST_SUCCESS = By.xpath("//div[contains(@class,'oxd-toast') and contains(.,'Successfully Updated')]");

    @BeforeAll
    void setupOnce() {
        // 1. Tự động login từ BaseTest
        loginAsAdmin();

        // 2. Vào My Info
        wait.until(ExpectedConditions.elementToBeClickable(MY_INFO_MENU)).click();
        
        // Chờ form Personal Details load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstName")));

        System.out.println("✅ ĐÃ LOGIN + VÀO MY INFO (SẴN SÀNG EDIT)");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/fullNameData.csv", numLinesToSkip = 1)
    void changeFullName_MultipleTimes(String TestCaseID,
                                      String FirstName,
                                      String MiddleName,
                                      String LastName,
                                      String ExpectedResult) {

        System.out.println("\n=== Running " + TestCaseID + " | " + FirstName + " | " + MiddleName + " | " + LastName + " ===");

        // Đảm bảo đang ở tab Personal Details
        // (Trong trường hợp chạy nhiều test xen kẽ, có thể thêm bước click lại MyInfo ở đây nếu cần)

        clearAndSend(By.name("firstName"), FirstName);
        clearAndSend(By.name("middleName"), MiddleName);
        clearAndSend(By.name("lastName"), LastName);

        driver.findElement(SAVE_BTN).click();

        if ("success".equalsIgnoreCase(ExpectedResult)) {
            // Verify Success Toast
            wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST_SUCCESS));
            System.out.println(TestCaseID + " → PASS (Successfully Updated)");
            
            // Đợi Toast biến mất để không che các element lần chạy sau
            wait.until(ExpectedConditions.invisibilityOfElementLocated(TOAST_SUCCESS));

        } else {
            // Chờ lỗi validation xuất hiện
            boolean hasError = !wait.until(ExpectedConditions
                .visibilityOfAllElementsLocatedBy(By.xpath("//span[contains(@class,'oxd-input-group__message')]")))
                .isEmpty();

            Assertions.assertTrue(hasError, TestCaseID + " mong đợi lỗi nhưng lại không thấy message lỗi!");
            System.out.println(TestCaseID + " → PASS (Có lỗi validation như mong đợi)");
        }
    }

    private void clearAndSend(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        
        // Dùng Ctrl+A Delete để clear sạch hơn .clear()
        el.sendKeys(Keys.CONTROL + "a");
        el.sendKeys(Keys.DELETE);

        if (text != null && !text.isBlank() && !"\"\"".equals(text.trim())) {
            // Xử lý dữ liệu CSV đôi khi có dấu ngoặc kép thừa
            el.sendKeys(text.replace("\"", "").trim());
        }
    }
}