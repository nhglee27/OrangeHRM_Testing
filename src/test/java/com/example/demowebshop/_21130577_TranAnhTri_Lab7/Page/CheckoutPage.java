package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPage extends BasePage {
  WebDriver driver;
  WebDriverWait wait;

  By cartLink = By.cssSelector(".cart-label");
  By checkboxTerms = By.id("termsofservice");
  By checkoutBtn = By.id("checkout");

  public CheckoutPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void openCart() {
    driver.findElement(cartLink).click();
    wait2s();
  }

  public void proceedCheckout() {
    driver.findElement(checkboxTerms).click();
    wait2s();
    driver.findElement(checkoutBtn).click();
    wait2s();
  }

  public boolean isCheckoutPage() {
    return driver.getPageSource().contains("Checkout");
  }
}
