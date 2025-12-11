package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CandidatesPage extends BasePage {
  private By addBtn = By.xpath("//div[@id='app']/div/div[2]/div[2]/div/div[2]/div/button");
  private By editableBtn = By.xpath("//div[@id='app']/div/div[2]/div[2]/div[2]/div/div/div/label/span");
  private By deleteBtn = By.xpath("//button[contains(., 'Delete Selected')]");
  private By confirmDelete = By.xpath("//button[contains(., 'Yes, Delete')]");

  public CandidatesPage(WebDriver driver) {
    super(driver);
  }

  public void goToAddCandidate() {
    WebElement addButton = waitClickable(addBtn);
    addButton.click();
  }

  public void deleteSelected() throws InterruptedException {
    click(deleteBtn);
    click(confirmDelete);
  }

  public void editFirstCandidate() {
    WebElement editButton = waitClickable(
        By.xpath("//div[@id='app']/div/div[2]/div[2]/div/div[2]/div[3]/div/div[2]/div/div/div[7]/div/button"));
    editButton.click();
  }

  public void clickEditableBtn() throws InterruptedException {
    click(editableBtn);
  }
}
