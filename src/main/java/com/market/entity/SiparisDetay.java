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

@Entity
@Table(name = "siparis_detay")
public class SiparisDetay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "siparis_id", nullable = false)
    private Siparis siparis;

    @NotBlank
    @Column(nullable = false, length = 140)
    private String urunAdi;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double alinanFiyat;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer miktar;

    public SiparisDetay() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Siparis getSiparis() {
        return siparis;
    }

    public void setSiparis(Siparis siparis) {
        this.siparis = siparis;
    }

    public String getUrunAdi() {
        return urunAdi;
    }

    public void setUrunAdi(String urunAdi) {
        this.urunAdi = urunAdi;
    }

    public Double getAlinanFiyat() {
        return alinanFiyat;
    }

    public void setAlinanFiyat(Double alinanFiyat) {
        this.alinanFiyat = alinanFiyat;
    }

    public Integer getMiktar() {
        return miktar;
    }

    public void setMiktar(Integer miktar) {
        this.miktar = miktar;
    }
}
