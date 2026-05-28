package facadeLocal;

import entity.Yorum;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface YorumFacadeLocal {
    Yorum kaydet(Yorum yorum);
    void sil(Yorum yorum);
    Yorum bul(Long id);
    List<Yorum> urunYorumlariniGetir(Long urunId);
    List<Yorum> kullaniciYorumlariniGetir(Long kullaniciId);
    boolean urunYorumuVar(Long urunId);
}
