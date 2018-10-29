package com.example.anant.smartattendancemanager.View;

public interface LoginView {

    void OnSuccess();

    void onFailed();

    void showInvalidPassword();

    void showInvalidEmail();

    void showEmailFieldRequired();

    void toggleProgressVisibility(boolean visible);

}
