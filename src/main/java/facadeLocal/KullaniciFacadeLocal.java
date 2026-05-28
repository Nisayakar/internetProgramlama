package facadeLocal;

import entity.Kullanici;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface KullaniciFacadeLocal {
    Kullanici kaydet(Kullanici kullanici);
    Kullanici guncelle(Kullanici kullanici);
    void sil(Kullanici kullanici);
    Kullanici bul(Long id);
    List<Kullanici> tumKullanicilariGetir();
    Kullanici epostaIleBul(String eposta);
    boolean epostaKullaniliyor(String eposta, Long mevcutKullaniciId);
    boolean iliskiliKaydiVar(Long kullaniciId);
}
