package com.market.bean;

import com.market.entity.Kategori;
import com.market.facadeLocal.KategoriFacadeLocal; // Arayüz eklendi
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("kategoriBean")
@SessionScoped
public class KategoriBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private KategoriFacadeLocal kategoriFacade; 

    private Kategori yeniKategori = new Kategori();
    private String mesaj;

    public void kaydet() {
        if (yeniKategori.getId() == null) {
            kategoriFacade.kaydet(yeniKategori);
            mesaj = "Kategori eklendi.";
        } else {
            kategoriFacade.guncelle(yeniKategori);
            mesaj = "Kategori güncellendi.";
        }
        yeniKategori = new Kategori();
    }

    public void duzenle(Kategori kategori) {
        this.yeniKategori = kategori;
        mesaj = null;
    }

    public void sil(Kategori kategori) {
        if (kategoriFacade.urunuVar(kategori.getId())) {
            mesaj = "Bu kategoriye ait ürün olduğu için silinemez.";
            return;
        }
        kategoriFacade.sil(kategori);
        yeniKategori = new Kategori();
        mesaj = "Kategori silindi.";
    }

    public void temizle() {
        yeniKategori = new Kategori();
        mesaj = null;
    }

    public List<Kategori> getTumKategoriler() {
        return kategoriFacade.tumKategorileriGetir();
    }

    public int getKategoriSayisi() {
        return getTumKategoriler().size();
    }

    public Kategori getYeniKategori() { return yeniKategori; }
    public void setYeniKategori(Kategori yeniKategori) { this.yeniKategori = yeniKategori; }
    public String getMesaj() { return mesaj; }
    public void setMesaj(String mesaj) { this.mesaj = mesaj; }
}
