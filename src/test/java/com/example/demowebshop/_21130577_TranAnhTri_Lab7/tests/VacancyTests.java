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
    waitABit(500);

    addVacancy.enterVacancy("Automation QA Vacancy", "Linda Anderson");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p")));

    Assert.assertTrue(successMsg.isDisplayed(), "Vacancy không được tạo thành công!");
  }

  @Test(priority = 2)
  public void testAddVacancyMissingFields() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();
    waitABit(500);

    addVacancy.clickSave();
    waitABit(500);

    Assert.assertTrue(addVacancy.isValidationDisplayed());
  }

  @Test(priority = 3)
  public void testAddVacancyInvalidName() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();
    waitABit(500);

    addVacancy.enterVacancy("@@@###", "Linda Anderson");
    waitABit(500);

    Assert.assertTrue(addVacancy.isValidationDisplayed());
  }

  @Test(priority = 4)
  public void testAddVacancyExisting() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();
    waitABit(500);

    addVacancy.enterVacancy("Automation QA Vacancy", "Linda Anderson");
    waitABit(500);

    Assert.assertTrue(addVacancy.isValidationDisplayed(), "Không hiển thị lỗi trùng tên!");
  }

  // ---------------------- MODIFY VACANCY ----------------------
  @Test(priority = 5, dependsOnMethods = "testAddVacancySuccess")
  public void testModifyVacancy() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");

    vacancies.editFirstVacancy();
    waitABit(500);

    WebElement vacancyNameField = driver
        .findElement(By.xpath("//label[text()='Vacancy Name']/../following-sibling::div/input"));
    vacancyNameField.clear();
    vacancyNameField.sendKeys("Automation QA Updated");

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    waitABit(1000);

    Assert.assertTrue(driver.getPageSource().contains("Automation QA Updated"));
  }

  @Test(priority = 6, dependsOnMethods = "testAddVacancySuccess")
  public void testModifyVacancyWithDuplicateName() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");

    vacancies.editFirstVacancy();
    waitABit(500);

    WebElement vacancyNameField = driver
        .findElement(By.xpath("//label[text()='Vacancy Name']/../following-sibling::div/input"));
    vacancyNameField.clear();
    vacancyNameField.sendKeys("Automation QA Vacancy"); // đã tồn tại

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    waitABit(1000);

    Assert.assertTrue(addVacancy.isValidationDisplayed());
  }

  // ---------------------- DELETE VACANCY ----------------------
  @Test(priority = 7, dependsOnMethods = "testAddVacancySuccess")
  public void testDeleteVacancy() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    waitABit(500);

    driver.findElement(By.xpath("(//div[@role='row'])[2]//i")).click();
    waitABit(300);

    vacancies.deleteSelectedVacancies();
    waitABit(500);

    Assert.assertFalse(driver.getPageSource().contains("Automation QA Updated"));
  }

  @AfterClass
  public void tearDown() {
    driver.quit();
  }
}
