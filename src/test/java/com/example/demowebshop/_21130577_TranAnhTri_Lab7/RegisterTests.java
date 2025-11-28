package com.example.demowebshop._21130577_TranAnhTri_Lab7;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.Page.RegisterPage;

public class RegisterTests extends BaseTest {

  @Test(priority = 1)
  public void testValidRegister() {
    RegisterPage rp = new RegisterPage(driver);
    rp.open();
    rp.register("Test", "User", "test" + System.currentTimeMillis() + "@mail.com", "123456", "123456");
    Assert.assertTrue(rp.getResult().contains("completed"));
  }

  @Test(priority = 2)
  public void testRegisterMissingFields() {
    RegisterPage rp = new RegisterPage(driver);
    rp.open();
    rp.register("", "", "", "", "");
    Assert.assertTrue(driver.getPageSource().contains("is required"));
  }

  @Test(priority = 3)
  public void testRegisterInvalidEmail() {
    RegisterPage rp = new RegisterPage(driver);
    rp.open();
    rp.register("A", "B", "invalid_email", "123456", "123456");
    Assert.assertTrue(driver.getPageSource().contains("Wrong email"));
  }
}
