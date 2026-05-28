package facade;

import entity.Urun;
import entity.SepetElemani;
import entity.Favori;
import entity.Yorum;
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
        CriteriaQuery<Long> sepetCq = cb.createQuery(Long.class);
        Root<SepetElemani> sepetRoot = sepetCq.from(SepetElemani.class);
        sepetCq.select(cb.count(sepetRoot));
        sepetCq.where(cb.equal(sepetRoot.get("urun").get("id"), urunId));
        Long sepetAdet = this.entityManager.createQuery(sepetCq).getSingleResult();

        CriteriaQuery<Long> favoriCq = cb.createQuery(Long.class);
        Root<Favori> favoriRoot = favoriCq.from(Favori.class);
        favoriCq.select(cb.count(favoriRoot));
        favoriCq.where(cb.equal(favoriRoot.get("urun").get("id"), urunId));
        Long favoriAdet = this.entityManager.createQuery(favoriCq).getSingleResult();

        CriteriaQuery<Long> yorumCq = cb.createQuery(Long.class);
        Root<Yorum> yorumRoot = yorumCq.from(Yorum.class);
        yorumCq.select(cb.count(yorumRoot));
        yorumCq.where(cb.equal(yorumRoot.get("urun").get("id"), urunId));
        Long yorumAdet = this.entityManager.createQuery(yorumCq).getSingleResult();

        return sepetAdet + favoriAdet + yorumAdet > 0;
    }
}
