package com.market.facade;

import com.market.entity.Kullanici;
import com.market.facadeLocal.KullaniciFacadeLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class KullaniciFacade implements KullaniciFacadeLocal {

    @PersistenceContext(unitName = "marketPU")
    private EntityManager em;

    public void kaydet(Kullanici kullanici) {
        em.persist(kullanici);
    }

    public void guncelle(Kullanici kullanici) {
        em.merge(kullanici);
    }

    public void sil(Kullanici kullanici) {
        em.remove(em.merge(kullanici));
    }

    public Kullanici bul(Long id) {
        return em.find(Kullanici.class, id);
    }

    public List<Kullanici> tumKullanicilariGetir() {
        return em.createQuery("SELECT k FROM Kullanici k ORDER BY k.id ASC", Kullanici.class)
                .getResultList();
    }

    public Kullanici epostaIleBul(String eposta) {
        try {
            return em.createQuery("SELECT k FROM Kullanici k WHERE k.eposta = :eposta", Kullanici.class)
                    .setParameter("eposta", eposta)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean epostaKullaniliyor(String eposta, Long mevcutKullaniciId) {
        Long sayi;
        if (mevcutKullaniciId == null) {
            sayi = em.createQuery("SELECT COUNT(k) FROM Kullanici k WHERE k.eposta = :eposta", Long.class)
                    .setParameter("eposta", eposta)
                    .getSingleResult();
        } else {
            sayi = em.createQuery(
                            "SELECT COUNT(k) FROM Kullanici k WHERE k.eposta = :eposta AND k.id <> :id",
                            Long.class)
                    .setParameter("eposta", eposta)
                    .setParameter("id", mevcutKullaniciId)
                    .getSingleResult();
        }
        return sayi > 0;
    }

    public boolean iliskiliKaydiVar(Long kullaniciId) {
        Long siparisSayisi = em.createQuery("SELECT COUNT(s) FROM Siparis s WHERE s.kullanici.id = :kullaniciId", Long.class)
                .setParameter("kullaniciId", kullaniciId)
                .getSingleResult();
        Long sepetSayisi = em.createQuery("SELECT COUNT(s) FROM Sepet s WHERE s.kullanici.id = :kullaniciId", Long.class)
                .setParameter("kullaniciId", kullaniciId)
                .getSingleResult();
        return siparisSayisi + sepetSayisi > 0;
    }
}