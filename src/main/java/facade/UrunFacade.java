package facade;

import entity.Urun;
import entity.SepetElemani;
import facadeLocal.UrunFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class UrunFacade extends AbstractFacade implements UrunFacadeLocal {

    public Urun kaydet(Urun urun) {
        this.entityManager.persist(urun);
        this.entityManager.flush();
        return urun;
    }

    public Urun guncelle(Urun urun) {
        this.entityManager.merge(urun);
        this.entityManager.flush();
        return urun;
    }

    public void sil(Urun urun) {
        Urun merged = this.entityManager.merge(urun);
        this.entityManager.remove(merged);
    }

    public Urun bul(Long id) {
        return this.entityManager.find(Urun.class, id);
    }

    public List<Urun> tumUrunleriGetir() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Urun> cq = cb.createQuery(Urun.class);
        Root<Urun> root = cq.from(Urun.class);
        CriteriaQuery<Urun> all = cq.select(root).orderBy(cb.asc(root.get("ad")));
        TypedQuery<Urun> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public List<Urun> kategoriyeGoreUrunleriGetir(Long kategoriId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Urun> cq = cb.createQuery(Urun.class);
        Root<Urun> root = cq.from(Urun.class);
        cq.where(cb.equal(root.get("kategori").get("id"), kategoriId));
        CriteriaQuery<Urun> all = cq.select(root).orderBy(cb.asc(root.get("ad")));
        TypedQuery<Urun> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public boolean sepetteVar(Long urunId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<SepetElemani> root = cq.from(SepetElemani.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("urun").get("id"), urunId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long adet = q.getSingleResult();

        return adet > 0;
    }
}
