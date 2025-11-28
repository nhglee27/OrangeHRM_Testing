package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisterPage extends BasePage {
  WebDriver driver;

  public RegisterPage(WebDriver driver) {
    this.driver = driver;
  }

  public void open() {
    driver.get("https://demowebshop.tricentis.com/register");
    wait2s();
  }

  public void register(String fn, String ln, String email, String pass, String confirm) {
    driver.findElement(By.id("FirstName")).sendKeys(fn);
    wait2s();

    driver.findElement(By.id("LastName")).sendKeys(ln);
    wait2s();

    driver.findElement(By.id("Email")).sendKeys(email);
    wait2s();

    driver.findElement(By.id("Password")).sendKeys(pass);
    wait2s();

    driver.findElement(By.id("ConfirmPassword")).sendKeys(confirm);
    wait2s();

    driver.findElement(By.id("register-button")).click();
    wait2s();
  }

  public String getResult() {
    wait2s();
    return driver.findElement(By.className("result")).getText();
  }
}