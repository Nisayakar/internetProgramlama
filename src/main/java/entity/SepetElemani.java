package entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sepet_elemani")
public class SepetElemani implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sepet_id", nullable = false)
    private Sepet sepet;

    @ManyToOne
    @JoinColumn(name = "urun_id", nullable = false)
    private Urun urun;

    @Column(nullable = false)
    private Integer miktar;

    @Column(nullable = false)
    private Double araToplam;

    public SepetElemani() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sepet getSepet() {
        return sepet;
    }

    public void setSepet(Sepet sepet) {
        this.sepet = sepet;
    }

    public Urun getUrun() {
        return urun;
    }

    public void setUrun(Urun urun) {
        this.urun = urun;
    }

    public Integer getMiktar() {
        return miktar;
    }

    public void setMiktar(Integer miktar) {
        this.miktar = miktar;
    }

    public Double getAraToplam() {
        return araToplam;
    }

    public void setAraToplam(Double araToplam) {
        this.araToplam = araToplam;
    }
}

