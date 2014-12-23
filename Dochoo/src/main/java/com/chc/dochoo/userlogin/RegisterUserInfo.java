package com.chc.dochoo.userlogin;

/**
 * Created by HenryW on 1/8/14.
 */
public abstract class RegisterUserInfo {
    private String email,
    passwordUnhashed,
    passwordConfirm;

    protected RegisterUserInfo() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordUnhashed() {
        return passwordUnhashed;
    }

    public void setPasswordUnhashed(String passwordUnhashed) {
        this.passwordUnhashed = passwordUnhashed;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
