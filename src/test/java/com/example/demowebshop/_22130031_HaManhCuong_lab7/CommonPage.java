package com.example.demowebshop._22130031_HaManhCuong_lab7;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CommonPage {
    WebDriver driver;
    WebDriverWait wait;

    public CommonPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Hàm đăng nhập chung
    public void login(String username, String password) {
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    // Xử lý Dropdown tùy chỉnh của OrangeHRM (Leave Type)
    public void selectCustomDropdown(String labelText, String optionText) {
        String dropdownXpath = "//label[contains(text(),'" + labelText + "')]/following::div[contains(@class,'oxd-select-text')][1]";
        driver.findElement(By.xpath(dropdownXpath)).click();

        By optionLocator = By.xpath("//div[@role='option']//span[contains(text(),'" + optionText + "')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(optionLocator));
        driver.findElement(optionLocator).click();
    }

    // Xử lý Autocomplete (Employee Name)
    public void selectAutocomplete(String labelText, String searchText) throws InterruptedException {
        String inputXpath = "//label[contains(text(),'" + labelText + "')]/following::input[1]";
        driver.findElement(By.xpath(inputXpath)).sendKeys(searchText);

        Thread.sleep(3000);

        By suggestionLocator = By.xpath("//div[@role='listbox']//div[1]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionLocator));
        driver.findElement(suggestionLocator).click();
    }

    // Chọn ngày tháng (Đã sửa lỗi nối chuỗi)
    public void enterDate(String labelText, String dateYYYYMMDD) {
        String inputXpath = "//label[contains(text(),'" + labelText + "')]/following::input[1]";
        WebElement dateInput = driver.findElement(By.xpath(inputXpath));

        // SỬA LỖI: Xóa dữ liệu cũ trước khi nhập
        // Dùng tổ hợp phím Ctrl + A -> Delete để xóa sạch (tốt hơn .clear() thuần túy trên các web hiện đại)
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        dateInput.sendKeys(Keys.DELETE);

        // Sau khi xóa xong mới nhập ngày mới
        dateInput.sendKeys(dateYYYYMMDD);

        // Click ra ngoài để đóng lịch
        driver.findElement(By.xpath("//h6")).click();
    }
}