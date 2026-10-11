package com.example.cabaidetect.controller;

import com.example.cabaidetect.dto.*;
import com.example.cabaidetect.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller autentikasi dengan OTP Gmail.
 * Endpoint: /api/auth/*
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /** POST /api/auth/register — Daftar akun baru, kirim OTP ke email */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterRequest req) {
        ApiResponse<Void> res = authService.register(req);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    /** POST /api/auth/verify-otp — Verifikasi OTP dan aktifkan akun */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(@RequestBody VerifyOtpRequest req) {
        ApiResponse<LoginResponse> res = authService.verifyOtp(req);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    /** POST /api/auth/resend-otp — Kirim ulang OTP */
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "");
        ApiResponse<Void> res = authService.resendOtp(email);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    /** POST /api/auth/login — Login dengan email + password */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest req) {
        ApiResponse<LoginResponse> res = authService.login(req);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.status(401).body(res);
    }

    /** POST /api/auth/forgot-password — Kirim OTP reset password */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "");
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    /** POST /api/auth/reset-password — Reset password dengan OTP */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest req) {
        ApiResponse<Void> res = authService.resetPassword(req);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }

    /** GET /api/auth/profile/{userId} — Ambil profil user */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<com.example.cabaidetect.model.User>> getProfile(@PathVariable Long userId) {
        ApiResponse<com.example.cabaidetect.model.User> res = authService.getProfile(userId);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.status(404).body(res);
    }

    /** PUT /api/auth/profile/{userId} — Update profil user */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<com.example.cabaidetect.model.User>> updateProfile(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body
    ) {
        String nama = body.get("namaLengkap");
        String pass = body.get("password");
        ApiResponse<com.example.cabaidetect.model.User> res = authService.updateProfile(userId, nama, pass);
        return res.isSuccess() ? ResponseEntity.ok(res) : ResponseEntity.badRequest().body(res);
    }
}
