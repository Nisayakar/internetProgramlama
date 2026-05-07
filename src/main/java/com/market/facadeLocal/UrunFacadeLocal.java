package com.market.facadeLocal;

import com.market.entity.Urun;
import jakarta.ejb.Local;
import java.io.Serializable; // Eklendi
import java.util.List;

@Local
public interface UrunFacadeLocal extends Serializable { 
    void kaydet(Urun urun);
    void guncelle(Urun urun);
    void sil(Urun urun);
    Urun bul(Long id);
    List<Urun> tumUrunleriGetir();
    List<Urun> kategoriyeGoreUrunleriGetir(Long kategoriId);
    boolean sepetteVar(Long urunId);
}