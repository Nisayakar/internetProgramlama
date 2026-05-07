package com.market.bean;

import com.market.entity.Kategori;
import com.market.entity.Kullanici;
import com.market.enums.Rol;
import com.market.entity.Urun;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Singleton
@Startup
public class BootstrapBean {

    @PersistenceContext(unitName = "marketPU")
    private EntityManager em;

    @PostConstruct
    public void init() {
        adminOlustur();
        ornekVerileriOlustur();
    }

    private void adminOlustur() {
        Long adminSayisi = em.createQuery("SELECT COUNT(k) FROM Kullanici k WHERE k.eposta = :eposta", Long.class)
                .setParameter("eposta", "admin@cleanmarket.com")
                .getSingleResult();

        if (adminSayisi > 0) {
            return;
        }

        Kullanici admin = new Kullanici();
        admin.setAdSoyad("CleanMarket Admin");
        admin.setEposta("admin@cleanmarket.com");
        admin.setSifre("Admin123");
        admin.setRol(Rol.ADMIN);
        em.persist(admin);
    }

    private void ornekVerileriOlustur() {
        Long kategoriSayisi = em.createQuery("SELECT COUNT(k) FROM Kategori k", Long.class).getSingleResult();
        if (kategoriSayisi > 0) {
            return;
        }

        Kategori mutfak = kategoriOlustur("Mutfak");
        Kategori banyo = kategoriOlustur("Banyo");
        Kategori yuzeyler = kategoriOlustur("Yüzeyler");
        Kategori ekolojik = kategoriOlustur("Ekolojik");

        urunOlustur("Pure Essence Kit", "Limon otu aromalı, ev ve yaşam alanları için bitkisel ürün seti.", 549.00, 18, mutfak);
        urunOlustur("Bambu Bakım Seti", "Sürdürülebilir mutfak araçları ve yüzey bakım çözümü.", 215.00, 22, mutfak);
        urunOlustur("Zen Air Mist", "Hibrit antiseptik ve ferahlatıcı ortam spreyi.", 1299.00, 9, banyo);
        urunOlustur("Eko Tablet", "Günlük kullanım için çevre dostu çözünür tablet.", 89.00, 5, ekolojik);
        urunOlustur("Kristal Parlatıcı", "Cam ve parlak yüzeylerde iz bırakmayan formül.", 89.00, 34, yuzeyler);
        urunOlustur("Endüstriyel Yüzey Temizleyici", "Güçlü yüzey temizliği için yoğun konsantre.", 345.00, 15, yuzeyler);
    }

    private Kategori kategoriOlustur(String ad) {
        Kategori kategori = new Kategori();
        kategori.setAd(ad);
        em.persist(kategori);
        return kategori;
    }

    private void urunOlustur(String ad, String aciklama, Double fiyat, Integer stok, Kategori kategori) {
        Urun urun = new Urun();
        urun.setAd(ad);
        urun.setAciklama(aciklama);
        urun.setGuncelFiyat(fiyat);
        urun.setStokAdedi(stok);
        urun.setKategori(kategori);
        em.persist(urun);
    }
}
