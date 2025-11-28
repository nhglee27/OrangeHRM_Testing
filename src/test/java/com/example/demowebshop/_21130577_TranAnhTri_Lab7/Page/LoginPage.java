package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BasePage{
  WebDriver driver;
  WebDriverWait wait;

  By email = By.id("Email");
  By password = By.id("Password");
  By loginBtn = By.cssSelector("input.login-button");
  By errorMessage = By.cssSelector(".validation-summary-errors");

  public LoginPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void open() {
    driver.get("https://demowebshop.tricentis.com/login");
  }

  public void login(String mail, String pass) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(email)).sendKeys(mail);
    wait2s();
    driver.findElement(password).sendKeys(pass);
    wait2s();
    driver.findElement(loginBtn).click();
    wait2s();
  }

  public String getError() {
    return driver.findElement(errorMessage).getText();
  }
}
