package facadeLocal;

import entity.Kullanici;
import entity.Sepet;
import entity.Siparis;
import enums.SiparisDurum;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface SiparisFacadeLocal {
    Siparis kaydet(Siparis siparis);
    Siparis guncelle(Siparis siparis);
    void sil(Siparis siparis);
    Siparis bul(Long id);
    void durumGuncelle(Long siparisId, SiparisDurum durum);
    List<Siparis> tumSiparisleriGetir();
    List<Siparis> kullaniciSiparisleriniGetir(Long kullaniciId);
    Siparis sepettenSiparisOlustur(Kullanici kullanici, Sepet sepet);
}

