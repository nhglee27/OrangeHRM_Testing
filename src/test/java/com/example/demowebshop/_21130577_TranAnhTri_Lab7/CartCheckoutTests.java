package com.example.demowebshop._21130577_TranAnhTri_Lab7;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.Page.CheckoutPage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.Page.LoginPage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.Page.ProductDetailPage;

public class CartCheckoutTests extends BaseTest {

  @Test
  public void testAddToCartAndCheckout() {
    // 1. Login trước để checkout được
    LoginPage lp = new LoginPage(driver);
    lp.open();
    lp.login("test1764331606567@mail.com", "123456");
    Assert.assertTrue(driver.getPageSource().contains("Log out"));

    // 2. Mở sản phẩm + thêm vào giỏ
    ProductDetailPage pd = new ProductDetailPage(driver);
    pd.openFirstBook();
    pd.addToCart();
    Assert.assertTrue(pd.addedSuccessfully());

    // 3. Tiến hành thanh toán
    CheckoutPage cp = new CheckoutPage(driver);
    cp.openCart();
    cp.proceedCheckout();

    Assert.assertTrue(cp.isCheckoutPage(), "Không vào được trang Checkout!");
  }
}
