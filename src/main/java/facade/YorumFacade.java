package facade;

import entity.Yorum;
import facadeLocal.YorumFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class YorumFacade extends AbstractFacade implements YorumFacadeLocal {

    public Yorum kaydet(Yorum yorum) {
        this.entityManager.persist(yorum);
        this.entityManager.flush();
        return yorum;
    }

    public void sil(Yorum yorum) {
        Yorum merged = this.entityManager.merge(yorum);
        this.entityManager.remove(merged);
    }

    public Yorum bul(Long id) {
        return this.entityManager.find(Yorum.class, id);
    }

    public List<Yorum> urunYorumlariniGetir(Long urunId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Yorum> cq = cb.createQuery(Yorum.class);
        Root<Yorum> root = cq.from(Yorum.class);
        root.fetch("kullanici", JoinType.LEFT);
        cq.where(cb.equal(root.get("urun").get("id"), urunId));
        CriteriaQuery<Yorum> all = cq.select(root).orderBy(cb.desc(root.get("yorumTarihi")));
        TypedQuery<Yorum> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public List<Yorum> kullaniciYorumlariniGetir(Long kullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Yorum> cq = cb.createQuery(Yorum.class);
        Root<Yorum> root = cq.from(Yorum.class);
        cq.where(cb.equal(root.get("kullanici").get("id"), kullaniciId));
        CriteriaQuery<Yorum> all = cq.select(root).orderBy(cb.desc(root.get("yorumTarihi")));
        TypedQuery<Yorum> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public boolean urunYorumuVar(Long urunId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Yorum> root = cq.from(Yorum.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("urun").get("id"), urunId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long sayi = q.getSingleResult();
        return sayi > 0;
    }
}
