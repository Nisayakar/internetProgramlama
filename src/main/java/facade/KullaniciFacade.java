package facade;

import entity.Kullanici;
import entity.Sepet;
import entity.Siparis;
import facadeLocal.KullaniciFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class KullaniciFacade extends AbstractFacade implements KullaniciFacadeLocal {

    public Kullanici kaydet(Kullanici kullanici) {
        this.entityManager.persist(kullanici);
        this.entityManager.flush();
        return kullanici;
    }

    public Kullanici guncelle(Kullanici kullanici) {
        this.entityManager.merge(kullanici);
        this.entityManager.flush();
        return kullanici;
    }

    public void sil(Kullanici kullanici) {
        Kullanici merged = this.entityManager.merge(kullanici);
        this.entityManager.remove(merged);
    }

    public Kullanici bul(Long id) {
        return this.entityManager.find(Kullanici.class, id);
    }

    public List<Kullanici> tumKullanicilariGetir() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Kullanici> cq = cb.createQuery(Kullanici.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        CriteriaQuery<Kullanici> all = cq.select(root).orderBy(cb.asc(root.get("id")));
        TypedQuery<Kullanici> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public Kullanici epostaIleBul(String eposta) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Kullanici> cq = cb.createQuery(Kullanici.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.where(cb.equal(root.get("eposta"), eposta));
        CriteriaQuery<Kullanici> all = cq.select(root);
        TypedQuery<Kullanici> q = this.entityManager.createQuery(all);
        List<Kullanici> found = q.getResultList();

        if (found.isEmpty()) {
            return null;
        } else {
            return found.get(0);
        }
    }

    public boolean epostaKullaniliyor(String eposta, Long mevcutKullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Kullanici> root = cq.from(Kullanici.class);
        cq.select(cb.count(root));

        if (mevcutKullaniciId == null) {
            cq.where(cb.equal(root.get("eposta"), eposta));
        } else {
            cq.where(
                    cb.equal(root.get("eposta"), eposta),
                    cb.notEqual(root.get("id"), mevcutKullaniciId)
            );
        }

        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long sayi = q.getSingleResult();
        return sayi > 0;
    }

    public boolean iliskiliKaydiVar(Long kullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> siparisCq = cb.createQuery(Long.class);
        Root<Siparis> siparisRoot = siparisCq.from(Siparis.class);
        siparisCq.select(cb.count(siparisRoot));
        siparisCq.where(cb.equal(siparisRoot.get("kullanici").get("id"), kullaniciId));
        Long siparisSayisi = this.entityManager.createQuery(siparisCq).getSingleResult();

        CriteriaQuery<Long> sepetCq = cb.createQuery(Long.class);
        Root<Sepet> sepetRoot = sepetCq.from(Sepet.class);
        sepetCq.select(cb.count(sepetRoot));
        sepetCq.where(cb.equal(sepetRoot.get("kullanici").get("id"), kullaniciId));
        Long sepetSayisi = this.entityManager.createQuery(sepetCq).getSingleResult();

        return siparisSayisi + sepetSayisi > 0;
    }
}
