package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "favori")
public class Favori implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @ManyToOne
    @JoinColumn(name = "urun_id", nullable = false)
    private Urun urun;

    @Column(nullable = false)
    private LocalDateTime eklenmeTarihi;

    public Favori() {
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

    public Urun getUrun() {
        return urun;
    }

    public void setUrun(Urun urun) {
        this.urun = urun;
    }

    public LocalDateTime getEklenmeTarihi() {
        return eklenmeTarihi;
    }

    public void setEklenmeTarihi(LocalDateTime eklenmeTarihi) {
        this.eklenmeTarihi = eklenmeTarihi;
    }
}
