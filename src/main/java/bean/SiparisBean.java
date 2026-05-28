package bean;

import entity.Siparis;
import enums.SiparisDurum;
import facadeLocal.SiparisFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class SiparisBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String ADMIN_ORDERS_REDIRECT = "/panel/orders.xhtml?faces-redirect=true";

    @Inject
    private SessionBean sessionBean;

    @Inject
    private SepetBean sepetBean;

    @EJB
    private SiparisFacadeLocal siparisFacade;

    private List<Siparis> gecmisSiparislerim;
    private List<Siparis> tumSiparisler;
    private String mesaj;

    public String siparisiTamamla() {
        if (!sessionBean.isGirisYapmis()) {
            return "/login.xhtml?faces-redirect=true";
        }

        if (sepetBean.isSepetBos()) {
            mesaj = "Sepetiniz boş.";
            return null;
        }

        try {
            siparisFacade.sepettenSiparisOlustur(sessionBean.getKullanici(), sepetBean.getSepet());
        } catch (RuntimeException e) {
            mesaj = e.getMessage();
            return null;
        }

        sepetBean.sepetiBosalt();
        gecmisSiparislerim = null;
        tumSiparisler = null;
        mesaj = "Siparişiniz alındı.";
        return null;
    }

    public String onayla(Long siparisId) {
        return durumGuncelle(siparisId, SiparisDurum.ONAYLANDI, "Sipariş onaylandı.");
    }

    public String hazirlaniyorYap(Long siparisId) {
        return durumGuncelle(siparisId, SiparisDurum.HAZIRLANIYOR, "Sipariş hazırlanıyor olarak işaretlendi.");
    }

    public String teslimEt(Long siparisId) {
        return durumGuncelle(siparisId, SiparisDurum.TESLIM_EDILDI, "Sipariş teslim edildi.");
    }

    public String iptalEt(Long siparisId) {
        return durumGuncelle(siparisId, SiparisDurum.IPTAL_EDILDI, "Sipariş iptal edildi.");
    }

    public String kullaniciSiparisIptalEt(Long siparisId) {
        Siparis siparis = siparisFacade.bul(siparisId);
        if (siparis == null || !sessionBean.isGirisYapmis()
                || siparis.getKullanici() == null
                || !siparis.getKullanici().getId().equals(sessionBean.getKullanici().getId())) {
            mesaj = "Sipariş bulunamadı.";
            return null;
        }

        if (!isKullaniciIptalEdilebilir(siparis)) {
            mesaj = "Bu sipariş artık iptal edilemez.";
            return null;
        }

        siparisFacade.durumGuncelle(siparisId, SiparisDurum.IPTAL_EDILDI);
        gecmisSiparislerim = null;
        mesaj = "Sipariş iptal edildi.";
        return null;
    }

    public boolean isKullaniciIptalEdilebilir(Siparis siparis) {
        return siparis != null && siparis.getDurum() == SiparisDurum.ONAY_BEKLIYOR;
    }

    public boolean isAdminDurumGuncellenebilir(Siparis siparis) {
        return siparis != null && siparis.getDurum() != SiparisDurum.IPTAL_EDILDI;
    }

    public String durumGuncelle(Long siparisId, SiparisDurum durum) {
        return durumGuncelle(siparisId, durum, "Sipariş durumu güncellendi.");
    }

    private String durumGuncelle(Long siparisId, SiparisDurum durum, String basariMesaji) {
        siparisFacade.durumGuncelle(siparisId, durum);
        tumSiparisler = null;
        gecmisSiparislerim = null;
        mesaj = basariMesaji;
        return ADMIN_ORDERS_REDIRECT;
    }

    public String sil(Long siparisId) {
        Siparis siparis = siparisFacade.bul(siparisId);
        if (siparis != null) {
            siparisFacade.sil(siparis);
            tumSiparisler = null;
            gecmisSiparislerim = null;
            mesaj = "Sipariş silindi.";
        }
        return ADMIN_ORDERS_REDIRECT;
    }

    public List<Siparis> getGecmisSiparislerim() {
        if (sessionBean.isGirisYapmis() && gecmisSiparislerim == null) {
            gecmisSiparislerim = siparisFacade.kullaniciSiparisleriniGetir(sessionBean.getKullanici().getId());
        }
        return gecmisSiparislerim;
    }

    public List<Siparis> getTumSiparisler() {
        if (tumSiparisler == null) {
            tumSiparisler = siparisFacade.tumSiparisleriGetir();
        }
        return tumSiparisler;
    }

    public int getOnayBekleyenSayisi() {
        int sayi = 0;
        for (Siparis siparis : getTumSiparisler()) {
            if (siparis.getDurum() == SiparisDurum.ONAY_BEKLIYOR || siparis.getDurum() == SiparisDurum.HAZIRLANIYOR) {
                sayi++;
            }
        }
        return sayi;
    }

    public int getSiparisSayisi() {
        return getTumSiparisler().size();
    }

    public int getTeslimEdilenSayisi() {
        int sayi = 0;
        for (Siparis siparis : getTumSiparisler()) {
            if (siparis.getDurum() == SiparisDurum.TESLIM_EDILDI) {
                sayi++;
            }
        }
        return sayi;
    }

    public double getToplamSiparisTutari() {
        double toplam = 0.0;
        for (Siparis siparis : getTumSiparisler()) {
            if (siparis.getGenelToplam() != null) {
                toplam += siparis.getGenelToplam();
            }
        }
        return toplam;
    }

    public boolean isSiparisYok() {
        return getTumSiparisler().isEmpty();
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
}

