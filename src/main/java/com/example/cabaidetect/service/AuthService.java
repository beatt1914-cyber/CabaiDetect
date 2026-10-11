package com.example.cabaidetect.service;

import com.example.cabaidetect.dto.*;
import com.example.cabaidetect.model.User;
import com.example.cabaidetect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;
    @Autowired private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final SecureRandom secureRandom = new SecureRandom();

    // ─── Register ─────────────────────────────────────────────────────────────

    public ApiResponse<Void> register(RegisterRequest req) {
        // Validasi
        if (req.getNamaLengkap() == null || req.getNamaLengkap().isBlank())
            return ApiResponse.fail("Nama lengkap harus diisi");
        if (req.getEmail() == null || !req.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$"))
            return ApiResponse.fail("Format email tidak valid");
        if (req.getPassword() == null || req.getPassword().length() < 8)
            return ApiResponse.fail("Password minimal 8 karakter");
        if (!req.getPassword().equals(req.getConfirmPassword()))
            return ApiResponse.fail("Konfirmasi password tidak cocok");

        // Cek duplikat
        if (userRepository.existsByEmail(req.getEmail().toLowerCase()))
            return ApiResponse.fail("Email sudah terdaftar");

        // Buat user (belum verified)
        User user = new User();
        user.setNamaLengkap(req.getNamaLengkap().trim());
        user.setEmail(req.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setEmailVerified(false);

        // Generate OTP
        String otp = generateOtp();
        user.setOtpCode(passwordEncoder.encode(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);

        // Kirim OTP via Gmail
        boolean sent = emailService.sendOtpEmail(user.getEmail(), user.getNamaLengkap(), otp);
        if (sent) {
            return ApiResponse.ok("Registrasi berhasil! Cek email kamu untuk kode OTP.", null);
        } else {
            return ApiResponse.ok("Registrasi berhasil! [Mode Dev] Kode OTP kamu adalah: " + otp, null);
        }
    }

    // ─── Verify OTP ──────────────────────────────────────────────────────────

    public ApiResponse<LoginResponse> verifyOtp(VerifyOtpRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail().toLowerCase());
        if (opt.isEmpty()) return ApiResponse.fail("Email tidak ditemukan");

        User user = opt.get();

        if (user.isEmailVerified())
            return ApiResponse.fail("Email sudah diverifikasi sebelumnya");

        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry()))
            return ApiResponse.fail("Kode OTP sudah kedaluwarsa. Minta OTP baru.");

        if (!passwordEncoder.matches(req.getOtp(), user.getOtpCode()))
            return ApiResponse.fail("Kode OTP salah");

        // Aktifkan akun
        user.setEmailVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Kirim welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getNamaLengkap());

        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());
        return ApiResponse.ok("Verifikasi berhasil! Selamat datang.", buildLoginResponse(user, token));
    }

    // ─── Resend OTP ──────────────────────────────────────────────────────────

    public ApiResponse<Void> resendOtp(String email) {
        Optional<User> opt = userRepository.findByEmail(email.toLowerCase());
        if (opt.isEmpty()) return ApiResponse.fail("Email tidak ditemukan");

        User user = opt.get();
        if (user.isEmailVerified()) return ApiResponse.fail("Email sudah terverifikasi");

        // Rate limit: tunggu 1 menit dari OTP sebelumnya
        if (user.getOtpExpiry() != null && LocalDateTime.now().isBefore(user.getOtpExpiry().minusMinutes(9)))
            return ApiResponse.fail("Mohon tunggu 1 menit sebelum meminta OTP baru");

        String otp = generateOtp();
        user.setOtpCode(passwordEncoder.encode(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        boolean sent = emailService.sendOtpEmail(user.getEmail(), user.getNamaLengkap(), otp);
        if (sent) {
            return ApiResponse.ok("Kode OTP baru telah dikirim ke email kamu.", null);
        } else {
            return ApiResponse.ok("Kode OTP baru kamu: " + otp, null);
        }
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    public ApiResponse<LoginResponse> login(LoginRequest req) {
        if (req.getEmail() == null || req.getPassword() == null)
            return ApiResponse.fail("Email dan password harus diisi");

        Optional<User> opt = userRepository.findByEmail(req.getEmail().toLowerCase());
        if (opt.isEmpty()) return ApiResponse.fail("Email atau password salah");

        User user = opt.get();

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            return ApiResponse.fail("Email atau password salah");

        if (!user.isEmailVerified())
            return ApiResponse.fail("Email belum diverifikasi. Cek inbox atau spam kamu.");

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());
        return ApiResponse.ok("Login berhasil!", buildLoginResponse(user, token));
    }

    // ─── Forgot Password ─────────────────────────────────────────────────────

    public ApiResponse<Void> forgotPassword(String email) {
        Optional<User> opt = userRepository.findByEmail(email.toLowerCase());
        // Always return success to prevent email enumeration
        if (opt.isEmpty()) return ApiResponse.ok("Jika email terdaftar, kode OTP akan dikirim.", null);

        User user = opt.get();
        String otp = generateOtp();
        user.setOtpCode(passwordEncoder.encode(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        boolean sent = emailService.sendOtpEmail(user.getEmail(), user.getNamaLengkap(), otp);
        if (sent) {
            return ApiResponse.ok("Jika email terdaftar, kode OTP akan dikirim.", null);
        } else {
            return ApiResponse.ok("Kode OTP reset password kamu: " + otp, null);
        }
    }

    // ─── Reset Password ──────────────────────────────────────────────────────

    public ApiResponse<Void> resetPassword(ResetPasswordRequest req) {
        Optional<User> opt = userRepository.findByEmail(req.getEmail().toLowerCase());
        if (opt.isEmpty()) return ApiResponse.fail("Email tidak ditemukan");

        User user = opt.get();

        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry()))
            return ApiResponse.fail("Kode OTP sudah kedaluwarsa");

        if (!passwordEncoder.matches(req.getOtp(), user.getOtpCode()))
            return ApiResponse.fail("Kode OTP salah");

        if (req.getNewPassword() == null || req.getNewPassword().length() < 8)
            return ApiResponse.fail("Password baru minimal 8 karakter");

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        user.setEmailVerified(true); // pastikan akun aktif
        userRepository.save(user);

        return ApiResponse.ok("Password berhasil diubah. Silakan login.", null);
    }

    // ─── Profile Management ──────────────────────────────────────────────────

    public ApiResponse<User> getProfile(Long userId) {
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ApiResponse.fail("User tidak ditemukan");
        User user = opt.get();
        user.setPassword(null);
        user.setOtpCode(null);
        return ApiResponse.ok("Berhasil", user);
    }

    public ApiResponse<User> updateProfile(Long userId, String namaLengkap, String newPassword) {
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ApiResponse.fail("User tidak ditemukan");
        User user = opt.get();
        if (namaLengkap != null && !namaLengkap.isBlank()) {
            user.setNamaLengkap(namaLengkap.trim());
        }
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 6) return ApiResponse.fail("Password minimal 6 karakter");
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        User saved = userRepository.save(user);
        saved.setPassword(null);
        saved.setOtpCode(null);
        return ApiResponse.ok("Profil berhasil diperbarui", saved);
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private String generateOtp() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private LoginResponse buildLoginResponse(User user, String token) {
        return new LoginResponse(
                token,
                user.getId(),
                user.getNamaLengkap(),
                user.getEmail(),
                user.getRole()
        );
    }
}
