package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductPage extends BasePage {
  WebDriver driver;
  WebDriverWait wait;

  By productTitle = By.cssSelector(".product-title");

  public ProductPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void open() {
    driver.get("https://demowebshop.tricentis.com/books");
    wait2s();
  }

  public List<WebElement> getProducts() {
    return driver.findElements(productTitle);
  }
}
