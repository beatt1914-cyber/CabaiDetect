package com.example.cabaidetect.controller;

import com.example.cabaidetect.dto.ApiResponse;
import com.example.cabaidetect.dto.PredictionResponse;
import com.example.cabaidetect.model.PredictionRecord;
import com.example.cabaidetect.model.User;
import com.example.cabaidetect.repository.PredictionRecordRepository;
import com.example.cabaidetect.repository.UserRepository;
import com.example.cabaidetect.service.FileStorageService;
import com.example.cabaidetect.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller untuk proses deteksi penyakit dan riwayat prediksi.
 * Mewakili langkah (4) Input Data, (5) Pengambilan Gambar, (8) Proses Prediksi,
 * (9) Hasil Prediksi, (10) Saran Penanganan, (11) Riwayat & Laporan
 */
@RestController
@RequestMapping("/api/prediction")
public class PredictionController {

    @Autowired
    private PredictionRecordRepository predictionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private PredictionService predictionService;

    /**
     * Endpoint utama: Deteksi penyakit cabai.
     * Menerima foto tanaman + data lingkungan, lalu menjalankan prediksi
     * dan menyimpan hasilnya ke database.
     *
     * POST /api/prediction/detect (multipart/form-data)
     */
    @PostMapping("/detect")
    public ResponseEntity<ApiResponse<PredictionResponse>> detect(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "jenisCabai", required = false, defaultValue = "Cabai Rawit") String jenisCabai,
            @RequestParam(value = "bagianTanaman", required = false, defaultValue = "Buah") String bagianTanaman,
            @RequestParam("suhuUdara") Double suhuUdara,
            @RequestParam("kelembabanUdara") Double kelembabanUdara,
            @RequestParam("phTanah") Double phTanah,
            @RequestParam("kelembabanTanah") Double kelembabanTanah,
            @RequestParam("curahHujan") Double curahHujan,
            @RequestParam("intensitasCahaya") Integer intensitasCahaya,
            @RequestParam("umurTanaman") Integer umurTanaman,
            @RequestParam("adaHama") Boolean adaHama,
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) {
        // 1. Validasi user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "User tidak ditemukan", null));
        }

        // 2. Simpan foto jika ada
        String fotoUrl = null;
        if (foto != null && !foto.isEmpty()) {
            fotoUrl = fileStorageService.storeFile(foto);
        }

        // 3. Jalankan prediksi (analisis citra visual + data lingkungan + varietas cabai)
        Map<String, Object> hasilPrediksi = predictionService.predict(
                jenisCabai, bagianTanaman, suhuUdara, kelembabanUdara, phTanah,
                kelembabanTanah, curahHujan, intensitasCahaya, umurTanaman, adaHama,
                foto
        );

        String penyakit = (String) hasilPrediksi.get("penyakit");
        Double confidence = (Double) hasilPrediksi.get("confidenceScore");
        String saran = (String) hasilPrediksi.get("saranPenanganan");

        // 4. Simpan ke database
        PredictionRecord record = new PredictionRecord();
        record.setUser(userOpt.get());
        record.setFotoUrl(fotoUrl);
        record.setJenisCabai(jenisCabai);
        record.setBagianTanaman(bagianTanaman);
        record.setSuhuUdara(suhuUdara);
        record.setKelembabanUdara(kelembabanUdara);
        record.setPhTanah(phTanah);
        record.setKelembabanTanah(kelembabanTanah);
        record.setCurahHujan(curahHujan);
        record.setIntensitasCahaya(intensitasCahaya);
        record.setUmurTanaman(umurTanaman);
        record.setAdaHama(adaHama);
        record.setPenyakitTerdeteksi(penyakit);
        record.setConfidenceScore(confidence);
        record.setSaranPenanganan(saran);

        PredictionRecord savedRecord = predictionRepository.save(record);

        // 5. Bangun response
        PredictionResponse response = new PredictionResponse();
        response.setRecordId(savedRecord.getId());
        response.setJenisCabai(jenisCabai);
        response.setBagianTanaman(bagianTanaman);
        response.setPenyakitTerdeteksi(penyakit);
        response.setConfidenceScore(confidence);
        response.setSaranPenanganan(saran);
        response.setSuhuUdara(suhuUdara);
        response.setKelembabanUdara(kelembabanUdara);
        response.setPhTanah(phTanah);
        response.setKelembabanTanah(kelembabanTanah);
        response.setCurahHujan(curahHujan);
        response.setIntensitasCahaya(intensitasCahaya);
        response.setUmurTanaman(umurTanaman);
        response.setAdaHama(adaHama);

        return ResponseEntity.ok(new ApiResponse<>(true, "Prediksi berhasil", response));
    }

    /**
     * Ambil riwayat prediksi berdasarkan userId.
     * GET /api/prediction/history/{userId}
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<PredictionRecord>>> getHistory(@PathVariable Long userId) {
        List<PredictionRecord> history = predictionRepository.findByUserIdOrderByTanggalDeteksiDesc(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Berhasil mengambil riwayat", history));
    }

    /**
     * Ambil detail satu record prediksi.
     * GET /api/prediction/detail/{id}
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<PredictionRecord>> getDetail(@PathVariable Long id) {
        Optional<PredictionRecord> recordOpt = predictionRepository.findById(id);
        if (recordOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Data tidak ditemukan", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Berhasil", recordOpt.get()));
    }

    /**
     * Hapus record prediksi.
     * DELETE /api/prediction/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        if (!predictionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Data tidak ditemukan", null));
        }
        predictionRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Data berhasil dihapus", null));
    }
}
