package com.example.demowebshop._21130349_doxuanhau_lab7;

import java.io.FileReader;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactDetailsTest extends BaseTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private Config config;

    // Locator chung
    private final By MY_INFO_MENU = By.xpath("//span[normalize-space()='My Info']");
    private final By CONTACT_DETAILS_LINK = By.xpath("//a[normalize-space()='Contact Details']");
    private final By EDIT_BUTTON = By.xpath("//button[.//i[contains(@class,'bi-pencil-fill')]]");
    private final By SAVE_BUTTON = By.xpath("//button[.//i[contains(@class,'bi-check2')]]");
    private final By TOAST = By.xpath("//div[contains(@class,'oxd-toast-content')]/p[contains(@class,'oxd-text--toast-message')]");

    @BeforeAll
    void setupOnce() throws Exception {
        // Khởi tạo driver 1 lần duy nhất
        this.driver = createDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Load config từ JSON
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("src/test/resources/_21130349_doxuanhau_testdata.json")) {
            config = gson.fromJson(reader, Config.class);
        }

        // Login + vào Contact Details + nhấn Edit lần đầu
        driver.get(config.baseUrl);

        driver.findElement(By.name("username")).sendKeys(config.admin.username);
        driver.findElement(By.name("password")).sendKeys(config.admin.password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".oxd-userdropdown-tab")));

        driver.findElement(MY_INFO_MENU).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[text()='Personal Details']")));

        driver.findElement(CONTACT_DETAILS_LINK).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h6[text()='Contact Details']")));

        // BẮT BUỘC: Nhấn Edit trước khi điền
        clickWhenReady(EDIT_BUTTON);

        System.out.println("ĐÃ VÀO CONTACT DETAILS + NHẤN EDIT - SẴN SÀNG TEST");
    }

    @Test
    @DisplayName("Save valid contact details")
    void testSaveValidContactDetails() {
        fillContactDetails(config.contactDetails);
        clickWhenReady(SAVE_BUTTON);
        assertToastContains("Successfully Updated");

        // Sau khi Save → nhấn Edit lại cho test tiếp theo
        clickWhenReady(EDIT_BUTTON);
    }

    @Test
    @DisplayName("Required fields validation")
    void testRequiredFields() {
        clearAllFields();

        // Xóa các field bắt buộc
        clearField("//label[text()='Street 1']/following::input[1]");
        clearField("//label[text()='City']/following::input[1]");
        // Country: click để mở dropdown rồi click ra ngoài để trigger validation
        clickWhenReady(By.xpath("//div[text()='-- Select --']"));
        driver.findElement(By.tagName("body")).click(); // click ngoài để đóng

        clickWhenReady(SAVE_BUTTON);

        assertErrorVisible("//label[text()='Street 1']/following::span[contains(@class,'oxd-input-group__message')]");
        assertErrorVisible("//label[text()='City']/following::span[contains(@class,'oxd-input-group__message')]");
        assertErrorVisible("//div[text()='-- Select --']/following::span[contains(text(),'Required')]");
    }

    @Test
    @DisplayName("Invalid ZIP code")
    void testInvalidZipCode() {
        fillMinimalRequiredFields();
        sendKeysToField("//label[text()='Zip/Postal Code']/following::input[1]", config.invalidContactDetails.zipcode);
        clickWhenReady(SAVE_BUTTON);
        assertErrorMessage("//label[text()='Zip/Postal Code']/following::span", config.messages.invalid);
    }

    @Test
    @DisplayName("Invalid home phone")
    void testInvalidHomePhone() {
        fillMinimalRequiredFields();
        sendKeysToField("//label[text()='Home']/following::input[1]", config.invalidContactDetails.homePhone);
        clickWhenReady(SAVE_BUTTON);
        assertErrorMessage("//label[text()='Home']/following::span", config.messages.invalid);
    }

    @Test
    @DisplayName("Invalid work email")
    void testInvalidWorkEmail() {
        fillMinimalRequiredFields();
        sendKeysToField("//label[text()='Work Email']/following::input[1]", config.invalidContactDetails.workEmail);
        clickWhenReady(SAVE_BUTTON);
        assertErrorMessage("//label[text()='Work Email']/following::span", config.messages.invalidEmail);
    }

    @Test
    @DisplayName("Save with only required fields")
    void testSaveWithOptionalFieldsEmpty() {
        clearAllFields();
        sendKeysToField("//label[text()='Street 1']/following::input[1]", "123 Minimal Street");
        sendKeysToField("//label[text()='City']/following::input[1]", "Ho Chi Minh");
        selectCountry("Vietnam");

        clickWhenReady(SAVE_BUTTON);
        assertToastContains("Successfully Updated");

        clickWhenReady(EDIT_BUTTON); // sẵn sàng cho test sau
    }

    // ============================= HELPER METHODS =============================

    private void clickWhenReady(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private void fillContactDetails(ContactDetails data) {
        clearAllFields();
        sendKeysToField("//label[text()='Street 1']/following::input[1]", data.street1);
        sendKeysToField("//label[text()='Street 2']/following::input[1]", data.street2);
        sendKeysToField("//label[text()='City']/following::input[1]", data.city);
        sendKeysToField("//label[text()='State/Province']/following::input[1]", data.state);
        sendKeysToField("//label[text()='Zip/Postal Code']/following::input[1]", data.zipcode);
        selectCountry(data.country);
        sendKeysToField("//label[text()='Home']/following::input[1]", data.homePhone);
        sendKeysToField("//label[text()='Mobile']/following::input[1]", data.mobilePhone);
        sendKeysToField("//label[text()='Work']/following::input[1]", data.workPhone);
        sendKeysToField("//label[text()='Work Email']/following::input[1]", data.workEmail);
        sendKeysToField("//label[text()='Other Email']/following::input[1]", data.otherEmail);
    }

    private void fillMinimalRequiredFields() {
        clearAllFields();
        sendKeysToField("//label[text()='Street 1']/following::input[1]", "123 Test");
        sendKeysToField("//label[text()='City']/following::input[1]", "HCM");
        selectCountry("Vietnam");
    }

    private void clearAllFields() {
        driver.findElements(By.cssSelector("form.oxd-form input[type='text']"))
              .forEach(el -> {
                  el.click();
                  el.clear();
              });
    }

    private void clearField(String xpath) {
        WebElement el = driver.findElement(By.xpath(xpath));
        el.click();
        el.clear();
    }

    private void sendKeysToField(String xpath, String value) {
        if (value != null && !value.isBlank()) {
            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            el.click();
            el.clear();
            el.sendKeys(value);
        }
    }

    private void selectCountry(String countryText) {
        By dropdown = By.xpath("//div[text()='-- Select --' or contains(text(),'Country')]/parent::div");
        clickWhenReady(dropdown);
        By option = By.xpath("//div[@role='listbox']//span[normalize-space()='" + countryText + "']");
        wait.until(ExpectedConditions.elementToBeClickable(option)).click();
    }

    private void assertToastContains(String expected) {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST)).getText();
        assertTrue(text.contains(expected), "Toast nên chứa: '" + expected + "' nhưng thực tế: '" + text + "'");
    }

    private void assertErrorMessage(String xpath, String expected) {
        String actual = driver.findElement(By.xpath(xpath)).getText().trim();
        assertEquals(expected, actual, "Lỗi không đúng tại: " + xpath);
    }

    private void assertErrorVisible(String xpath) {
        boolean visible = !driver.findElements(By.xpath(xpath)).isEmpty();
        assertTrue(visible, "Phải hiển thị lỗi Required tại: " + xpath);
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("ĐÃ ĐÓNG BROWSER SAU KHI HOÀN TẤT CONTACT DETAILS TEST");
        }
    }

    // ============================= POJO CLASSES =============================
    static class Config {
        String baseUrl;
        Admin admin;
        ContactDetails contactDetails;
        InvalidData invalidContactDetails;
        Messages messages;
    }

    static class Admin { String username; String password; }
    static class ContactDetails {
        String street1, street2, city, state, zipcode, country;
        String homePhone, mobilePhone, workPhone, workEmail, otherEmail;
    }
    static class InvalidData { String zipcode, homePhone, workEmail; }
    static class Messages { String required = "Required"; String invalid = "Allows numbers and only + - / ( )"; String invalidEmail = "Expected valid email format"; }
}