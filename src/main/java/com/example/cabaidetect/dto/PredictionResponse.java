package com.example.cabaidetect.dto;

/**
 * Response khusus hasil prediksi yang dikembalikan ke client.
 * Berisi data prediksi + saran penanganan + faktor pendukung.
 */
public class PredictionResponse {
    private Long recordId;
    private String jenisCabai;
    private String bagianTanaman;
    private String penyakitTerdeteksi;
    private Double confidenceScore;
    private String saranPenanganan;

    // Faktor Pendukung
    private Double suhuUdara;
    private Double kelembabanUdara;
    private Double phTanah;
    private Double kelembabanTanah;
    private Double curahHujan;
    private Integer intensitasCahaya;
    private Integer umurTanaman;
    private Boolean adaHama;

    // Getters and Setters
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public String getJenisCabai() { return jenisCabai; }
    public void setJenisCabai(String jenisCabai) { this.jenisCabai = jenisCabai; }

    public String getBagianTanaman() { return bagianTanaman; }
    public void setBagianTanaman(String bagianTanaman) { this.bagianTanaman = bagianTanaman; }

    public String getPenyakitTerdeteksi() { return penyakitTerdeteksi; }
    public void setPenyakitTerdeteksi(String penyakitTerdeteksi) { this.penyakitTerdeteksi = penyakitTerdeteksi; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getSaranPenanganan() { return saranPenanganan; }
    public void setSaranPenanganan(String saranPenanganan) { this.saranPenanganan = saranPenanganan; }

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
}
