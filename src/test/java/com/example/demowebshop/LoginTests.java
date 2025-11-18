package com.example.demowebshop;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTests extends BaseTest {

    @BeforeEach
    void openLoginPage() {
        driver.manage().deleteAllCookies();
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
    }

    @AfterEach
    void logoutIfLoggedIn() {
        try {
            WebElement menu = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".oxd-userdropdown-tab")));
            menu.click();
            driver.findElement(By.xpath("//a[text()='Logout']")).click();
        } catch (Exception ignored) {}
    }

    @Order(1)
@ParameterizedTest
@CsvFileSource(resources = "/loginData.csv", numLinesToSkip = 1)
void loginDDT(String username, String password, String expected) {

    WebElement userInput = driver.findElement(By.name("username"));
    WebElement passInput = driver.findElement(By.name("password"));
    WebElement btnLogin = driver.findElement(By.cssSelector("button[type='submit']"));

    // nhập dữ liệu
    userInput.clear();
    passInput.clear();
    userInput.sendKeys(username == null ? "" : username);
    passInput.sendKeys(password == null ? "" : password);

    // click login
    btnLogin.click();

    // --- ✅ CASE 1: Username trống → phải hiện Required ---
    if (username == null || username.trim().isEmpty()) {
        WebElement userError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='username']/ancestor::div[contains(@class,'oxd-input-group')]//span"))
        );
        assertEquals("Required", userError.getText(), "Username trống nhưng không hiện 'Required'");
        assertEquals("failure", expected.toLowerCase());
        return;
    }

    // --- ✅ CASE 2: Password trống → phải hiện Required ---
    if (password == null || password.trim().isEmpty()) {
        WebElement passError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@name='password']/ancestor::div[contains(@class,'oxd-input-group')]//span"))
        );
        assertEquals("Required", passError.getText(), "Password trống nhưng không hiện 'Required'");
        assertEquals("failure", expected.toLowerCase());
        return;
    }

    // --- ✅ CASE 3: Đã nhập đủ → xử lý SUCCESS / FAIL ---
    try {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/dashboard"),
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".oxd-alert-content-text"))
        ));
    } catch (TimeoutException e) {
        fail("Timeout - không xác định kết quả login");
    }

    boolean loginSuccess = driver.getCurrentUrl().contains("/dashboard");
    boolean loginFailed = driver.findElements(By.cssSelector(".oxd-alert-content-text")).size() > 0;

    if (expected.equalsIgnoreCase("success")) {
        assertTrue(loginSuccess, "Expected SUCCESS nhưng không vào dashboard");
    } else {
        assertTrue(loginFailed || !loginSuccess, "Expected FAIL nhưng login thành công");
    }
}

}
