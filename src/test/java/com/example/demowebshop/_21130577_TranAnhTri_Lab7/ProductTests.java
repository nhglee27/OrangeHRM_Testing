package com.example.demowebshop._21130577_TranAnhTri_Lab7;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.Page.ProductPage;

public class ProductTests extends BaseTest {

  @Test
  public void testViewProducts() {
    ProductPage pp = new ProductPage(driver);
    pp.open();
    List<WebElement> items = pp.getProducts();
    Assert.assertTrue(items.size() > 0);
  }
}