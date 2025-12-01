package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CandidatesPage {
  WebDriver driver;
  WebDriverWait wait;

  By addBtn = By.xpath("//div[@id='app']/div/div[2]/div[2]/div/div[2]/div/button");
  By editableBtn = By.xpath("//div[@id='app']/div/div[2]/div[2]/div[2]/div/div/div/label/span");
  By deleteBtn = By.xpath("//button[contains(., 'Delete Selected')]");
  By confirmDelete = By.xpath("//button[contains(., 'Yes, Delete')]");

  public CandidatesPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void goToAddCandidate() {
    // driver.findElement(addBtn).click();
    WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//div[@id='app']/div/div[2]/div[2]/div/div[2]/div/button")));
    addButton.click();
  }

  public void deleteSelected() {
    wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
    wait.until(ExpectedConditions.elementToBeClickable(confirmDelete)).click();
  }

  public void editFirstCandidate() {
    // Click Edit button trong row đó
    WebElement editButton = wait
        .until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@id='app']/div/div[2]/div[2]/div/div[2]/div[3]/div/div[2]/div/div/div[7]/div/button")));
    editButton.click();
  }

  public void clickEditableBtn() {
    wait.until(ExpectedConditions.elementToBeClickable(editableBtn)).click();
  }
}
