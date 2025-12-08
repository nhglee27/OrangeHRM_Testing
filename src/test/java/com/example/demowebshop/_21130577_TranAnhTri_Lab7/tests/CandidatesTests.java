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

import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.AddCandidatePage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.CandidatesPage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.LoginPage;

public class CandidatesTests {
  WebDriver driver;
  LoginPage login;
  CandidatesPage candidates;
  AddCandidatePage addCandidate;

  @BeforeClass
  public void setup() {
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    driver.get("http://opensource-demo.orangehrmlive.com/web/index.php/auth/login");

    LoginPage loginPage = new LoginPage(driver);
    candidates = new CandidatesPage(driver);
    addCandidate = new AddCandidatePage(driver);

    loginPage.login("Admin", "admin123");
  }

  // Helper method để sleep dễ quan sát
  private void waitABit(long ms) throws InterruptedException {
    Thread.sleep(ms);
  }

  // ---------------------- ADD CANDIDATE ----------------------
  @Test(priority = 1)
  public void testAddCandidate() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    candidates.goToAddCandidate();
    waitABit(2000);

    addCandidate.enterValidCandidate("John", "Doe", "john.doe@example.com");
    waitABit(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p")));
    Assert.assertTrue(successMsg.isDisplayed(), "Candidate không được thêm thành công!");
  }

  @Test(priority = 2)
  public void testAddCandidateMissingFields() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    candidates.goToAddCandidate();
    waitABit(2000);

    addCandidate.clickSave();
    waitABit(2000);

    Assert.assertTrue(addCandidate.isValidationDisplayed());
  }

  @Test(priority = 3)
  public void testAddCandidateInvalidEmail() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    candidates.goToAddCandidate();
    waitABit(2000);

    addCandidate.enterValidCandidate("Test", "User", "invalid_email");
    waitABit(2000);

    Assert.assertTrue(addCandidate.isValidationDisplayed());
  }

  // ---------------------- MODIFY CANDIDATE ----------------------
  @Test(priority = 4, dependsOnMethods = "testAddCandidate")
  public void testModifyCandidate() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    waitABit(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // Click edit row đầu tiên
    candidates.editFirstCandidate();
    waitABit(2000);

    // Click nút toggle editable để mở form edit
    candidates.clickEditableBtn();
    waitABit(2000);

    // Sử dụng AddCandidatePage để modify
    WebElement firstNameField = addCandidate.getFirstNameField();
    firstNameField.clear();
    waitABit(2000);
    firstNameField.sendKeys("JohnUpdated");
    waitABit(2000);

    addCandidate.clickSave();
    waitABit(2000);

    // Verify
    // Wait cho thông báo thành công hoặc row cập nhật
    WebElement firstRowName = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("(//div[@role='row'])[2]//div[contains(text(),'JohnUpdated')]")));
    Assert.assertTrue(firstRowName.isDisplayed());
  }

  // ---------------------- DELETE CANDIDATE ----------------------
  @Test(priority = 5, dependsOnMethods = "testAddCandidate")
  public void testDeleteCandidate() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    waitABit(3000);

    // Click checkbox of first row
    driver.findElement(By.xpath("(//div[@role='row'])[2]//i")).click();
    waitABit(3000);

    candidates.deleteSelected();
    waitABit(3000);

    Assert.assertFalse(driver.getPageSource().contains("JohnUpdated"));
  }

  @AfterClass
  public void tearDown() {
    driver.quit();
  }
}