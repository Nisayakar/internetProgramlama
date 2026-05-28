UPDATE urun SET guncelfiyat = 0 WHERE guncelfiyat IS NULL;
UPDATE urun SET stokadedi = 0 WHERE stokadedi IS NULL;
UPDATE sepet SET toplamtutar = 0 WHERE toplamtutar IS NULL;
UPDATE sepet_elemani SET miktar = 1 WHERE miktar IS NULL;
UPDATE sepet_elemani SET aratoplam = 0 WHERE aratoplam IS NULL;
UPDATE siparis SET siparistarihi = CURRENT_TIMESTAMP WHERE siparistarihi IS NULL;
UPDATE siparis SET geneltoplam = 0 WHERE geneltoplam IS NULL;
UPDATE siparis SET durum = 'ONAY_BEKLIYOR' WHERE durum IS NULL OR durum = '';
UPDATE siparis_detay SET miktar = 1 WHERE miktar IS NULL;
UPDATE siparis_detay SET alinanfiyat = 0 WHERE alinanfiyat IS NULL;

UPDATE siparis SET durum = 'ONAY_BEKLIYOR' WHERE durum = 'Onay Bekliyor';
UPDATE siparis SET durum = 'ONAYLANDI' WHERE durum IN ('Onaylandı', 'Onaylandi');
UPDATE siparis SET durum = 'HAZIRLANIYOR' WHERE durum IN ('Hazırlanıyor', 'Hazirlaniyor');
UPDATE siparis SET durum = 'TESLIM_EDILDI' WHERE durum = 'Teslim Edildi';
UPDATE siparis SET durum = 'IPTAL_EDILDI' WHERE durum IN ('İptal Edildi', 'Iptal Edildi');

UPDATE urun
SET gorselurl = replace(gorselurl, '/uploads/', '/resources/uploads/')
WHERE gorselurl LIKE '/uploads/%';

ALTER TABLE urun
    ALTER COLUMN ad SET NOT NULL,
    ALTER COLUMN guncelfiyat SET NOT NULL,
    ALTER COLUMN stokadedi SET NOT NULL,
    ALTER COLUMN kategori_id SET NOT NULL;

ALTER TABLE kategori
    ALTER COLUMN ad SET NOT NULL;

ALTER TABLE kullanici
    ALTER COLUMN adsoyad SET NOT NULL,
    ALTER COLUMN eposta SET NOT NULL,
    ALTER COLUMN sifre SET NOT NULL,
    ALTER COLUMN rol SET NOT NULL;

ALTER TABLE sepet
    ALTER COLUMN kullanici_id SET NOT NULL,
    ALTER COLUMN toplamtutar SET NOT NULL;

ALTER TABLE sepet_elemani
    ALTER COLUMN sepet_id SET NOT NULL,
    ALTER COLUMN urun_id SET NOT NULL,
    ALTER COLUMN miktar SET NOT NULL,
    ALTER COLUMN aratoplam SET NOT NULL;

ALTER TABLE siparis
    ALTER COLUMN kullanici_id SET NOT NULL,
    ALTER COLUMN siparistarihi SET NOT NULL,
    ALTER COLUMN geneltoplam SET NOT NULL,
    ALTER COLUMN durum SET NOT NULL;

ALTER TABLE siparis_detay
    ALTER COLUMN siparis_id SET NOT NULL,
    ALTER COLUMN urunadi SET NOT NULL,
    ALTER COLUMN alinanfiyat SET NOT NULL,
    ALTER COLUMN miktar SET NOT NULL;
