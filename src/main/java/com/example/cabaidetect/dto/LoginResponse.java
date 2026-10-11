package com.example.cabaidetect.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private String namaLengkap;
    private String email;
    private String role;

    public LoginResponse() {}

    public LoginResponse(String token, Long userId, String namaLengkap, String email, String role) {
        this.token = token;
        this.userId = userId;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
