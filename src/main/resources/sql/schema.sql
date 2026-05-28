CREATE TABLE IF NOT EXISTS kategori (
    id BIGSERIAL PRIMARY KEY,
    ad VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS kullanici (
    id BIGSERIAL PRIMARY KEY,
    adsoyad VARCHAR(120) NOT NULL,
    eposta VARCHAR(160) NOT NULL UNIQUE,
    sifre VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS urun (
    id BIGSERIAL PRIMARY KEY,
    ad VARCHAR(140) NOT NULL,
    aciklama VARCHAR(500),
    guncelfiyat DOUBLE PRECISION NOT NULL,
    eskifiyat DOUBLE PRECISION,
    stokadedi INTEGER NOT NULL,
    gorselurl VARCHAR(255),
    kategori_id BIGINT NOT NULL,
    CONSTRAINT fk_urun_kategori FOREIGN KEY (kategori_id) REFERENCES kategori(id)
);

CREATE TABLE IF NOT EXISTS sepet (
    id BIGSERIAL PRIMARY KEY,
    kullanici_id BIGINT NOT NULL UNIQUE,
    toplamtutar DOUBLE PRECISION NOT NULL DEFAULT 0,
    CONSTRAINT fk_sepet_kullanici FOREIGN KEY (kullanici_id) REFERENCES kullanici(id)
);

CREATE TABLE IF NOT EXISTS sepet_elemani (
    id BIGSERIAL PRIMARY KEY,
    sepet_id BIGINT NOT NULL,
    urun_id BIGINT NOT NULL,
    miktar INTEGER NOT NULL,
    aratoplam DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_sepet_elemani_sepet FOREIGN KEY (sepet_id) REFERENCES sepet(id) ON DELETE CASCADE,
    CONSTRAINT fk_sepet_elemani_urun FOREIGN KEY (urun_id) REFERENCES urun(id)
);

CREATE TABLE IF NOT EXISTS siparis (
    id BIGSERIAL PRIMARY KEY,
    kullanici_id BIGINT NOT NULL,
    siparistarihi TIMESTAMP NOT NULL,
    geneltoplam DOUBLE PRECISION NOT NULL,
    durum VARCHAR(30) NOT NULL,
    CONSTRAINT fk_siparis_kullanici FOREIGN KEY (kullanici_id) REFERENCES kullanici(id)
);

CREATE TABLE IF NOT EXISTS siparis_detay (
    id BIGSERIAL PRIMARY KEY,
    siparis_id BIGINT NOT NULL,
    urunadi VARCHAR(140) NOT NULL,
    alinanfiyat DOUBLE PRECISION NOT NULL,
    miktar INTEGER NOT NULL,
    CONSTRAINT fk_siparis_detay_siparis FOREIGN KEY (siparis_id) REFERENCES siparis(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS adres (
    id BIGSERIAL PRIMARY KEY,
    baslik VARCHAR(80) NOT NULL,
    il VARCHAR(60) NOT NULL,
    ilce VARCHAR(80) NOT NULL,
    acikadres VARCHAR(500) NOT NULL,
    telefon VARCHAR(20),
    kullanici_id BIGINT NOT NULL,
    CONSTRAINT fk_adres_kullanici FOREIGN KEY (kullanici_id) REFERENCES kullanici(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS favori (
    id BIGSERIAL PRIMARY KEY,
    kullanici_id BIGINT NOT NULL,
    urun_id BIGINT NOT NULL,
    eklenmetarihi TIMESTAMP NOT NULL,
    CONSTRAINT fk_favori_kullanici FOREIGN KEY (kullanici_id) REFERENCES kullanici(id) ON DELETE CASCADE,
    CONSTRAINT fk_favori_urun FOREIGN KEY (urun_id) REFERENCES urun(id) ON DELETE CASCADE,
    CONSTRAINT uq_favori_kullanici_urun UNIQUE (kullanici_id, urun_id)
);

CREATE TABLE IF NOT EXISTS yorum (
    id BIGSERIAL PRIMARY KEY,
    yorummetni VARCHAR(500) NOT NULL,
    puan INTEGER NOT NULL,
    yorumtarihi TIMESTAMP NOT NULL,
    kullanici_id BIGINT NOT NULL,
    urun_id BIGINT NOT NULL,
    CONSTRAINT fk_yorum_kullanici FOREIGN KEY (kullanici_id) REFERENCES kullanici(id) ON DELETE CASCADE,
    CONSTRAINT fk_yorum_urun FOREIGN KEY (urun_id) REFERENCES urun(id) ON DELETE CASCADE
);
