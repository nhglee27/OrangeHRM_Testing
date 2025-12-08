package com.example.demowebshop._21130577_TranAnhTri_Lab7.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.AddVacancyPage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.LoginPage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.VacanciesPage;

public class VacancyTests {
  WebDriver driver;
  LoginPage login;
  VacanciesPage vacancies;
  AddVacancyPage addVacancy;

  @BeforeClass
  public void setup() {
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

    login = new LoginPage(driver);
    vacancies = new VacanciesPage(driver);
    addVacancy = new AddVacancyPage(driver);

    login.login("Admin", "admin123");
  }

  private void waitABit(long ms) throws InterruptedException {
    Thread.sleep(ms);
  }

  // ---------------------- ADD VACANCY ----------------------
  @Test(priority = 1)
  public void testAddVacancySuccess() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();
    waitABit(2000);
    addVacancy.enterVacancy("Automation QA", "Thomas Kutty Benny");

    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    Thread.sleep(2000);

    boolean hasVacancy = driver.getPageSource().contains("Automation QA");
    Assert.assertTrue(hasVacancy, "Không thấy vacancy trong danh sách nhưng bỏ qua toast!");
  }

  @Test(priority = 2)
  public void testAddVacancyMissingFields() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();

    addVacancy.clickSave();

    Assert.assertTrue(addVacancy.isValidationDisplayed());
  }

  @Test(priority = 3)
  public void testAddVacancyExisting() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();

    addVacancy.enterVacancy("Automation QA", "Thomas Kutty Benny");

    Assert.assertTrue(addVacancy.isValidationDisplayed(), "Không hiển thị lỗi trùng tên!");
  }

  // ---------------------- MODIFY VACANCY ----------------------
  @Test(priority = 4, dependsOnMethods = "testAddVacancySuccess")
  public void testModifyVacancy() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");

    vacancies.editFirstVacancy();
    waitABit(2000);

    WebElement vacancyNameField = driver
        .findElement(By.xpath("//label[text()='Vacancy Name']/../following-sibling::div/input"));
    vacancyNameField.clear();
    waitABit(2000);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    vacancyNameField.sendKeys("Automation QA Updated");
    waitABit(2000);

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    waitABit(2000);
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    waitABit(2000);
    WebElement firstRowName = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("(//div[@role='row'])[2]//div[contains(text(),'Automation QA Updated')]")));
    Assert.assertTrue(firstRowName.isDisplayed());
    // Assert.assertTrue(driver.getPageSource().contains(" Automation QA Updated"));
  }

  // ---------------------- DELETE VACANCY ----------------------
  @Test(priority = 5, dependsOnMethods = "testAddVacancySuccess")
  public void testDeleteVacancy() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    waitABit(2000);

    driver.findElement(By.xpath("(//div[@role='row'])[2]//i")).click();
    waitABit(2000);

    vacancies.deleteSelectedVacancies();
    waitABit(2000);

    Assert.assertFalse(driver.getPageSource().contains("Automation QA Updated"));
  }

  @AfterClass
  public void tearDown() {
    driver.quit();
  }
}
