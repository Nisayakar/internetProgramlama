package entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "urun")
public class Urun implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String ad;

    @Column(length = 500)
    private String aciklama;

    @Column(nullable = false)
    private Double guncelFiyat;

    private Double eskiFiyat;

    @Column(nullable = false)
    private Integer stokAdedi;

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

