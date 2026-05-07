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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "sepet_elemani")
public class SepetElemani {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sepet_id", nullable = false)
    private Sepet sepet;

    @ManyToOne
    @JoinColumn(name = "urun_id", nullable = false)
    private Urun urun;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer miktar;

    @NotNull
    @PositiveOrZero
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
