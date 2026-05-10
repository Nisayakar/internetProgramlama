package com.market.bean;

import com.market.entity.Kullanici;
import com.market.enums.Rol;
import com.market.facadeLocal.KullaniciFacadeLocal; // Arayüz eklendi
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("kullaniciBean")
@SessionScoped
public class KullaniciBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private KullaniciFacadeLocal kullaniciFacade; 

    @Inject
    private SessionBean sessionBean;

    private Kullanici yeniKullanici = new Kullanici();
    private Kullanici adminKullanici = new Kullanici();
    private String adminSifre;
    private String girisEposta;
    private String girisSifre;
    private String mesaj;

    public String kayitOl() {
        if (kullaniciFacade.epostaKullaniliyor(yeniKullanici.getEposta(), null)) {
            mesaj = "Bu e-posta adresi zaten kullanılıyor.";
            return null;
        }

        yeniKullanici.setSifre(yeniKullanici.getSifre());
        yeniKullanici.setRol(Rol.MUSTERI);
        kullaniciFacade.kaydet(yeniKullanici);
        yeniKullanici = new Kullanici();
        mesaj = null;
        return "/login.xhtml?faces-redirect=true";
    }

    public String girisYap() {
        Kullanici kullanici = kullaniciFacade.epostaIleBul(girisEposta);

        if (kullanici != null && kullanici.getSifre() != null && kullanici.getSifre().equals(girisSifre)) {
            sessionBean.setKullanici(kullanici);
            mesaj = null;

            if (kullanici.getRol() == Rol.ADMIN) {
                return "/panel/products.xhtml?faces-redirect=true";
            }
            return "/index.xhtml?faces-redirect=true";
        }

        mesaj = "E-posta veya şifre hatalı.";
        return null;
    }

    public void adminKaydet() {
        if (adminKullanici.getRol() == null) {
            adminKullanici.setRol(Rol.MUSTERI);
        }

        if (kullaniciFacade.epostaKullaniliyor(adminKullanici.getEposta(), adminKullanici.getId())) {
            mesaj = "Bu e-posta adresi başka bir kullanıcı tarafından kullanılıyor.";
            return;
        }

        if (adminKullanici.getId() == null) {
            if (adminSifre == null || adminSifre.isBlank()) {
                mesaj = "Yeni kullanıcı için şifre zorunludur.";
                return;
            }
            adminKullanici.setSifre(adminSifre);
            kullaniciFacade.kaydet(adminKullanici);
            mesaj = "Kullanıcı eklendi.";
        } else {
            Kullanici mevcut = kullaniciFacade.bul(adminKullanici.getId());
            if (mevcut == null) {
                mesaj = "Kullanıcı bulunamadı.";
                temizleAdminFormu();
                return;
            }
            mevcut.setAdSoyad(adminKullanici.getAdSoyad());
            mevcut.setEposta(adminKullanici.getEposta());
            mevcut.setRol(adminKullanici.getRol());
            if (adminSifre != null && !adminSifre.isBlank()) {
                mevcut.setSifre(adminSifre);
            }
            kullaniciFacade.guncelle(mevcut);
            mesaj = "Kullanıcı güncellendi.";
        }
        temizleAdminFormu();
    }

    public void duzenle(Kullanici kullanici) {
        adminKullanici = new Kullanici();
        adminKullanici.setId(kullanici.getId());
        adminKullanici.setAdSoyad(kullanici.getAdSoyad());
        adminKullanici.setEposta(kullanici.getEposta());
        adminKullanici.setRol(kullanici.getRol());
        adminSifre = null;
        mesaj = null;
    }

    public void sil(Kullanici kullanici) {
        if (sessionBean.getKullanici() != null && sessionBean.getKullanici().getId().equals(kullanici.getId())) {
            mesaj = "Aktif oturumdaki kullanıcı silinemez.";
            return;
        }
        if (kullaniciFacade.iliskiliKaydiVar(kullanici.getId())) {
            mesaj = "Sipariş veya sepet kaydı olan kullanıcı silinemez.";
            return;
        }
        kullaniciFacade.sil(kullanici);
        temizleAdminFormu();
        mesaj = "Kullanıcı silindi.";
    }

    public void temizleAdminFormu() {
        adminKullanici = new Kullanici();
        adminKullanici.setRol(Rol.MUSTERI);
        adminSifre = null;
    }

    public String cikisYap() { return sessionBean.cikisYap(); }
    public List<Kullanici> getTumKullanicilar() { return kullaniciFacade.tumKullanicilariGetir(); }
    public int getKullaniciSayisi() { return getTumKullanicilar().size(); }
    public Rol[] getRoller() { return Rol.values(); }

    
    public Kullanici getYeniKullanici() { return yeniKullanici; }
    public void setYeniKullanici(Kullanici yeniKullanici) { this.yeniKullanici = yeniKullanici; }
    public Kullanici getAdminKullanici() { return adminKullanici; }
    public void setAdminKullanici(Kullanici adminKullanici) { this.adminKullanici = adminKullanici; }
    public String getAdminSifre() { return adminSifre; }
    public void setAdminSifre(String adminSifre) { this.adminSifre = adminSifre; }
    public String getGirisEposta() { return girisEposta; }
    public void setGirisEposta(String girisEposta) { this.girisEposta = girisEposta; }
    public String getGirisSifre() { return girisSifre; }
    public void setGirisSifre(String girisSifre) { this.girisSifre = girisSifre; }
    public String getMesaj() { return mesaj; }
    public void setMesaj(String mesaj) { this.mesaj = mesaj; }
}
