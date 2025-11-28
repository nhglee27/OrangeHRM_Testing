package com.example.demowebshop._21130576_PhamTrungTri_Lab7;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Job_Title_Test extends BaseTest {

    // Constants
    private static final int SHORT_DELAY = 300;
    private static final int MEDIUM_DELAY = 500;
    private static final int LONG_DELAY = 1000;
    private static final int FORM_DELAY = 1000;
    private static final int TOAST_DELAY = 1000;

    // Locators
    private static final By ADD_BUTTON = By.xpath("//button[contains(., 'Add')]");
    private static final By JOB_TITLE_INPUT = By.xpath("//label[text()='Job Title']/../following-sibling::div//input");
    private static final By SAVE_BUTTON = By.cssSelector("button[type='submit']");
    private static final By ERROR_MESSAGE = By.cssSelector("span.oxd-input-field-error-message");
    private static final By SUCCESS_TOAST = By.cssSelector(".oxd-toast-content");
    private static final By JOB_SPEC_UPLOAD_ICON = By.xpath(
            "//label[contains(text(),'Job Specification')]/ancestor::div[contains(@class,'oxd-input-group')]//i[contains(@class,'bi-upload')]");
    private static final By JOB_SPEC_FILE_INPUT = By.xpath(
            "//label[contains(text(),'Job Specification')]/ancestor::div[contains(@class,'oxd-input-group')]//input[@type='file']");
    private static final By JOB_SPEC_ERROR = By.xpath(
            "//label[contains(text(),'Job Specification')]/ancestor::div[contains(@class,'oxd-input-group')]//span[contains(@class,'oxd-input-field-error-message')]");

    // Helper Methods
    private void navigateToJobTitles() {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Admin']"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(), 'Job')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Job Titles']"))).click();
        wait.until(ExpectedConditions.urlContains("/admin/viewJobTitleList"));
    }

    private void setupTest() {
        loginAsAdmin();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        safeSleep(LONG_DELAY);
        navigateToJobTitles();
        safeSleep(LONG_DELAY);
    }

    private void safeSleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void safeClick(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
        }
    }

    private void scrollIntoView(WebElement element) {
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        safeSleep(MEDIUM_DELAY);
    }

    private void clickAddButton() {
        wait.until(ExpectedConditions.elementToBeClickable(ADD_BUTTON)).click();
        safeSleep(FORM_DELAY);
    }

    private WebElement getJobTitleInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(JOB_TITLE_INPUT));
    }

    private void enterJobTitle(String title) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(getJobTitleInput()));
        input.click();
        safeSleep(SHORT_DELAY);
        input.clear();
        input.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
        input.sendKeys(org.openqa.selenium.Keys.DELETE);
        safeSleep(SHORT_DELAY);
        input.sendKeys(title);
        safeSleep(LONG_DELAY);
    }

    private void clickSaveButton() {
        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(SAVE_BUTTON));
        safeSleep(MEDIUM_DELAY);
        saveButton.click();
        safeSleep(FORM_DELAY);
    }

    private void verifySuccessToast() {
        WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TOAST));
        String toastText = successToast.getText();
        assertTrue(toastText.contains("Successfully Saved") || toastText.contains("Success"),
                "Toast message không đúng. Nội dung: " + toastText);
    }

    private void verifyError(String expectedError) {
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
        String errorText = errorMessage.getText();
        assertTrue(errorText.contains(expectedError),
                "Error message không đúng. Nội dung: " + errorText);
    }

    private void verifyJobSpecError(String expectedError) {
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(JOB_SPEC_ERROR));
        String errorText = errorMessage.getText();
        assertTrue(errorText.contains(expectedError),
                "Error message không đúng. Nội dung: " + errorText);
    }

    private File createTestFile(String prefix, String suffix, int sizeInMB) throws IOException {
        File testFile = File.createTempFile(prefix, suffix);
        byte[] content = new byte[sizeInMB * 1024 * 1024];
        java.util.Arrays.fill(content, (byte) 'A');
        Files.write(testFile.toPath(), content);
        return testFile;
    }

    private void uploadFile(File file) {
        WebElement uploadIcon = wait.until(ExpectedConditions.elementToBeClickable(JOB_SPEC_UPLOAD_ICON));
        scrollIntoView(uploadIcon);
        safeClick(uploadIcon);
        safeSleep(MEDIUM_DELAY);

        WebElement attachmentInput = wait.until(ExpectedConditions.presenceOfElementLocated(JOB_SPEC_FILE_INPUT));
        attachmentInput.sendKeys(file.getAbsolutePath());
        safeSleep(LONG_DELAY);
    }

    // Test Cases
    @Test
    public void testAddJobTitle_ValidData() {
        setupTest();
        clickAddButton();

        enterJobTitle("Software Engineer");

        String titleValue = getJobTitleInput().getAttribute("value");
        assertTrue(titleValue != null && titleValue.contains("Software Engineer"),
                "Data chưa được nhập vào Job Title. Giá trị: " + titleValue);

        getJobTitleInput().sendKeys(org.openqa.selenium.Keys.TAB);
        safeSleep(MEDIUM_DELAY);

        clickSaveButton();
        verifySuccessToast();
        safeSleep(TOAST_DELAY);
    }

    @Test
    public void testAddJobTitle_MissingRequiredField() {
        setupTest();
        clickAddButton();

        clickSaveButton();
        safeSleep(TOAST_DELAY);

        verifyError("Required");
        safeSleep(TOAST_DELAY);
    }

    @Test
    public void testAddJobTitle_DuplicateName() {
        loginAsAdmin();
        navigateToJobTitles();

        clickAddButton();
        enterJobTitle("Software Engineer");

        String titleValue = getJobTitleInput().getAttribute("value");
        assertTrue(titleValue != null && titleValue.contains("Software Engineer"),
                "Data chưa được nhập vào Job Title. Giá trị: " + titleValue);

        getJobTitleInput().sendKeys(org.openqa.selenium.Keys.TAB);
        safeSleep(MEDIUM_DELAY);

        clickSaveButton();
        verifyError("Already exists");
    }

    @Test
    public void testUploadJobTitleDescription_InvalidFileType() {
        loginAsAdmin();
        navigateToJobTitles();
        clickAddButton();

        File testFile = null;
        try {
            testFile = File.createTempFile("test_invalid_file", ".exe");
            Files.write(testFile.toPath(), "This is a test file with invalid extension".getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo file test: " + e.getMessage(), e);
        }

        uploadFile(testFile);
        safeSleep(TOAST_DELAY);

        boolean errorFound = false;
        try {
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(JOB_SPEC_ERROR));
            String errorText = errorMessage.getText();
            if (errorText.contains("File type not allowed") || errorText.contains("not allowed")
                    || errorText.contains("invalid")) {
                errorFound = true;
            }
        } catch (Exception e) {
            // Error not found immediately
        }

        if (!errorFound) {
            clickSaveButton();
            safeSleep(TOAST_DELAY);
            verifyJobSpecError("File type not allowed");
        }

        safeSleep(TOAST_DELAY);
    }

    @Test
    public void testEditJobTitle() {
        setupTest();

        WebElement editIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".oxd-table-body .oxd-table-row:first-child .bi-pencil-fill")));
        scrollIntoView(editIcon);
        safeClick(editIcon);
        safeSleep(FORM_DELAY);

        WebElement jobTitleInput = wait.until(ExpectedConditions.presenceOfElementLocated(JOB_TITLE_INPUT));
        scrollIntoView(jobTitleInput);

        jobTitleInput = wait.until(ExpectedConditions.elementToBeClickable(jobTitleInput));
        jobTitleInput.click();
        safeSleep(SHORT_DELAY);

        jobTitleInput.clear();
        jobTitleInput.sendKeys(org.openqa.selenium.Keys.CONTROL + "a");
        jobTitleInput.sendKeys(org.openqa.selenium.Keys.DELETE);
        safeSleep(SHORT_DELAY);

        jobTitleInput.sendKeys("Senior Software Engineer");
        safeSleep(LONG_DELAY);

        String titleValue = jobTitleInput.getAttribute("value");
        assertTrue(titleValue != null && titleValue.contains("Senior Software Engineer"),
                "Data chưa được nhập vào Job Title. Giá trị: " + titleValue);

        clickSaveButton();
        verifySuccessToast();
        safeSleep(TOAST_DELAY);
    }

    @Test
    public void testDeleteJobTitle() {
        setupTest();

        WebElement deleteIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".oxd-table-body .oxd-table-row:first-child .bi-trash")));
        deleteIcon.click();
        safeSleep(FORM_DELAY);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'oxd-dialog') or contains(@class,'modal')]")));
        safeSleep(LONG_DELAY);

        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'oxd-button--label-danger') and contains(@class,'orangehrm-button-margin') and contains(.,'Yes, Delete')]")));
        confirmButton.click();

        WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TOAST));
        String toastText = successToast.getText();
        assertTrue(toastText.contains("Successfully Deleted") || toastText.contains("Deleted"),
                "Toast message không đúng. Nội dung: " + toastText);

        safeSleep(TOAST_DELAY);
    }

    @Test
    public void testViewJobTitleList() {
        loginAsAdmin();
        navigateToJobTitles();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".oxd-table-body")));
        safeSleep(LONG_DELAY);

        WebElement jobTitleList = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".oxd-table-body")));

        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'start'});", jobTitleList);
        safeSleep(LONG_DELAY);

        js.executeScript("window.scrollBy(0, 200);");
        safeSleep(MEDIUM_DELAY);

        assertTrue(jobTitleList.isDisplayed());
    }

    @Test
    public void testJobTitleMaxLength() {
        loginAsAdmin();
        navigateToJobTitles();
        clickAddButton();

        WebElement jobTitleInput = getJobTitleInput();
        String longTitle = "A very long job title that exceeds the maximum length allowed by 100 characters and this is additional text to make it longer than 100 characters";
        jobTitleInput.sendKeys(longTitle);

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(SAVE_BUTTON));
        saveButton.click();

        verifyError("Should not exceed");
    }

    //Test Failed
    @Test
    public void testJobTitleInvalidCharacters() {
        loginAsAdmin();
        navigateToJobTitles();
        clickAddButton();

        WebElement jobTitleInput = getJobTitleInput();
        jobTitleInput.sendKeys("Job@Title!$");

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(SAVE_BUTTON));
        saveButton.click();

        verifyError("Invalid characters");
    }

    @Test
    public void testBulkDeleteJobTitles() {
        loginAsAdmin();
        navigateToJobTitles();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".oxd-table-body")));
        safeSleep(LONG_DELAY);

        WebElement selectAllCheckbox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".oxd-table-header input[type='checkbox']")));
        safeSleep(LONG_DELAY);

        scrollIntoView(selectAllCheckbox);
        safeClick(selectAllCheckbox);
        safeSleep(LONG_DELAY);

        assertTrue(selectAllCheckbox.isSelected(), "Select All checkbox không được chọn");

        var checkboxes = driver.findElements(By.cssSelector(".oxd-table-body input[type='checkbox']:checked"));
        assertTrue(checkboxes.size() > 0, "Không có checkbox nào được chọn");
        safeSleep(LONG_DELAY);

        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'oxd-button--label-danger') and contains(.,'Delete Selected')]")));

        scrollIntoView(deleteButton);
        safeClick(deleteButton);

        safeSleep(LONG_DELAY);
        var dialogs = driver.findElements(By.cssSelector(".oxd-dialog-container, [role='dialog'], .oxd-dialog"));
        assertTrue(!dialogs.isEmpty(), "Dialog xác nhận không xuất hiện");
        safeSleep(LONG_DELAY);

        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'oxd-button--label-danger') and contains(@class,'orangehrm-button-margin') and contains(.,'Yes, Delete')]")));

        scrollIntoView(confirmButton);
        safeClick(confirmButton);
        safeSleep(LONG_DELAY);

        WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TOAST));
        String toastText = successToast.getText();
        assertTrue(toastText.contains("Successfully Deleted") || toastText.contains("Deleted"),
                "Toast message không đúng: " + toastText);
    }

    @Test
    public void testUploadJobTitleDescription_FileSizeExceeded() {
        setupTest();
        clickAddButton();

        File testFile = null;
        try {
            testFile = createTestFile("test_large_file", ".pdf", 2);
            System.out.println("✅ Đã tạo file test với kích thước: " + testFile.length() + " bytes ("
                    + (testFile.length() / (1024 * 1024)) + " MB)");
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo file test: " + e.getMessage(), e);
        }

        uploadFile(testFile);
        verifyJobSpecError("Attachment Size Exceeded");
    }
}
