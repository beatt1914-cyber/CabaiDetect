package com.example.cabaidetect.dto;

public class RegisterRequest {
    private String namaLengkap;
    private String email;
    private String password;
    private String confirmPassword;

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    // backward compat
    public String getEmailOrUsername() { return email; }
    public void setEmailOrUsername(String v) { this.email = v; }
}
