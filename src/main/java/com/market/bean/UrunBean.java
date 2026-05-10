package com.market.bean;

import com.market.entity.Kategori;
import com.market.entity.Urun;
import com.market.facadeLocal.KategoriFacadeLocal;
import com.market.facadeLocal.UrunFacadeLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Named("urunBean")
@SessionScoped
public class UrunBean implements Serializable {

    private static final long serialVersionUID = 1L; 

    @EJB
    private UrunFacadeLocal urunFacade;

    @EJB
    private KategoriFacadeLocal kategoriFacade;

    private Urun urun;
    private List<Urun> tumUrunler;
    private List<Kategori> kategoriler;
    private Long seciliKategoriId;
    private Long seciliVitrinKategoriId;
    private Long detayUrunId;
    private Urun detayUrun;
    private Part yuklenenGorsel;
    private String mesaj;

    @PostConstruct
    public void init() {
        urun = new Urun();
        tumUrunler = urunFacade.tumUrunleriGetir();
        kategoriler = kategoriFacade.tumKategorileriGetir();
        seciliKategoriId = null;
    }

    public void kaydet() {
        if (seciliKategoriId != null) {
            Kategori secilenKategori = kategoriFacade.bul(seciliKategoriId);
            urun.setKategori(secilenKategori);
        }

        if (yuklenenGorsel != null && yuklenenGorsel.getSize() > 0) {
            try {
                gorseliKaydet();
            } catch (IOException e) {
                mesaj = "Görsel yüklenirken bir hata oluştu.";
                return;
            }
        }

        if (urun.getId() == null) {
            urunFacade.kaydet(urun);
            mesaj = "Ürün eklendi.";
        } else {
            urunFacade.guncelle(urun);
            mesaj = "Ürün güncellendi.";
        }
        init();
    }

    private void gorseliKaydet() throws IOException {
        String dosyaAdi = yuklenenGorsel.getSubmittedFileName();
        String uzanti = dosyaUzantisi(dosyaAdi);
        String benzersizDosyaAdi = UUID.randomUUID() + uzanti;
        Path uploadsDizini = uploadsDizininiBul();

        Files.createDirectories(uploadsDizini);
        Path hedefDosya = uploadsDizini.resolve(benzersizDosyaAdi);

        try (InputStream input = yuklenenGorsel.getInputStream()) {
            Files.copy(input, hedefDosya);
        }

        urun.setGorselUrl("/resources/uploads/" + benzersizDosyaAdi);
    }

    private Path uploadsDizininiBul() {
        String gercekYol = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRealPath("/resources/uploads");

        if (gercekYol != null) {
            return Paths.get(gercekYol);
        }

        return Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "resources", "uploads");
    }

    private String dosyaUzantisi(String dosyaAdi) {
        if (dosyaAdi == null || !dosyaAdi.contains(".")) {
            return ".jpg";
        }

        String uzanti = dosyaAdi.substring(dosyaAdi.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        if (uzanti.matches("\\.(jpg|jpeg|png|webp|gif)")) {
            return uzanti;
        }

        return ".jpg";
    }

    public void sil(Urun u) {
        if (urunFacade.sepetteVar(u.getId())) {
            mesaj = "Sepette bulunan ürün silinemez.";
            return;
        }
        urunFacade.sil(u);
        mesaj = "Ürün silindi.";
        init();
    }

    public void duzenle(Urun u) {
        this.urun = u;
        if (u.getKategori() != null) {
            this.seciliKategoriId = u.getKategori().getId();
        }
    }

    public void temizle() {
        urun = new Urun();
        seciliKategoriId = null;
        yuklenenGorsel = null;
        mesaj = null;
    }

    public void kategoriSec(Long kategoriId) {
        seciliVitrinKategoriId = kategoriId;
    }

    public void tumKategorileriGoster() {
        seciliVitrinKategoriId = null;
    }

    public void detayUrunYukle() {
        if (detayUrunId == null) {
            detayUrun = null;
            return;
        }

        detayUrun = urunFacade.bul(detayUrunId);
    }

    public List<Urun> getVitrinUrunleri() {
        if (seciliVitrinKategoriId == null) {
            return getTumUrunler();
        }
        return urunFacade.kategoriyeGoreUrunleriGetir(seciliVitrinKategoriId);
    }

    public int getStokUyarisiSayisi() {
        int sayi = 0;
        for (Urun u : getTumUrunler()) {
            Integer stok = u.getStokAdedi();
            if (stok != null && stok < 10) {
                sayi++;
            }
        }
        return sayi;
    }

    public double getToplamEnvanterDegeri() {
        double toplam = 0.0;
        for (Urun u : getTumUrunler()) {
            if (u.getGuncelFiyat() != null && u.getStokAdedi() != null) {
                toplam += u.getGuncelFiyat() * u.getStokAdedi();
            }
        }
        return toplam;
    }

    public int getUrunSayisi() {
        return getTumUrunler().size();
    }

    public int getKategoriSayisi() {
        return getKategoriler().size();
    }

    public boolean isKategoriYok() {
        return kategoriler == null || kategoriler.isEmpty();
    }

    public boolean isUrunYok() {
        return getTumUrunler().isEmpty();
    }


    public Urun getUrun() { return urun; }
    public void setUrun(Urun urun) { this.urun = urun; }
    public List<Urun> getTumUrunler() { return urunFacade.tumUrunleriGetir(); }
    public void setTumUrunler(List<Urun> tumUrunler) { this.tumUrunler = tumUrunler; }
    public List<Kategori> getKategoriler() { return kategoriFacade.tumKategorileriGetir(); }
    public void setKategoriler(List<Kategori> kategoriler) { this.kategoriler = kategoriler; }
    public Long getSeciliKategoriId() { return seciliKategoriId; }
    public void setSeciliKategoriId(Long seciliKategoriId) { this.seciliKategoriId = seciliKategoriId; }
    public Long getSeciliVitrinKategoriId() { return seciliVitrinKategoriId; }
    public void setSeciliVitrinKategoriId(Long seciliVitrinKategoriId) { this.seciliVitrinKategoriId = seciliVitrinKategoriId; }
    public Long getDetayUrunId() { return detayUrunId; }
    public void setDetayUrunId(Long detayUrunId) { this.detayUrunId = detayUrunId; }
    public Urun getDetayUrun() { return detayUrun; }
    public void setDetayUrun(Urun detayUrun) { this.detayUrun = detayUrun; }
    public Part getYuklenenGorsel() { return yuklenenGorsel; }
    public void setYuklenenGorsel(Part yuklenenGorsel) { this.yuklenenGorsel = yuklenenGorsel; }
    public String getMesaj() { return mesaj; }
    public void setMesaj(String mesaj) { this.mesaj = mesaj; }
}
