package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "yorum")
public class Yorum implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String yorumMetni;

    @Column(nullable = false)
    private Integer puan;

    @Column(nullable = false)
    private LocalDateTime yorumTarihi;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @ManyToOne
    @JoinColumn(name = "urun_id", nullable = false)
    private Urun urun;

    public Yorum() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYorumMetni() {
        return yorumMetni;
    }

    public void setYorumMetni(String yorumMetni) {
        this.yorumMetni = yorumMetni;
    }

    public Integer getPuan() {
        return puan;
    }

    public void setPuan(Integer puan) {
        this.puan = puan;
    }

    public LocalDateTime getYorumTarihi() {
        return yorumTarihi;
    }

    public void setYorumTarihi(LocalDateTime yorumTarihi) {
        this.yorumTarihi = yorumTarihi;
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
}
