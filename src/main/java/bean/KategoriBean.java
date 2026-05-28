package bean;

import entity.Kategori;
import facadeLocal.KategoriFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class KategoriBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private KategoriFacadeLocal kategoriFacade;

    private Kategori yeniKategori;
    private List<Kategori> tumKategoriler;
    private String mesaj;

    public void kaydet() {
        Kategori kategori = getYeniKategori();

        if (kategori.getId() == null) {
            kategoriFacade.kaydet(kategori);
            mesaj = "Kategori eklendi.";
        } else {
            kategoriFacade.guncelle(kategori);
            mesaj = "Kategori güncellendi.";
        }
        yeniKategori = new Kategori();
    }

    public void duzenle(Kategori kategori) {
        this.yeniKategori = kategori;
        mesaj = null;
    }

    public void sil(Kategori kategori) {
        if (kategoriFacade.urunuVar(kategori.getId())) {
            mesaj = "Bu kategoriye ait ürün olduğu için silinemez.";
            return;
        }
        kategoriFacade.sil(kategori);
        yeniKategori = new Kategori();
        mesaj = "Kategori silindi.";
    }

    public void temizle() {
        yeniKategori = new Kategori();
        mesaj = null;
    }

    public List<Kategori> getTumKategoriler() {
        tumKategoriler = kategoriFacade.tumKategorileriGetir();
        return tumKategoriler;
    }

    public int getKategoriSayisi() {
        return getTumKategoriler().size();
    }

    public void setTumKategoriler(List<Kategori> tumKategoriler) {
        this.tumKategoriler = tumKategoriler;
    }

    public Kategori getYeniKategori() {
        if (yeniKategori == null) {
            yeniKategori = new Kategori();
        }
        return yeniKategori;
    }

    public void setYeniKategori(Kategori yeniKategori) {
        this.yeniKategori = yeniKategori;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}

