package facade;

import entity.Kullanici;
import entity.Sepet;
import facadeLocal.SepetFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class SepetFacade extends AbstractFacade implements SepetFacadeLocal {

    public Sepet kaydet(Sepet sepet) {
        this.entityManager.persist(sepet);
        this.entityManager.flush();
        return sepet;
    }

    public Sepet guncelle(Sepet sepet) {
        Sepet merged = this.entityManager.merge(sepet);
        this.entityManager.flush();
        return merged;
    }

    public void sil(Sepet sepet) {
        Sepet merged = this.entityManager.merge(sepet);
        this.entityManager.remove(merged);
    }

    public Sepet bul(Long id) {
        return this.entityManager.find(Sepet.class, id);
    }

    public List<Sepet> tumSepetleriGetir() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Sepet> cq = cb.createQuery(Sepet.class);
        Root<Sepet> root = cq.from(Sepet.class);
        root.fetch("elemanlar", JoinType.LEFT);
        cq.distinct(true);
        CriteriaQuery<Sepet> all = cq.select(root).orderBy(cb.asc(root.get("id")));
        TypedQuery<Sepet> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public Sepet kullaniciSepetiniGetir(Long kullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Sepet> cq = cb.createQuery(Sepet.class);
        Root<Sepet> root = cq.from(Sepet.class);
        Fetch<Object, Object> elemanlar = root.fetch("elemanlar", JoinType.LEFT);
        elemanlar.fetch("urun", JoinType.LEFT);
        cq.where(cb.equal(root.get("kullanici").get("id"), kullaniciId));
        cq.distinct(true);
        CriteriaQuery<Sepet> all = cq.select(root);
        TypedQuery<Sepet> q = this.entityManager.createQuery(all);
        List<Sepet> found = q.getResultList();

        if (found.isEmpty()) {
            return null;
        } else {
            return found.get(0);
        }
    }

    public Sepet kullaniciIcinSepetOlustur(Long kullaniciId) {
        Kullanici kullanici = this.entityManager.getReference(Kullanici.class, kullaniciId);
        Sepet sepet = new Sepet();
        sepet.setKullanici(kullanici);
        sepet.setToplamTutar(0.0);
        this.entityManager.persist(sepet);
        this.entityManager.flush();
        return sepet;
    }
}
