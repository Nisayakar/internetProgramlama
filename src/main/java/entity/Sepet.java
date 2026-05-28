package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sepet")
public class Sepet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "kullanici_id", nullable = false, unique = true)
    private Kullanici kullanici;

    @Column(nullable = false)
    private Double toplamTutar = 0.0;

    @OneToMany(mappedBy = "sepet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SepetElemani> elemanlar = new ArrayList<>();

    public Sepet() {
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

    public Double getToplamTutar() {
        return toplamTutar;
    }

    public void setToplamTutar(Double toplamTutar) {
        this.toplamTutar = toplamTutar;
    }

    public List<SepetElemani> getElemanlar() {
        return elemanlar;
    }

    public void setElemanlar(List<SepetElemani> elemanlar) {
        this.elemanlar = elemanlar;
    }
}

