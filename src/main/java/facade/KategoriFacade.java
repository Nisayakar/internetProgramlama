package facade;

import entity.Kategori;
import entity.Urun;
import facadeLocal.KategoriFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class KategoriFacade extends AbstractFacade implements KategoriFacadeLocal {

    public Kategori kaydet(Kategori kategori) {
        this.entityManager.persist(kategori);
        this.entityManager.flush();
        return kategori;
    }

    public Kategori guncelle(Kategori kategori) {
        this.entityManager.merge(kategori);
        this.entityManager.flush();
        return kategori;
    }

    public void sil(Kategori kategori) {
        Kategori merged = this.entityManager.merge(kategori);
        this.entityManager.remove(merged);
    }

    public Kategori bul(Long id) {
        return this.entityManager.find(Kategori.class, id);
    }

    public List<Kategori> tumKategorileriGetir() {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Kategori> cq = cb.createQuery(Kategori.class);
        Root<Kategori> root = cq.from(Kategori.class);
        CriteriaQuery<Kategori> all = cq.select(root).orderBy(cb.asc(root.get("ad")));
        TypedQuery<Kategori> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public boolean urunuVar(Long kategoriId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Urun> root = cq.from(Urun.class);
        cq.select(cb.count(root));
        cq.where(cb.equal(root.get("kategori").get("id"), kategoriId));
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long sayi = q.getSingleResult();

        return sayi > 0;
    }
}
