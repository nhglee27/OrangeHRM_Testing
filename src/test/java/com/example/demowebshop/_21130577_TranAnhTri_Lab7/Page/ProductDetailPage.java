package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProductDetailPage extends BasePage {
  WebDriver driver;
  WebDriverWait wait;

  By addToCartBtn = By.cssSelector("input[value='Add to cart']");
  By successMsg = By.cssSelector(".content");

  public ProductDetailPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void openFirstBook() {
    driver.get("https://demowebshop.tricentis.com/books");
    wait2s();
    driver.findElement(By.cssSelector(".product-title a")).click();
    wait2s();
  }

  public void addToCart() {
    driver.findElement(addToCartBtn).click();
    wait2s();
  }

  public boolean addedSuccessfully() {
    return driver.getPageSource().contains("The product has been added");
  }
}
