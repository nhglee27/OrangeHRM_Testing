package com.example.demowebshop;



import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MyInfoChangeNameTest extends BaseTest {

    private static final By MY_INFO_MENU = By.xpath("//span[normalize-space()='My Info']");
    private static final By EDIT_SAVE_BTN = By.xpath("(//button[contains(@class,'oxd-button') and .//i[contains(@class,'save')]])[1]");
    private static final By TOAST_SUCCESS = By.xpath("//div[contains(@class,'oxd-toast') and contains(.,'Successfully Updated')]");

    @BeforeEach
    void loginAndGoToMyInfo_ONCE() {

        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

        // Login chỉ chạy 1 lần
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-userdropdown-tab")));

        // Vào My Info chỉ chạy 1 lần
        driver.findElement(MY_INFO_MENU).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[text()='Personal Details']")));

        System.out.println("ĐÃ LOGIN + VÀO MY INFO – BẮT ĐẦU LẶP ĐIỀN DATA");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/fullNameData.csv", numLinesToSkip = 1)
    void changeFullName_MultipleTimes(String TestCaseID,
                                      String FirstName,
                                      String MiddleName,
                                      String LastName,
                                      String ExpectedResult) {

        System.out.println("Running " + TestCaseID + " | " + FirstName + " | " + MiddleName + " | " + LastName);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click Edit (nếu đang ở trạng thái Save)
        var editBtn = driver.findElement(EDIT_SAVE_BTN);
        if (editBtn.getText().contains("Edit")) {
            editBtn.click();
            wait.until(ExpectedConditions.elementToBeClickable(By.name("firstName")));
        }

        // Clear + điền data mới (xử lý cả trường hợp rỗng hoặc có dấu ngoặc kép)
        clearAndSend(driver.findElement(By.name("firstName")), FirstName);
        clearAndSend(driver.findElement(By.name("middleName")), MiddleName);
        clearAndSend(driver.findElement(By.name("lastName")), LastName);

        // Click Save
        driver.findElement(EDIT_SAVE_BTN).click();

        // Kiểm tra kết quả
        if ("success".equalsIgnoreCase(ExpectedResult)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST_SUCCESS));
            System.out.println(TestCaseID + " → PASS (Successfully Updated)");
        } else {
            // Chờ lỗi validation xuất hiện
            boolean hasError = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//span[contains(@class,'oxd-input-group__message')]"))).size() > 0;
            Assertions.assertTrue(hasError, TestCaseID + " mong đợi lỗi nhưng lại thành công!");
            System.out.println(TestCaseID + " → FAIL như mong đợi (validation error)");
        }
    }

    // Helper để xử lý trường hợp rỗng hoặc có dấu "
    private void clearAndSend(org.openqa.selenium.WebElement element, String text) {
        element.clear();
        if (text != null && !text.isBlank() && !"\"\"".equals(text)) {
            element.sendKeys(text.replace("\"", ""));
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }
}