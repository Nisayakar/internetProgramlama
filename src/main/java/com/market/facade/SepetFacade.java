package com.market.facade;

import com.market.entity.Kullanici;
import com.market.entity.Sepet;
import com.market.facadeLocal.SepetFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class SepetFacade implements SepetFacadeLocal {

    @PersistenceContext(unitName = "marketPU")
    private EntityManager em;

    public Sepet kaydet(Sepet sepet) {
        em.persist(sepet);
        return sepet;
    }

    public Sepet guncelle(Sepet sepet) {
        return em.merge(sepet);
    }

    public void sil(Sepet sepet) {
        em.remove(em.merge(sepet));
    }

    public Sepet bul(Long id) {
        return em.find(Sepet.class, id);
    }

    public List<Sepet> tumSepetleriGetir() {
        return em.createQuery("SELECT DISTINCT s FROM Sepet s LEFT JOIN FETCH s.elemanlar ORDER BY s.id ASC", Sepet.class)
                .getResultList();
    }

    public Sepet kullaniciSepetiniGetir(Long kullaniciId) {
        try {
            return em.createQuery(
                            "SELECT DISTINCT s FROM Sepet s LEFT JOIN FETCH s.elemanlar e LEFT JOIN FETCH e.urun WHERE s.kullanici.id = :kullaniciId",
                            Sepet.class)
                    .setParameter("kullaniciId", kullaniciId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Sepet kullaniciIcinSepetOlustur(Long kullaniciId) {
        Kullanici kullanici = em.getReference(Kullanici.class, kullaniciId);
        Sepet sepet = new Sepet();
        sepet.setKullanici(kullanici);
        sepet.setToplamTutar(0.0);
        em.persist(sepet);
        return sepet;
    }
}