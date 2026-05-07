package com.market.facade;

import com.market.entity.Kullanici;
import com.market.entity.Sepet;
import com.market.entity.SepetElemani;
import com.market.entity.Siparis;
import com.market.entity.SiparisDetay;
import com.market.entity.Urun;
import com.market.enums.SiparisDurum;
import com.market.facadeLocal.SiparisFacadeLocal;
import com.market.facadeLocal.UrunFacadeLocal; // Arayüzü import et
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class SiparisFacade implements SiparisFacadeLocal {

    @PersistenceContext(unitName = "marketPU")
    private EntityManager em;


    @Inject
    private UrunFacadeLocal urunFacade;

    @Override
    public void kaydet(Siparis siparis) {
        em.persist(siparis);
    }

    @Override
    public void guncelle(Siparis siparis) {
        em.merge(siparis);
    }

    @Override
    public void sil(Siparis siparis) {
        em.remove(em.merge(siparis));
    }

    @Override
    public Siparis bul(Long id) {
        return em.find(Siparis.class, id);
    }

    @Override
    public void durumGuncelle(Long siparisId, SiparisDurum durum) {
        Siparis siparis = bul(siparisId);
        if (siparis != null) {
            siparis.setDurum(durum);
            em.merge(siparis);
        }
    }

    @Override
    public List<Siparis> tumSiparisleriGetir() {
        return em.createQuery("SELECT DISTINCT s FROM Siparis s JOIN FETCH s.kullanici LEFT JOIN FETCH s.detaylar ORDER BY s.siparisTarihi DESC", Siparis.class)
                .getResultList();
    }

    @Override
    public List<Siparis> kullaniciSiparisleriniGetir(Long kullaniciId) {
        return em.createQuery("SELECT DISTINCT s FROM Siparis s LEFT JOIN FETCH s.detaylar WHERE s.kullanici.id = :kId ORDER BY s.siparisTarihi DESC", Siparis.class)
                .setParameter("kId", kullaniciId)
                .getResultList();
    }

    @Override
    public Siparis sepettenSiparisOlustur(Kullanici kullanici, Sepet sepet) {
        if (kullanici == null) {
            throw new RuntimeException("Sipariş için kullanıcı zorunludur.");
        }
        if (sepet == null || sepet.getElemanlar() == null || sepet.getElemanlar().isEmpty()) {
            throw new RuntimeException("Sepetiniz boş.");
        }

        Siparis yeniSiparis = new Siparis();
        yeniSiparis.setKullanici(kullanici);
        yeniSiparis.setSiparisTarihi(LocalDateTime.now());
        yeniSiparis.setGenelToplam(sepet.getToplamTutar());
        yeniSiparis.setDurum(SiparisDurum.ONAY_BEKLIYOR);

        for (SepetElemani se : sepet.getElemanlar()) {
            Urun gercekUrun = urunFacade.bul(se.getUrun().getId());
            if (gercekUrun == null || gercekUrun.getStokAdedi() == null || gercekUrun.getStokAdedi() < se.getMiktar()) {
                throw new RuntimeException(se.getUrun().getAd() + " için yeterli stok yok.");
            }

            SiparisDetay detay = new SiparisDetay();
            detay.setSiparis(yeniSiparis);
            detay.setUrunAdi(se.getUrun().getAd());
            detay.setAlinanFiyat(se.getUrun().getGuncelFiyat());
            detay.setMiktar(se.getMiktar());
            yeniSiparis.getDetaylar().add(detay);

            gercekUrun.setStokAdedi(gercekUrun.getStokAdedi() - se.getMiktar());
            urunFacade.guncelle(gercekUrun);
        }

        this.kaydet(yeniSiparis);
        return yeniSiparis;
    }
}
