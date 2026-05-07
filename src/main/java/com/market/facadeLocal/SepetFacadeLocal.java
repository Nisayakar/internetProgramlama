package com.market.facadeLocal;

import com.market.entity.Sepet;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface SepetFacadeLocal {
    Sepet kaydet(Sepet sepet);
    Sepet guncelle(Sepet sepet);
    void sil(Sepet sepet);
    Sepet bul(Long id);
    List<Sepet> tumSepetleriGetir();
    Sepet kullaniciSepetiniGetir(Long kullaniciId);
    Sepet kullaniciIcinSepetOlustur(Long kullaniciId);
}