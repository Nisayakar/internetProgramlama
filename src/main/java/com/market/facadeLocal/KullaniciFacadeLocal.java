package com.market.facadeLocal;

import com.market.entity.Kullanici;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface KullaniciFacadeLocal {
    void kaydet(Kullanici kullanici);
    void guncelle(Kullanici kullanici);
    void sil(Kullanici kullanici);
    Kullanici bul(Long id);
    List<Kullanici> tumKullanicilariGetir();
    Kullanici epostaIleBul(String eposta);
    boolean epostaKullaniliyor(String eposta, Long mevcutKullaniciId);
    boolean iliskiliKaydiVar(Long kullaniciId);
}