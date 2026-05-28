package bean;

import entity.Adres;
import facadeLocal.AdresFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class AdresBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AdresFacadeLocal adresFacade;

    @Inject
    private SessionBean sessionBean;

    private Adres adres;
    private List<Adres> adreslerim;
    private String mesaj;

    public void kaydet() {
        if (!sessionBean.isGirisYapmis()) {
            mesaj = "Adres eklemek için giriş yapmalısınız.";
            return;
        }

        Adres a = getAdres();
        a.setKullanici(sessionBean.getKullanici());

        if (a.getId() == null) {
            adresFacade.kaydet(a);
            mesaj = "Adres eklendi.";
        } else {
            adresFacade.guncelle(a);
            mesaj = "Adres güncellendi.";
        }

        temizle();
        adreslerim = null;
    }

    public void duzenle(Adres a) {
        adres = new Adres();
        adres.setId(a.getId());
        adres.setBaslik(a.getBaslik());
        adres.setIl(a.getIl());
        adres.setIlce(a.getIlce());
        adres.setAcikAdres(a.getAcikAdres());
        adres.setTelefon(a.getTelefon());
        adres.setKullanici(a.getKullanici());
        mesaj = null;
    }

    public void sil(Adres a) {
        if (!sessionBean.isGirisYapmis() || !adresFacade.kullaniciAdresiMi(a.getId(), sessionBean.getKullanici().getId())) {
            mesaj = "Adres bulunamadı.";
            return;
        }

        adresFacade.sil(a);
        adreslerim = null;
        mesaj = "Adres silindi.";
    }

    public void temizle() {
        adres = new Adres();
    }

    public Adres getAdres() {
        if (adres == null) {
            adres = new Adres();
        }
        return adres;
    }

    public void setAdres(Adres adres) {
        this.adres = adres;
    }

    public List<Adres> getAdreslerim() {
        if (sessionBean.isGirisYapmis()) {
            adreslerim = adresFacade.kullaniciAdresleriniGetir(sessionBean.getKullanici().getId());
        }
        return adreslerim;
    }

    public void setAdreslerim(List<Adres> adreslerim) {
        this.adreslerim = adreslerim;
    }

    public boolean isAdresYok() {
        return getAdreslerim() == null || getAdreslerim().isEmpty();
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}
