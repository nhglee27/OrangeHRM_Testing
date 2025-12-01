package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddCandidatePage {

  WebDriver driver;
  WebDriverWait wait;

  By firstName = By.name("firstName");
  By lastName = By.name("lastName");
  By email = By.xpath("//div[@id='app']/div/div[2]/div[2]/div/div/form/div[3]/div/div/div/div[2]/input");
  By saveBtn = By.xpath("//button[@type='submit']");

  public AddCandidatePage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void enterValidCandidate(String fName, String lName, String mail) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(firstName)).sendKeys(fName);
    wait.until(ExpectedConditions.visibilityOfElementLocated(lastName)).sendKeys(lName);
    wait.until(ExpectedConditions.visibilityOfElementLocated(email)).sendKeys(mail);

    // Thêm sleep ngắn hoặc chờ element clickable
    wait.until(ExpectedConditions.elementToBeClickable(saveBtn));
    driver.findElement(saveBtn).click();
  }

  public void clickSave() {
    wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
  }

  public boolean isValidationDisplayed() {
    return driver.getPageSource().contains("Required")
        || driver.getPageSource().contains("Invalid");
  }

  public WebElement getFirstNameField() {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(firstName));
  }
}
