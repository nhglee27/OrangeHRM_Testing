package com.example.demowebshop._21130577_TranAnhTri_Lab7.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class VacanciesPage extends BasePage {

  By addBtn = By.xpath("//button[contains(.,'Add')]");
  By deleteBtn = By.xpath("//button[contains(., 'Delete Selected')]");
  By confirmDelete = By.xpath("//button[contains(., 'Yes, Delete')]");
  By firstEditBtn = By
      .xpath("//div[@id='app']/div/div[2]/div[2]/div/div[2]/div[3]/div/div[2]/div/div/div[6]/div/button[2]");

  public VacanciesPage(WebDriver driver) {
    super(driver);
  }

  public void goToAddVacancy() throws InterruptedException {
    click(addBtn);
  }

  public void editFirstVacancy() throws InterruptedException {
    click(firstEditBtn);
  }

  public void deleteSelectedVacancies() throws InterruptedException {
    click(deleteBtn);
    click(confirmDelete);
  }
}