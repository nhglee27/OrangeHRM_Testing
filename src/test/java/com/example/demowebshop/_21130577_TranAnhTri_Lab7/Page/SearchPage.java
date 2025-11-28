package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchPage extends BasePage {
  WebDriver driver;
  WebDriverWait wait;

  By searchBox = By.id("small-searchterms");
  By searchButton = By.cssSelector("input.search-box-button");
  By resultItems = By.cssSelector(".product-item");

  public SearchPage(WebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  public void search(String keyword) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(searchBox))
        .sendKeys(keyword);
    wait2s();
    driver.findElement(searchButton).click();
    wait2s();
  }

  public boolean hasResults() {
    return driver.findElements(resultItems).size() > 0;
  }
}
