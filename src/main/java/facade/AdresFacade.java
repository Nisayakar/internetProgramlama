package facade;

import entity.Adres;
import facadeLocal.AdresFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@Stateless
public class AdresFacade extends AbstractFacade implements AdresFacadeLocal {

    public Adres kaydet(Adres adres) {
        this.entityManager.persist(adres);
        this.entityManager.flush();
        return adres;
    }

    public Adres guncelle(Adres adres) {
        this.entityManager.merge(adres);
        this.entityManager.flush();
        return adres;
    }

    public void sil(Adres adres) {
        Adres merged = this.entityManager.merge(adres);
        this.entityManager.remove(merged);
    }

    public Adres bul(Long id) {
        return this.entityManager.find(Adres.class, id);
    }

    public List<Adres> kullaniciAdresleriniGetir(Long kullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Adres> cq = cb.createQuery(Adres.class);
        Root<Adres> root = cq.from(Adres.class);
        cq.where(cb.equal(root.get("kullanici").get("id"), kullaniciId));
        CriteriaQuery<Adres> all = cq.select(root).orderBy(cb.asc(root.get("id")));
        TypedQuery<Adres> q = this.entityManager.createQuery(all);
        return q.getResultList();
    }

    public boolean kullaniciAdresiMi(Long adresId, Long kullaniciId) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Adres> root = cq.from(Adres.class);
        cq.select(cb.count(root));
        cq.where(
                cb.equal(root.get("id"), adresId),
                cb.equal(root.get("kullanici").get("id"), kullaniciId)
        );
        TypedQuery<Long> q = this.entityManager.createQuery(cq);
        Long sayi = q.getSingleResult();
        return sayi > 0;
    }
}
