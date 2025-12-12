package com.example.demowebshop._21130577_TranAnhTri_Lab7.tests;

import java.time.Duration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.AddVacancyPage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.VacanciesPage;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VacancyTest extends BaseTest {
  VacanciesPage vacancies;
  AddVacancyPage addVacancy;

  @BeforeEach
  public void initPages() {
    vacancies = new VacanciesPage(driver);
    addVacancy = new AddVacancyPage(driver);
  }

  // ---------------------- ADD VACANCY ----------------------
  @Test
  @Order(1)
  public void testAddVacancySuccess() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();

    addVacancy.enterVacancy("Automation QA", "Thomas Kutty Benny");

    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    Thread.sleep(4000);

    Assertions.assertTrue(driver.getPageSource().contains("Automation QA"));
  }

  @Test
  @Order(2)
  public void testAddVacancyMissingFields() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();

    addVacancy.clickSave();

    Assertions.assertTrue(addVacancy.isValidationDisplayed());
  }

  @Test
  @Order(3)
  public void testAddVacancyExisting() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.goToAddVacancy();

    addVacancy.enterVacancy("Automation QA", "Thomas Kutty Benny");

    Assertions.assertTrue(addVacancy.isValidationDisplayed());
  }

  // ---------------------- SEARCH VACANCY ----------------------
  @Test
  @Order(4)
  public void SEARCH_VACANCY_BY_NAME_SUCCESS() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    Thread.sleep(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // 1. Click mở dropdown Vacancy
    WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//label[text()='Vacancy']/../following-sibling::div//div[contains(@class,'oxd-select-text')]")));
    dropdown.click();
    Thread.sleep(1000);

    // 2. Chọn option chứa tên Vacancy vừa tạo
    WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//div[@role='option']//span[contains(text(),'Automation QA')]")));
    option.click();
    Thread.sleep(1000);

    // 3. Submit tìm kiếm
    WebElement searchBtn = driver.findElement(By.xpath("//button[@type='submit']"));
    searchBtn.click();
    Thread.sleep(2000);

    // 4. Kiểm tra có kết quả
    WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//div[@role='row']//div[contains(text(),'Automation QA')]")));

    Assertions.assertTrue(result.isDisplayed(), "Không tìm thấy Vacancy sau khi search!");
  }

  // ---------------------- MODIFY VACANCY ----------------------
  @Test
  @Order(5)
  public void testModifyVacancy() throws InterruptedException {

    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    vacancies.editFirstVacancy();

    Thread.sleep(2000);

    WebElement vacancyNameField = driver.findElement(
        By.xpath("//label[text()='Vacancy Name']/../following-sibling::div/input"));
    vacancyNameField.clear();
    Thread.sleep(2000);

    vacancyNameField.sendKeys("Automation QA Updated");
    Thread.sleep(2000);

    driver.findElement(By.xpath("//button[@type='submit']")).click();
    Thread.sleep(2000);

    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    Thread.sleep(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement firstRowName = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("(//div[@role='row'])//div[contains(text(),'Automation QA Updated')]")));

    Assertions.assertTrue(firstRowName.isDisplayed());
  }

  // ---------------------- DELETE VACANCY ----------------------
  @Test
  @Order(6)
  public void testDeleteVacancy() throws InterruptedException {

    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewJobVacancy");
    Thread.sleep(2000);

    driver.findElement(By.xpath("(//div[@role='row'])[2]//i")).click();
    Thread.sleep(2000);

    vacancies.deleteSelectedVacancies();
    Thread.sleep(2000);

    Assertions.assertFalse(driver.getPageSource().contains("Automation QA Updated"));
  }
}