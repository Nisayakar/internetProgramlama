package com.market.facade;

import com.market.entity.Urun;
import com.market.facadeLocal.UrunFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class UrunFacade implements UrunFacadeLocal {

    @PersistenceContext(unitName = "marketPU")
    private EntityManager em;

    public void kaydet(Urun urun) {
        em.persist(urun);
    }

    public void guncelle(Urun urun) {
        em.merge(urun);
    }

    public void sil(Urun urun) {
        em.remove(em.merge(urun));
    }

    public Urun bul(Long id) {
        return em.find(Urun.class, id);
    }

    public List<Urun> tumUrunleriGetir() {
        return em.createQuery("SELECT u FROM Urun u ORDER BY u.ad ASC", Urun.class).getResultList();
    }

    public List<Urun> kategoriyeGoreUrunleriGetir(Long kategoriId) {
        return em.createQuery("SELECT u FROM Urun u WHERE u.kategori.id = :kategoriId ORDER BY u.ad ASC", Urun.class)
                .setParameter("kategoriId", kategoriId)
                .getResultList();
    }

    public boolean sepetteVar(Long urunId) {
        Long adet = em.createQuery("SELECT COUNT(se) FROM SepetElemani se WHERE se.urun.id = :urunId", Long.class)
                .setParameter("urunId", urunId)
                .getSingleResult();
        return adet > 0;
    }
}