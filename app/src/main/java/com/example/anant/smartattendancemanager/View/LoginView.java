package com.example.anant.smartattendancemanager.View;

public interface LoginView {

    void OnSuccess();

    void onFailed();

    void showInvalidPassword();

    void showInvalidEmail();

    void showEmailFieldRequired();

    void showNameFieldRequired();

    void toggleProgressVisibility(boolean visible);

}
