package facadeLocal;

import entity.Urun;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface UrunFacadeLocal {
    Urun kaydet(Urun urun);
    Urun guncelle(Urun urun);
    void sil(Urun urun);
    Urun bul(Long id);
    List<Urun> tumUrunleriGetir();
    List<Urun> kategoriyeGoreUrunleriGetir(Long kategoriId);
    boolean sepetteVar(Long urunId);
}
