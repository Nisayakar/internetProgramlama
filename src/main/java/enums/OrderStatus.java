package enums;

public enum OrderStatus {
    PENDING_APPROVAL("Onay Bekliyor"),
    APPROVED("Onaylandı"),
    PREPARING("Hazırlanıyor"),
    DELIVERED("Teslim Edildi"),
    CANCELLED("İptal Edildi");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}



