package com.market.entity;

import com.market.enums.SiparisDurum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "siparis")
public class Siparis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime siparisTarihi;

    @NotNull
    @Column(nullable = false)
    private Double genelToplam;

    @NotNull
    @Convert(converter = SiparisDurumConverter.class)
    @Column(nullable = false, length = 30)
    private SiparisDurum durum;

    @OneToMany(mappedBy = "siparis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SiparisDetay> detaylar = new ArrayList<>();

    public Siparis() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    public LocalDateTime getSiparisTarihi() {
        return siparisTarihi;
    }

    public void setSiparisTarihi(LocalDateTime siparisTarihi) {
        this.siparisTarihi = siparisTarihi;
    }

    public Double getGenelToplam() {
        return genelToplam;
    }

    public void setGenelToplam(Double genelToplam) {
        this.genelToplam = genelToplam;
    }

    public SiparisDurum getDurum() {
        return durum;
    }

    public void setDurum(SiparisDurum durum) {
        this.durum = durum;
    }

    public List<SiparisDetay> getDetaylar() {
        return detaylar;
    }

    public void setDetaylar(List<SiparisDetay> detaylar) {
        this.detaylar = detaylar;
    }
}
