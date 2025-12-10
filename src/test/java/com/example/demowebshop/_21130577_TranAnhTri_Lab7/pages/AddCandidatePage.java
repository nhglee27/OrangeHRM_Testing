package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddCandidatePage extends BasePage {
  private By firstName = By.name("firstName");
  private By lastName = By.name("lastName");
  private By email = By.xpath("//div[@id='app']/div/div[2]/div[2]/div/div/form/div[3]/div/div/div/div[2]/input");
  private By saveBtn = By.xpath("//button[@type='submit']");

  public AddCandidatePage(WebDriver driver) {
    super(driver);
  }

  public void enterValidCandidate(String fName, String lName, String mail) throws InterruptedException {
    type(firstName, fName);
    type(lastName, lName);
    type(email, mail);
    waitClickable(saveBtn);
    driver.findElement(saveBtn).click(); // giữ nguyên logic của bạn
  }

  public void clickSave() throws InterruptedException {
    click(saveBtn);
  }

  public boolean isValidationDisplayed() {
    return driver.getPageSource().contains("Required")
        || driver.getPageSource().contains("Invalid");
  }

  public WebElement getFirstNameField() {
    return waitVisible(firstName);
  }
}