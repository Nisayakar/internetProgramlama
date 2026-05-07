package com.market.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "urun")
public class Urun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 140)
    @Column(nullable = false, length = 140)
    private String ad;

    @Size(max = 500)
    @Column(length = 500)
    private String aciklama;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double guncelFiyat;

    @PositiveOrZero
    private Double eskiFiyat;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stokAdedi;

    @Size(max = 255)
    @Column(length = 255)
    private String gorselUrl;


    @ManyToOne
    @JoinColumn(name = "kategori_id", nullable = false)
    private Kategori kategori;

    public Urun() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getAciklama() {
        return aciklama;
    }

    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }

    public Double getGuncelFiyat() {
        return guncelFiyat;
    }

    public void setGuncelFiyat(Double guncelFiyat) {
        this.guncelFiyat = guncelFiyat;
    }

    public Double getEskiFiyat() {
        return eskiFiyat;
    }

    public void setEskiFiyat(Double eskiFiyat) {
        this.eskiFiyat = eskiFiyat;
    }

    public Integer getStokAdedi() {
        return stokAdedi;
    }

    public void setStokAdedi(Integer stokAdedi) {
        this.stokAdedi = stokAdedi;
    }

    public String getGorselUrl() {
        return gorselUrl;
    }

    public void setGorselUrl(String gorselUrl) {
        this.gorselUrl = gorselUrl;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }
}
