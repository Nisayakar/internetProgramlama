package entity;

import enums.SiparisDurum;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "siparis")
public class Siparis implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @Column(nullable = false)
    private LocalDateTime siparisTarihi;

    @Column(nullable = false)
    private Double genelToplam;

    @Enumerated(EnumType.STRING)
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
