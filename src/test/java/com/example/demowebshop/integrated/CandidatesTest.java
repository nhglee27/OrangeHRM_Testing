package com.example.demowebshop.integrated;

import java.time.Duration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.AddCandidatePage;
import com.example.demowebshop._21130577_TranAnhTri_Lab7.pages.CandidatesPage;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CandidatesTest extends BaseTest {
  CandidatesPage candidates;
  AddCandidatePage addCandidate;

  @BeforeEach
  public void initPages() {
    loginAsAdmin();
    candidates = new CandidatesPage(driver);
    addCandidate = new AddCandidatePage(driver);
  }

  // Sleep giữ nguyên theo yêu cầu
  private void waitABit(long ms) throws InterruptedException {
    Thread.sleep(ms);
  }

  // ---------- ADD ----------
  @Test
  @Order(1)
  public void testAddCandidate() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");

    candidates.goToAddCandidate();
    waitABit(2000);

    addCandidate.enterValidCandidate("Anh", "Tri", "anhtri@example.com");
    waitABit(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement successMsg = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@id='oxd-toaster_1']/div/div/div[2]/p")));

    Assertions.assertTrue(successMsg.isDisplayed(), "Candidate không được thêm thành công!");
  }

  @Test
  @Order(2)
  public void testAddCandidateMissingFields() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");

    candidates.goToAddCandidate();
    waitABit(2000);

    addCandidate.clickSave();
    waitABit(2000);

    Assertions.assertTrue(addCandidate.isValidationDisplayed());
  }

  @Test
  @Order(3)
  public void testAddCandidateInvalidEmail() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    candidates.goToAddCandidate();
    waitABit(2000);

    addCandidate.enterValidCandidate("Anh", "Tri", "anhtri_email");
    waitABit(2000);

    Assertions.assertTrue(addCandidate.isValidationDisplayed());
  }

  // ---------- SEARCH ----------
  @Test
  @Order(4)
  public void SEARCH_CANDIDATE_BY_NAME_SUCCESS() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    waitABit(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    // 1. Nhập text vào search input
    WebElement searchInput = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//input[@placeholder='Type for hints...']")));
    searchInput.sendKeys("Anh");
    waitABit(2500);

    // 2. Chờ dropdown autocomplete xuất hiện
    WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//div[contains(@class,'oxd-autocomplete-dropdown')]")));

    // 3. Chọn row chứa chữ "Anh"
    WebElement option = dropdown.findElement(
        By.xpath(".//span[contains(text(),'Anh')]"));
    option.click();
    waitABit(1500);

    // 3. Submit
    WebElement searchBtn = wait.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
    searchBtn.click();
    waitABit(2000);

    WebElement result = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@role='row']//div[contains(text(),'Anh')]")));

    Assertions.assertTrue(result.isDisplayed(), "Không tìm thấy tên ứng viên khớp một phần!");
  }

  @Test
  @Order(5)
  public void SEARCH_CANDIDATE_NOT_FOUND() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    waitABit(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    WebElement searchInput = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By
            .xpath("//div[@id='app']/div/div[2]/div[2]/div/div/div[2]/form/div[2]/div/div/div/div[2]/div/div/input")));
    searchInput.sendKeys("NameThatDoesNotExist123");
    waitABit(2000);

    WebElement searchBtn = wait.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
    searchBtn.click();
    waitABit(3000);

    // Kiểm tra kết quả rỗng
    WebElement invalidMsg = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(@class,'oxd-input-field-error-message')]")));

    Assertions.assertEquals(
        "Invalid",
        invalidMsg.getText(),
        "Không hiển thị validation 'Invalid' khi không tìm thấy candidate!");
  }

  // ---------- MODIFY ----------
  @Test
  @Order(6)
  public void testModifyCandidate() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    waitABit(2000);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    candidates.editFirstCandidate();
    waitABit(2000);

    candidates.clickEditableBtn();
    waitABit(2000);

    WebElement firstNameField = addCandidate.getFirstNameField();
    firstNameField.clear();

    firstNameField.sendKeys("AnhTriUpdated");
    waitABit(2000);

    addCandidate.clickSave();
    waitABit(2000);

    WebElement firstRowName = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(//div[@role='row'])//div[contains(text(),'AnhTriUpdated')]")));

    Assertions.assertTrue(firstRowName.isDisplayed());
  }

  // ---------- DELETE ----------
  @Test
  @Order(7)
  public void testDeleteCandidate() throws InterruptedException {
    driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/recruitment/viewCandidates");
    waitABit(3000);

    driver.findElement(By.xpath("(//div[@role='row'])[2]//i")).click();
    waitABit(3000);

    candidates.deleteSelected();
    waitABit(3000);

    Assertions.assertFalse(driver.getPageSource().contains("AnhTriUpdated"));
  }
}