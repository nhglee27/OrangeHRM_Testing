package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

  private void waitABit(long ms) throws InterruptedException {
    Thread.sleep(ms);
  }

  public void enterVacancy(String name, String manager) throws InterruptedException {
    wait.until(ExpectedConditions.visibilityOfElementLocated(vacancyName)).sendKeys(name);
    waitABit(2000);

    // open Job Title dropdown
    wait.until(ExpectedConditions.elementToBeClickable(jobTitleDropdown)).click();
    waitABit(2000);
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='listbox']/div[2]"))).click();
    waitABit(2000);
    WebElement managerInput = wait.until(ExpectedConditions.visibilityOfElementLocated(hiringManager));

    // Gõ chữ cái đầu để trigger suggestion
    managerInput.sendKeys(manager.substring(0, 3));
    waitABit(2000);
    // Chờ suggestion list xuất hiện
    By suggestion = By.xpath("//div[@role='listbox']//span[text()='" + manager + "']");
    wait.until(ExpectedConditions.visibilityOfElementLocated(suggestion)).click();
    waitABit(2000);
    wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    waitABit(3000);
  }

  public void clickSave() throws InterruptedException {
    wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    waitABit(2000);
  }

  public boolean isValidationDisplayed() {
    return driver.getPageSource().contains("Required")
        || driver.getPageSource().contains("Already exists")
        || driver.getPageSource().contains("Invalid");
  }
}
