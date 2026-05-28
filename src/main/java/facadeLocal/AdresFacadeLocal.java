package facadeLocal;

import entity.Adres;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AdresFacadeLocal {
    Adres kaydet(Adres adres);
    Adres guncelle(Adres adres);
    void sil(Adres adres);
    Adres bul(Long id);
    List<Adres> kullaniciAdresleriniGetir(Long kullaniciId);
    boolean kullaniciAdresiMi(Long adresId, Long kullaniciId);
}
