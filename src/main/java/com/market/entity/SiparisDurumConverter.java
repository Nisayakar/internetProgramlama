package com.market.entity;

import com.market.enums.SiparisDurum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class SiparisDurumConverter implements AttributeConverter<SiparisDurum, String> {

    @Override
    public String convertToDatabaseColumn(SiparisDurum durum) {
        return durum == null ? null : durum.getEtiket();
    }

    @Override
    public SiparisDurum convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        for (SiparisDurum durum : SiparisDurum.values()) {
            if (durum.name().equals(value) || durum.getEtiket().equals(value)) {
                return durum;
            }
        }

        if ("Onaylandi".equals(value)) {
            return SiparisDurum.ONAYLANDI;
        }
        if ("Hazirlaniyor".equals(value)) {
            return SiparisDurum.HAZIRLANIYOR;
        }
        if ("Iptal Edildi".equals(value)) {
            return SiparisDurum.IPTAL_EDILDI;
        }

        throw new IllegalArgumentException("Bilinmeyen sipariş durumu: " + value);
    }
}
