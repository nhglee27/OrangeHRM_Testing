package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.BaseTest;

public class SearchTests extends BaseTest {

  @Test
  public void testSearchProduct() {
    SearchPage sp = new SearchPage(driver);
    driver.get("https://demowebshop.tricentis.com");

    sp.search("book");

    Assert.assertTrue(sp.hasResults(), "Không tìm thấy sản phẩm!");
  }
}
