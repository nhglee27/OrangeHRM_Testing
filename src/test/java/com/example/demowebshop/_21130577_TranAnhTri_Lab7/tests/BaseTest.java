package com.example.demowebshop._21130577_TranAnhTri_Lab7.tests;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.LoginPage;

public class BaseTest {
  protected static WebDriver driver;
  protected static LoginPage login;

  @BeforeAll
  public static void setupSuite() throws InterruptedException {
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

    login = new LoginPage(driver);
    login.login("Admin", "admin123");
  }

  @AfterAll
  public static void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }
}