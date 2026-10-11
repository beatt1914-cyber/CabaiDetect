package com.example.cabaidetect.controller;

import com.example.cabaidetect.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check / beranda endpoint.
 * Mewakili langkah (3) Beranda pada flowchart.
 */
@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/")
    public ApiResponse<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>();
        data.put("appName", "CabaiDetect");
        data.put("tagline", "Tanaman Sehat, Hasil Maksimal");
        data.put("version", "1.0.0");
        data.put("serverTime", LocalDateTime.now().toString());
        data.put("menu", new String[]{
            "Deteksi Penyakit (/api/prediction/detect)",
            "Riwayat (/api/prediction/history/{userId})",
            "Pengaturan Akun (/api/auth/profile/{userId})"
        });

        return new ApiResponse<>(true, "Selamat Datang di CabaiDetect", data);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return new ApiResponse<>(true, "Server berjalan normal", "OK");
    }
}
