package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddVacancyPage extends BasePage {
  By vacancyName = By.xpath("//label[text()='Vacancy Name']/../following-sibling::div/input");
  By jobTitleDropdown = By.xpath("//label[text()='Job Title']/../following-sibling::div//i");
  By hiringManager = By.xpath("//label[text()='Hiring Manager']/../following-sibling::div//input");
  By saveBtn = By.xpath("//button[@type='submit']");

  public AddVacancyPage(WebDriver driver) {
    super(driver);
  }

  public void enterVacancy(String name, String manager) throws InterruptedException {
    type(vacancyName, name);

    click(jobTitleDropdown);

    clickDynamicXpath("//div[@role='listbox']/div[2]");

    WebElement managerInput = waitVisible(hiringManager);
    managerInput.sendKeys(manager.substring(0, 3));
    waitABit(2000);

    String suggestionXpath = "//div[@role='listbox']//span[text()='" + manager + "']";
    clickDynamicXpath(suggestionXpath);

    click(saveBtn);
    waitABit(2000); // giữ nguyên yêu cầu
  }

  public void clickSave() throws InterruptedException {
    click(saveBtn);
  }

  public boolean isValidationDisplayed() {
    return driver.getPageSource().contains("Required")
        || driver.getPageSource().contains("Already exists")
        || driver.getPageSource().contains("Invalid");
  }
}
