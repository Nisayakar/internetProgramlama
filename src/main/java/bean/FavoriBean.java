package bean;

import entity.Favori;
import entity.Urun;
import facadeLocal.FavoriFacadeLocal;
import facadeLocal.UrunFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Named
@ViewScoped
public class FavoriBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private FavoriFacadeLocal favoriFacade;

    @EJB
    private UrunFacadeLocal urunFacade;

    @Inject
    private SessionBean sessionBean;

    private List<Favori> favorilerim;
    private String mesaj;

    public void favoriDurumunuDegistir(Long urunId) {
        if (!sessionBean.isGirisYapmis()) {
            mesaj = "Favorilere eklemek için giriş yapmalısınız.";
            return;
        }

        Favori mevcut = favoriFacade.kullaniciUrunFavorisi(sessionBean.getKullanici().getId(), urunId);
        if (mevcut != null) {
            favoriFacade.sil(mevcut);
            mesaj = "Ürün favorilerden çıkarıldı.";
        } else {
            Urun urun = urunFacade.bul(urunId);
            if (urun == null) {
                mesaj = "Ürün bulunamadı.";
                return;
            }

            Favori favori = new Favori();
            favori.setKullanici(sessionBean.getKullanici());
            favori.setUrun(urun);
            favori.setEklenmeTarihi(LocalDateTime.now());
            favoriFacade.kaydet(favori);
            mesaj = "Ürün favorilere eklendi.";
        }

        favorilerim = null;
    }

    public void favoridenCikar(Long favoriId) {
        Favori favori = favoriFacade.bul(favoriId);
        if (favori == null || !sessionBean.isGirisYapmis()
                || favori.getKullanici() == null
                || !favori.getKullanici().getId().equals(sessionBean.getKullanici().getId())) {
            mesaj = "Favori kaydı bulunamadı.";
            return;
        }

        favoriFacade.sil(favori);
        favorilerim = null;
        mesaj = "Ürün favorilerden çıkarıldı.";
    }

    public boolean isFavoride(Long urunId) {
        return sessionBean.isGirisYapmis() && favoriFacade.favorideMi(sessionBean.getKullanici().getId(), urunId);
    }

    public List<Favori> getFavorilerim() {
        if (sessionBean.isGirisYapmis()) {
            favorilerim = favoriFacade.kullaniciFavorileriniGetir(sessionBean.getKullanici().getId());
        }
        return favorilerim;
    }

    public void setFavorilerim(List<Favori> favorilerim) {
        this.favorilerim = favorilerim;
    }

    public boolean isFavoriYok() {
        return getFavorilerim() == null || getFavorilerim().isEmpty();
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}
