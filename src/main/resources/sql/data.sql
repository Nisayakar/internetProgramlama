INSERT INTO kategori (ad) VALUES
('Gıda'),
('Temizlik'),
('Mutfak')
ON CONFLICT (ad) DO NOTHING;

INSERT INTO kullanici (adsoyad, eposta, sifre, rol) VALUES
('Admin Kullanıcı', 'admin@marketsistemi.local', 'admin123', 'ADMIN'),
('Müşteri Kullanıcı', 'musteri@marketsistemi.local', 'musteri123', 'MUSTERI')
ON CONFLICT (eposta) DO NOTHING;

INSERT INTO urun (ad, aciklama, guncelfiyat, eskifiyat, stokadedi, gorselurl, kategori_id)
SELECT 'Organik Kırmızı Mercimek', '500 gr paket', 79.90, NULL, 25, '/resources/uploads/organik-kirmizi-mercimek-500gr.png', k.id
FROM kategori k
WHERE k.ad = 'Gıda'
  AND NOT EXISTS (SELECT 1 FROM urun u WHERE u.ad = 'Organik Kırmızı Mercimek');

INSERT INTO urun (ad, aciklama, guncelfiyat, eskifiyat, stokadedi, gorselurl, kategori_id)
SELECT 'Bıçak Seti', '10 parça mutfak bıçak seti', 1299.90, NULL, 8, '/resources/uploads/600.15.01.1221_emsan_kobe_10_parca_bicak_seti.png', k.id
FROM kategori k
WHERE k.ad = 'Mutfak'
  AND NOT EXISTS (SELECT 1 FROM urun u WHERE u.ad = 'Bıçak Seti');
