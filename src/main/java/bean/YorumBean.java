package bean;

import entity.Urun;
import entity.Yorum;
import facadeLocal.UrunFacadeLocal;
import facadeLocal.YorumFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Named
@ViewScoped
public class YorumBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private YorumFacadeLocal yorumFacade;

    @EJB
    private UrunFacadeLocal urunFacade;

    @Inject
    private SessionBean sessionBean;

    private Yorum yorum;
    private Long urunId;
    private List<Yorum> yorumlar;
    private String mesaj;

    public void kaydet() {
        if (!sessionBean.isGirisYapmis()) {
            mesaj = "Yorum yazmak için giriş yapmalısınız.";
            return;
        }

        Urun urun = urunFacade.bul(urunId);
        if (urun == null) {
            mesaj = "Ürün bulunamadı.";
            return;
        }

        Yorum y = getYorum();
        if (y.getPuan() == null || y.getPuan() < 1 || y.getPuan() > 5) {
            mesaj = "Puan 1 ile 5 arasında olmalıdır.";
            return;
        }

        y.setKullanici(sessionBean.getKullanici());
        y.setUrun(urun);
        y.setYorumTarihi(LocalDateTime.now());
        yorumFacade.kaydet(y);
        yorum = new Yorum();
        yorumlar = null;
        mesaj = "Yorumunuz eklendi.";
    }

    public void sil(Long yorumId) {
        Yorum y = yorumFacade.bul(yorumId);
        if (y == null || !sessionBean.isGirisYapmis()) {
            mesaj = "Yorum bulunamadı.";
            return;
        }

        boolean kendiYorumu = y.getKullanici() != null && y.getKullanici().getId().equals(sessionBean.getKullanici().getId());
        if (!kendiYorumu && !sessionBean.isAdmin()) {
            mesaj = "Bu yorum silinemez.";
            return;
        }

        yorumFacade.sil(y);
        yorumlar = null;
        mesaj = "Yorum silindi.";
    }

    public Yorum getYorum() {
        if (yorum == null) {
            yorum = new Yorum();
            yorum.setPuan(5);
        }
        return yorum;
    }

    public void setYorum(Yorum yorum) {
        this.yorum = yorum;
    }

    public Long getUrunId() {
        return urunId;
    }

    public void setUrunId(Long urunId) {
        this.urunId = urunId;
    }

    public List<Yorum> getYorumlar() {
        if (urunId != null) {
            yorumlar = yorumFacade.urunYorumlariniGetir(urunId);
        }
        return yorumlar;
    }

    public void setYorumlar(List<Yorum> yorumlar) {
        this.yorumlar = yorumlar;
    }

    public boolean isYorumYok() {
        return getYorumlar() == null || getYorumlar().isEmpty();
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}
