SELECT u.id, u.ad, u.guncelfiyat, u.stokadedi, k.ad AS kategori
FROM urun u
JOIN kategori k ON k.id = u.kategori_id
ORDER BY u.ad;

SELECT s.id, s.siparistarihi, s.geneltoplam, s.durum, k.adsoyad
FROM siparis s
JOIN kullanici k ON k.id = s.kullanici_id
ORDER BY s.siparistarihi DESC;

SELECT sd.urunadi, sd.miktar, sd.alinanfiyat
FROM siparis_detay sd
WHERE sd.siparis_id = 1;

UPDATE urun
SET stokadedi = stokadedi - 1
WHERE id = 1 AND stokadedi > 0;

UPDATE siparis
SET durum = 'HAZIRLANIYOR'
WHERE id = 1;

DELETE FROM sepet_elemani
WHERE sepet_id = 1 AND urun_id = 1;
