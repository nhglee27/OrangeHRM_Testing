package com.example.demowebshop._21130577_TranAnhTri_Lab7;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.Page.LoginPage;

public class LoginTests extends BaseTest {

  @Test(priority = 1)
  public void testLoginValid() {
    LoginPage lp = new LoginPage(driver);
    lp.open();
    lp.login("test1764331606567@mail.com", "123456"); // use an existing account
    Assert.assertTrue(driver.getPageSource().contains("Log out"));
  }

  @Test(priority = 2)
  public void testLoginWrongPassword() {
    LoginPage lp = new LoginPage(driver);
    lp.open();
    lp.login("test999@mail.com", "wrongpass");
    Assert.assertTrue(lp.getError().contains("Login was unsuccessful"));
  }

  @Test(priority = 3)
  public void testLoginEmptyFields() {
    LoginPage lp = new LoginPage(driver);
    lp.open();
    lp.login("", "");
    Assert.assertTrue(lp.getError().contains("Login was unsuccessful"));
  }
}