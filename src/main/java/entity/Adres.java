package entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "adres")
public class Adres implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String baslik;

    @Column(nullable = false, length = 60)
    private String il;

    @Column(nullable = false, length = 80)
    private String ilce;

    @Column(nullable = false, length = 500)
    private String acikAdres;

    @Column(length = 20)
    private String telefon;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    public Adres() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getIl() {
        return il;
    }

    public void setIl(String il) {
        this.il = il;
    }

    public String getIlce() {
        return ilce;
    }

    public void setIlce(String ilce) {
        this.ilce = ilce;
    }

    public String getAcikAdres() {
        return acikAdres;
    }

    public void setAcikAdres(String acikAdres) {
        this.acikAdres = acikAdres;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Kullanici getKullanici() {
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici) {
        this.kullanici = kullanici;
    }
}
