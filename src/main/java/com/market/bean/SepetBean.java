package com.market.bean;

import com.market.entity.Sepet;
import com.market.entity.SepetElemani;
import com.market.entity.Urun;
import com.market.facadeLocal.SepetFacadeLocal; // Arayüz eklendi
import com.market.facadeLocal.UrunFacadeLocal;  // Arayüz eklendi
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;

@Named("sepetBean")
@SessionScoped
public class SepetBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UrunFacadeLocal urunFacade; 

    @Inject
    private SepetFacadeLocal sepetFacade; 

    @Inject
    private SessionBean sessionBean;

    private Sepet sepet;
    private String mesaj;

    @PostConstruct
    public void init() {
        sepet = yeniGeciciSepet();
    }

    public void sepeteEkle(Long urunId) {
        if (!sessionBean.isGirisYapmis()) {
            mesaj = "Sepete eklemek için giriş yapmalısınız.";
            return;
        }

        Sepet aktifSepet = aktifSepetiGetir();
        Urun urun = urunFacade.bul(urunId);
        if (urun == null || urun.getStokAdedi() == null || urun.getStokAdedi() <= 0) {
            mesaj = "Bu ürün stokta yok.";
            return;
        }

        for (SepetElemani eleman : aktifSepet.getElemanlar()) {
            if (eleman.getUrun().getId().equals(urun.getId())) {
                if (eleman.getMiktar() < urun.getStokAdedi()) {
                    eleman.setMiktar(eleman.getMiktar() + 1);
                    eleman.setAraToplam(eleman.getMiktar() * urun.getGuncelFiyat());
                    hesapla();
                    sepet = sepetFacade.guncelle(aktifSepet);
                    mesaj = "Sepet güncellendi.";
                } else {
                    mesaj = "Stok sınırına ulaşıldı.";
                }
                return;
            }
        }

        SepetElemani yeniEleman = new SepetElemani();
        yeniEleman.setUrun(urun);
        yeniEleman.setSepet(aktifSepet);
        yeniEleman.setMiktar(1);
        yeniEleman.setAraToplam(urun.getGuncelFiyat());

        aktifSepet.getElemanlar().add(yeniEleman);
        hesapla();
        sepet = sepetFacade.guncelle(aktifSepet);
        mesaj = "Ürün sepete eklendi.";
    }

    public void miktarAzalt(SepetElemani eleman) {
        if (eleman == null) return;
        aktifSepetiGetir();
        if (eleman.getMiktar() <= 1) {
            cikar(eleman);
            return;
        }
        eleman.setMiktar(eleman.getMiktar() - 1);
        eleman.setAraToplam(eleman.getMiktar() * eleman.getUrun().getGuncelFiyat());
        hesapla();
        sepet = sepetFacade.guncelle(sepet);
        mesaj = "Sepet güncellendi.";
    }

    public void cikar(SepetElemani eleman) {
        aktifSepetiGetir();
        sepet.getElemanlar().remove(eleman);
        hesapla();
        sepet = sepetFacade.guncelle(sepet);
        mesaj = "Ürün sepetten çıkarıldı.";
    }

    private void hesapla() {
        double toplam = 0.0;
        if (sepet != null && sepet.getElemanlar() != null) {
            for (SepetElemani e : sepet.getElemanlar()) {
                toplam += e.getAraToplam();
            }
            sepet.setToplamTutar(toplam);
        }
    }

    public void sepetiBosalt() {
        aktifSepetiGetir();
        sepet.getElemanlar().clear();
        sepet.setToplamTutar(0.0);
        if (sepet.getId() != null) {
            sepet = sepetFacade.guncelle(sepet);
        }
        mesaj = null;
    }

    public boolean isSepetBos() {
        return getSepet().getElemanlar().isEmpty();
    }

    public int getSepetUrunSayisi() {
        int sayi = 0;
        for (SepetElemani e : getSepet().getElemanlar()) {
            sayi += e.getMiktar();
        }
        return sayi;
    }

    public Sepet getSepet() {
        return aktifSepetiGetir();
    }

    public void setSepet(Sepet sepet) {
        this.sepet = sepet;
    }

    public String getMesaj() { return mesaj; }
    public void setMesaj(String mesaj) { this.mesaj = mesaj; }

    private Sepet aktifSepetiGetir() {
        if (!sessionBean.isGirisYapmis()) {
            if (sepet == null) sepet = yeniGeciciSepet();
            return sepet;
        }

        Long kullaniciId = sessionBean.getKullanici().getId();
        if (sepet != null && sepet.getKullanici() != null && kullaniciId.equals(sepet.getKullanici().getId())) {
            return sepet;
        }

        sepet = sepetFacade.kullaniciSepetiniGetir(kullaniciId);
        if (sepet == null) {
            sepet = sepetFacade.kullaniciIcinSepetOlustur(kullaniciId);
        }
        if (sepet.getElemanlar() == null) {
            sepet.setElemanlar(new ArrayList<>());
        }
        return sepet;
    }

    private Sepet yeniGeciciSepet() {
        Sepet geciciSepet = new Sepet();
        geciciSepet.setElemanlar(new ArrayList<>());
        geciciSepet.setToplamTutar(0.0);
        return geciciSepet;
    }
}
