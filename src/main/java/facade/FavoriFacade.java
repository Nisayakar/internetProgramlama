package facade;

import entity.Favori;
import facadeLocal.FavoriFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class FavoriFacade extends AbstractFacade implements FavoriFacadeLocal {

    public Favori kaydet(Favori favori) {
        this.entityManager.persist(favori);
        this.entityManager.flush();
        return favori;
    }

    public void sil(Favori favori) {
        Favori merged = this.entityManager.merge(favori);
        this.entityManager.remove(merged);
    }

    public Favori bul(Long id) {
        return this.entityManager.find(Favori.class, id);
    }

    public Favori kullaniciUrunFavorisi(Long kullaniciId, Long urunId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Favori> cq = cb.createQuery(Favori.class);
        Root<Favori> root = cq.from(Favori.class);
        root.fetch("urun", JoinType.LEFT);
        cq.where(
                cb.equal(root.get("kullanici").get("id"), kullaniciId),
                cb.equal(root.get("urun").get("id"), urunId)
        );
        CriteriaQuery<Favori> all = cq.select(root);
        TypedQuery<Favori> q = this.entityManager.createQuery(all);
        List<Favori> found = q.getResultList();

        if (found.isEmpty()) {
            return null;
        } else {
            return found.get(0);
        }
    }

    public List<Favori> kullaniciFavorileriniGetir(Long kullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Favori> cq = cb.createQuery(Favori.class);
        Root<Favori> root = cq.from(Favori.class);
        root.fetch("urun", JoinType.LEFT);
        cq.where(cb.equal(root.get("kullanici").get("id"), kullaniciId));
        CriteriaQuery<Favori> all = cq.select(root).orderBy(cb.desc(root.get("eklenmeTarihi")));
        TypedQuery<Favori> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public boolean favorideMi(Long kullaniciId, Long urunId) {
        return kullaniciUrunFavorisi(kullaniciId, urunId) != null;
    }

    public boolean urunFavorideVar(Long urunId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Favori> root = cq.from(Favori.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("urun").get("id"), urunId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long sayi = q.getSingleResult();
        return sayi > 0;
    }
}
