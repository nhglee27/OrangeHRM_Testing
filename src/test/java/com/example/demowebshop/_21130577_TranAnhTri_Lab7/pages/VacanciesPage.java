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
  By firstEditBtn = By.xpath("(//button[contains(@class,'oxd-icon-button')])[2]");

  public VacanciesPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void goToAddVacancy() {
    wait.until(ExpectedConditions.elementToBeClickable(addBtn)).click();
  }

  public void editFirstVacancy() {
    wait.until(ExpectedConditions.elementToBeClickable(firstEditBtn)).click();
  }

  public void deleteSelectedVacancies() {
    wait.until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();
    wait.until(ExpectedConditions.elementToBeClickable(confirmDelete)).click();
  }
}
