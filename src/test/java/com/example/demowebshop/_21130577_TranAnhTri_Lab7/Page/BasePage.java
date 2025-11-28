package com.example.demowebshop._21130577_TranAnhTri_Lab7.Page;

public class BasePage {
  public void wait2s() {
    try {
      Thread.sleep(2000); // delay 2 giây
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
