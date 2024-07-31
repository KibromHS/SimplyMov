package com.example.driver;

import com.example.driver.models.Driver;

public interface DriverCallback {
    void onDriverReceived(Driver driver);
    void onDriverNotFound(String error);
}
