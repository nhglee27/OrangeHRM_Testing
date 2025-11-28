package com.example.demowebshop._21130349_doxuanhau_lab7;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Bắt buộc để @BeforeAll không static
public class MyInfoChangeNameTest extends BaseTest {

    private WebDriver driver;        // Khai báo lại ở đây
    private WebDriverWait wait;

    private static final By MY_INFO_MENU = By.xpath("//span[normalize-space()='My Info']");
    private static final By EDIT_BTN     = By.xpath("//i[contains(@class,'bi-pencil-fill')]/parent::button");
    private static final By SAVE_BTN     = By.xpath("//button[normalize-space()='Save']");
    private static final By TOAST_SUCCESS = By.xpath("//div[contains(@class,'oxd-toast') and contains(.,'Successfully Updated')]");

    @BeforeAll
    void setupOnce() {
        // Khởi tạo driver 1 lần duy nhất
        this.driver = createDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

        // Login
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-userdropdown-tab")));

        // Vào My Info
        driver.findElement(MY_INFO_MENU).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[normalize-space()='Personal Details']")));

        // Nhấn Edit lần đầu
        clickEditButton();

        System.out.println("ĐÃ LOGIN + VÀO MY INFO + SẴN SÀNG EDIT");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/fullNameData.csv", numLinesToSkip = 1)
    void changeFullName_MultipleTimes(String TestCaseID,
                                      String FirstName,
                                      String MiddleName,
                                      String LastName,
                                      String ExpectedResult) {

        System.out.println("\n=== Running " + TestCaseID + " | " + FirstName + " | " + MiddleName + " | " + LastName + " ===");

        clearAndSend(By.name("firstName"), FirstName);
        clearAndSend(By.name("middleName"), MiddleName);
        clearAndSend(By.name("lastName"), LastName);

        driver.findElement(SAVE_BTN).click();

        if ("success".equalsIgnoreCase(ExpectedResult)) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST_SUCCESS));
            System.out.println(TestCaseID + " → PASS (Successfully Updated)");

            // Sau khi Save thành công → nhấn Edit lại cho test case tiếp theo
            // clickEditButton();

        } else {
            // Chờ lỗi validation
            boolean hasError = !wait.until(ExpectedConditions
                .visibilityOfAllElementsLocatedBy(By.xpath("//span[contains(@class,'oxd-input-group__message')]")))
                .isEmpty();

            Assertions.assertTrue(hasError, TestCaseID + " mong đợi lỗi nhưng lại thành công!");
            System.out.println(TestCaseID + " → PASS (Có lỗi validation như mong đợi)");
            // Nếu lỗi → vẫn đang ở trạng thái Edit → không cần nhấn Edit lại
        }
    }

    private void clickEditButton() {
        WebElement editBtn = wait.until(ExpectedConditions.elementToBeClickable(EDIT_BTN));
        editBtn.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.name("firstName")));
    }

    private void clearAndSend(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
        el.clear();
        if (text != null && !text.isBlank() && !"\"\"".equals(text.trim())) {
            el.sendKeys(text.replace("\"", "").trim());
        }
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("ĐÃ ĐÓNG BROWSER SAU KHI HOÀN TẤT TẤT CẢ TEST CASE");
        }
    }
}