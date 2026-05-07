package com.market.facadeLocal;

import com.market.entity.Kullanici;
import com.market.entity.Sepet;
import com.market.entity.Siparis;
import com.market.enums.SiparisDurum;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface SiparisFacadeLocal {
    void kaydet(Siparis siparis);
    void guncelle(Siparis siparis);
    void sil(Siparis siparis);
    Siparis bul(Long id);
    void durumGuncelle(Long siparisId, SiparisDurum durum);
    List<Siparis> tumSiparisleriGetir();
    List<Siparis> kullaniciSiparisleriniGetir(Long kullaniciId);
    Siparis sepettenSiparisOlustur(Kullanici kullanici, Sepet sepet);
}
