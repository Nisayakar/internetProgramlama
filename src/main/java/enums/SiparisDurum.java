package enums;

public enum SiparisDurum {
    ONAY_BEKLIYOR("Onay Bekliyor"),
    ONAYLANDI("Onaylandı"),
    HAZIRLANIYOR("Hazırlanıyor"),
    TESLIM_EDILDI("Teslim Edildi"),
    IPTAL_EDILDI("İptal Edildi");

    private final String etiket;

    SiparisDurum(String etiket) {
        this.etiket = etiket;
    }

    public String getEtiket() {
        return etiket;
    }

    @Override
    public String toString() {
        return etiket;
    }
}

