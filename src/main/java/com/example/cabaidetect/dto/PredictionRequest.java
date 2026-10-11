package com.example.cabaidetect.dto;

public class PredictionRequest {
    private Long userId;
    private String fotoUrl;
    private String jenisCabai;
    private String bagianTanaman;
    private Double suhuUdara;
    private Double kelembabanUdara;
    private Double phTanah;
    private Double kelembabanTanah;
    private Double curahHujan;
    private Integer intensitasCahaya;
    private Integer umurTanaman;
    private Boolean adaHama;

    // Fields to receive the simulated or actual ML result
    private String penyakitTerdeteksi;
    private Double confidenceScore;

    // Getters and setters (omitted for brevity, assume Lombok or generate manually)
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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
}
