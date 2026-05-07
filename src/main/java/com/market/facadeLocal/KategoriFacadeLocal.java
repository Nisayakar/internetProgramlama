package com.market.facadeLocal;

import com.market.entity.Kategori;
import jakarta.ejb.Local;
import java.io.Serializable; // Eklendi
import java.util.List;

@Local
public interface KategoriFacadeLocal extends Serializable { // Serializable eklendi
    void kaydet(Kategori kategori);
    void guncelle(Kategori kategori);
    void sil(Kategori kategori);
    Kategori bul(Long id);
    List<Kategori> tumKategorileriGetir();
    boolean urunuVar(Long kategoriId);
}