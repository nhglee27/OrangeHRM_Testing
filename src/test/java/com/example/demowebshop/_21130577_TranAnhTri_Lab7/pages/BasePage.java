package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
  protected WebDriver driver;
  protected WebDriverWait wait;

  public BasePage(WebDriver driver) {
    this.driver = driver;
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  protected WebElement waitVisible(By locator) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }

  protected WebElement waitClickable(By locator) {
    return wait.until(ExpectedConditions.elementToBeClickable(locator));
  }

  protected void waitABit(long ms) throws InterruptedException {
    Thread.sleep(ms);
  }

  protected void click(By locator) throws InterruptedException {
    waitClickable(locator).click();
    waitABit(2000); // giữ nguyên yêu cầu chờ
  }

  protected void type(By locator, String text) throws InterruptedException {
    waitVisible(locator).sendKeys(text);
    waitABit(2000);
  }

  protected void clickDynamicXpath(String xpath) throws InterruptedException {
    waitClickable(By.xpath(xpath)).click();
    waitABit(2000);
  }
}