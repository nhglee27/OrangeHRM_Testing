package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
  private By usernameField = By.name("username");
  private By passwordField = By.name("password");
  private By loginButton = By.cssSelector("button[type='submit']");

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  public void login(String username, String password) throws InterruptedException {
    type(usernameField, username);
    type(passwordField, password);
    click(loginButton);
  }
}