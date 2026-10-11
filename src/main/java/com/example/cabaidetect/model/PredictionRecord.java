package com.example.cabaidetect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_records")
public class PredictionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String fotoUrl;
    private String jenisCabai; // Cabai Rawit, Cabai Merah Keriting, Cabai Merah Besar
    private String bagianTanaman; // Buah

    // Data Lingkungan & Tanaman
    private Double suhuUdara; // C
    private Double kelembabanUdara; // %
    private Double phTanah;
    private Double kelembabanTanah; // %
    private Double curahHujan; // mm/hari
    private Integer intensitasCahaya; // lux
    private Integer umurTanaman; // hari
    private Boolean adaHama;

    // Hasil Prediksi
    private String penyakitTerdeteksi;
    private Double confidenceScore;

    @Column(columnDefinition = "TEXT")
    private String saranPenanganan;
    
    private LocalDateTime tanggalDeteksi = LocalDateTime.now();

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
 
    public String getJenisCabai() { return jenisCabai; }
    public void setJenisCabai(String jenisCabai) { this.jenisCabai = jenisCabai; }

    public String getBagianTanaman() { return bagianTanaman; }
    public void setBagianTanaman(String bagianTanaman) { this.bagianTanaman = bagianTanaman; }

    public Double getSuhuUdara() { return suhuUdara; }
    public void setSuhuUdara(Double suhuUdara) { this.suhuUdara = suhuUdara; }

    public Double getKelembabanUdara() { return kelembabanUdara; }
    public void setKelembabanUdara(Double kelembabanUdara) { this.kelembabanUdara = kelembabanUdara; }

    public Double getPhTanah() { return phTanah; }
    public void setPhTanah(Double phTanah) { this.phTanah = phTanah; }

    public Double getKelembabanTanah() { return kelembabanTanah; }
    public void setKelembabanTanah(Double kelembabanTanah) { this.kelembabanTanah = kelembabanTanah; }

    public Double getCurahHujan() { return curahHujan; }
    public void setCurahHujan(Double curahHujan) { this.curahHujan = curahHujan; }

    public Integer getIntensitasCahaya() { return intensitasCahaya; }
    public void setIntensitasCahaya(Integer intensitasCahaya) { this.intensitasCahaya = intensitasCahaya; }

    public Integer getUmurTanaman() { return umurTanaman; }
    public void setUmurTanaman(Integer umurTanaman) { this.umurTanaman = umurTanaman; }

    public Boolean getAdaHama() { return adaHama; }
    public void setAdaHama(Boolean adaHama) { this.adaHama = adaHama; }

    public String getPenyakitTerdeteksi() { return penyakitTerdeteksi; }
    public void setPenyakitTerdeteksi(String penyakitTerdeteksi) { this.penyakitTerdeteksi = penyakitTerdeteksi; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getSaranPenanganan() { return saranPenanganan; }
    public void setSaranPenanganan(String saranPenanganan) { this.saranPenanganan = saranPenanganan; }

    public LocalDateTime getTanggalDeteksi() { return tanggalDeteksi; }
    public void setTanggalDeteksi(LocalDateTime tanggalDeteksi) { this.tanggalDeteksi = tanggalDeteksi; }
}
