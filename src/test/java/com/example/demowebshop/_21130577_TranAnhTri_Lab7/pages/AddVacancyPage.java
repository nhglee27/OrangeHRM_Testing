package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AddVacancyPage {
  WebDriver driver;
  WebDriverWait wait;

  By vacancyName = By.xpath("//label[text()='Vacancy Name']/../following-sibling::div/input");
  By jobTitleDropdown = By.xpath("//label[text()='Job Title']/../following-sibling::div//i");
  By hiringManager = By.xpath("//label[text()='Hiring Manager']/../following-sibling::div//input");
  By saveBtn = By.xpath("//button[@type='submit']");

  public AddVacancyPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void enterVacancy(String name, String manager) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(vacancyName)).sendKeys(name);

    // open Job Title dropdown
    wait.until(ExpectedConditions.elementToBeClickable(jobTitleDropdown)).click();
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='listbox']/div[2]"))).click();

    wait.until(ExpectedConditions.visibilityOfElementLocated(hiringManager)).sendKeys(manager);

    wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
  }

  public void clickSave() {
    wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
  }

  public boolean isValidationDisplayed() {
    return driver.getPageSource().contains("Required")
        || driver.getPageSource().contains("Already exists")
        || driver.getPageSource().contains("Invalid");
  }
}
