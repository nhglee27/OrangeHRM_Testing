package com.example.demowebshop._21130577_TranAnhTri_Lab7.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
  public static WebDriver initDriver() {
    WebDriverManager.chromedriver().setup(); // ✔ tự động tải và config chromedriver

    WebDriver driver = new ChromeDriver();
    driver.manage().window().maximize();
    return driver;
  }
}
