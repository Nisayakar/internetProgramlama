package facadeLocal;

import entity.Favori;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface FavoriFacadeLocal {
    Favori kaydet(Favori favori);
    void sil(Favori favori);
    Favori bul(Long id);
    Favori kullaniciUrunFavorisi(Long kullaniciId, Long urunId);
    List<Favori> kullaniciFavorileriniGetir(Long kullaniciId);
    boolean favorideMi(Long kullaniciId, Long urunId);
    boolean urunFavorideVar(Long urunId);
}
