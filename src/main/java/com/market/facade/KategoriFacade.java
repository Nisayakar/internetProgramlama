package com.market.facade;

import com.market.entity.Kategori;
import com.market.facadeLocal.KategoriFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class KategoriFacade implements KategoriFacadeLocal {

    @PersistenceContext(unitName = "marketPU")
    private EntityManager em;

    public void kaydet(Kategori kategori) {
        em.persist(kategori);
    }

    public void guncelle(Kategori kategori) {
        em.merge(kategori);
    }

    public void sil(Kategori kategori) {
        em.remove(em.merge(kategori));
    }

    public Kategori bul(Long id) {
        return em.find(Kategori.class, id);
    }

    public List<Kategori> tumKategorileriGetir() {
        return em.createQuery("SELECT k FROM Kategori k ORDER BY k.ad ASC", Kategori.class).getResultList();
    }

    public boolean urunuVar(Long kategoriId) {
        Long sayi = em.createQuery("SELECT COUNT(u) FROM Urun u WHERE u.kategori.id = :kategoriId", Long.class)
                .setParameter("kategoriId", kategoriId)
                .getSingleResult();
        return sayi > 0;
    }
}