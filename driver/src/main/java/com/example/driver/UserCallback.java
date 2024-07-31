package com.example.driver;

import com.example.driver.models.UserModel;

public interface UserCallback {
    void onUserReceived(UserModel userModel);
}
