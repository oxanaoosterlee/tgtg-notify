package com.example.tgtgnotify.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    LoginRepository loginRepository;


    public LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }


    // Log in
    public void login(String emailAddress, String password) {
        this.loginRepository.login(emailAddress, password);
    }

    public void token_expired(){
        this.loginRepository.is_token_expired();
    }



    public LiveData<LoggedInUser> getLoggedInUser(){
        return loginRepository.getLoggedInUser();
    }
}