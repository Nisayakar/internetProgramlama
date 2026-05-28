package facade;

import entity.Kullanici;
import entity.Sepet;
import entity.SepetElemani;
import entity.Siparis;
import entity.SiparisDetay;
import entity.Urun;
import enums.SiparisDurum;
import facadeLocal.SiparisFacadeLocal;
import facadeLocal.UrunFacadeLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class SiparisFacade extends AbstractFacade implements SiparisFacadeLocal {

    @EJB
    private UrunFacadeLocal urunFacade;

    public Siparis kaydet(Siparis siparis) {
        this.entityManager.persist(siparis);
        this.entityManager.flush();
        return siparis;
    }

    public Siparis guncelle(Siparis siparis) {
        this.entityManager.merge(siparis);
        this.entityManager.flush();
        return siparis;
    }

    public void sil(Siparis siparis) {
        Siparis merged = this.entityManager.merge(siparis);
        this.entityManager.remove(merged);
    }

    public Siparis bul(Long id) {
        return this.entityManager.find(Siparis.class, id);
    }

    public void durumGuncelle(Long siparisId, SiparisDurum durum) {
        Siparis siparis = bul(siparisId);
        if (siparis != null) {
            siparis.setDurum(durum);
            this.entityManager.merge(siparis);
            this.entityManager.flush();
        }
    }

    public List<Siparis> tumSiparisleriGetir() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Siparis> cq = cb.createQuery(Siparis.class);
        Root<Siparis> root = cq.from(Siparis.class);
        root.fetch("kullanici", JoinType.INNER);
        root.fetch("detaylar", JoinType.LEFT);
        cq.distinct(true);
        CriteriaQuery<Siparis> all = cq.select(root).orderBy(cb.desc(root.get("siparisTarihi")));
        TypedQuery<Siparis> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public List<Siparis> kullaniciSiparisleriniGetir(Long kullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Siparis> cq = cb.createQuery(Siparis.class);
        Root<Siparis> root = cq.from(Siparis.class);
        root.fetch("detaylar", JoinType.LEFT);
        cq.where(cb.equal(root.get("kullanici").get("id"), kullaniciId));
        cq.distinct(true);
        CriteriaQuery<Siparis> all = cq.select(root).orderBy(cb.desc(root.get("siparisTarihi")));
        TypedQuery<Siparis> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

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
