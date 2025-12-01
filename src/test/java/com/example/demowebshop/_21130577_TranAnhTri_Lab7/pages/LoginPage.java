package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

  private WebDriver driver;
  private WebDriverWait wait;

  private By usernameField = By.name("username");
  private By passwordField = By.name("password");
  private By loginButton = By.cssSelector("button[type='submit']");

  public LoginPage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void login(String username, String password) {

    wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField))
        .sendKeys(username);

    wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField))
        .sendKeys(password);

    wait.until(ExpectedConditions.elementToBeClickable(loginButton))
        .click();
  }
}