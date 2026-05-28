package facadeLocal;

import entity.Kategori;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface KategoriFacadeLocal {
    Kategori kaydet(Kategori kategori);
    Kategori guncelle(Kategori kategori);
    void sil(Kategori kategori);
    Kategori bul(Long id);
    List<Kategori> tumKategorileriGetir();
    boolean urunuVar(Long kategoriId);
}
