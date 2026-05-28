package entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "siparis_detay")
public class SiparisDetay implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "siparis_id", nullable = false)
    private Siparis siparis;

    @Column(nullable = false, length = 140)
    private String urunAdi;

    @Column(nullable = false)
    private Double alinanFiyat;

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

