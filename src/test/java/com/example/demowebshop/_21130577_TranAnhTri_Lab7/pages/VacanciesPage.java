package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VacanciesPage {
  WebDriver driver;
  WebDriverWait wait;

  By addBtn = By.xpath("//button[contains(.,'Add')]");
  By deleteBtn = By.xpath("//button[contains(., 'Delete Selected')]");
  By confirmDelete = By.xpath("//button[contains(., 'Yes, Delete')]");
  By firstEditBtn = By
      .xpath("//div[@id='app']/div/div[2]/div[2]/div/div[2]/div[3]/div/div[2]/div/div/div[6]/div/button[2]");

  private void waitABit(long ms) throws InterruptedException {
    Thread.sleep(ms);
  }

  public VacanciesPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void goToAddVacancy() throws InterruptedException {
    wait.until(ExpectedConditions.elementToBeClickable(addBtn)).click();
    waitABit(2000);
  }

  public void editFirstVacancy() throws InterruptedException {
    wait.until(ExpectedConditions.elementToBeClickable(firstEditBtn)).click();
    waitABit(2000);
  }

  public void deleteSelectedVacancies() throws InterruptedException {
    wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
    waitABit(2000);
    wait.until(ExpectedConditions.elementToBeClickable(confirmDelete)).click();
    waitABit(2000);
  }
}
