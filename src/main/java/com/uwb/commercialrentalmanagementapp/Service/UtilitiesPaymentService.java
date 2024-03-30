package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.UtilitiesPayment;
import com.uwb.commercialrentalmanagementapp.Repository.UtilitiesPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UtilitiesPaymentService {

    private final UtilitiesPaymentRepository utilitiesPaymentRepository;

    @Autowired
    public UtilitiesPaymentService(UtilitiesPaymentRepository utilitiesPaymentRepository) {
        this.utilitiesPaymentRepository = utilitiesPaymentRepository;
    }

    public String getUtilitiesStatusForProperty(Long propertyId) {
        String status = utilitiesPaymentRepository.findStatusByPropertyId(propertyId);
        if ("paid".equalsIgnoreCase(status)) {
            return "Opłacone";
        } else {
            return "Opłata za media nie została jeszcze dokonana.";
        }
    }

    public BigDecimal getUtilitiesAmountForProperty(Long propertyId) {
        BigDecimal utilitiesPayment = utilitiesPaymentRepository.findAmountByPropertyId(propertyId);
        if (utilitiesPayment != null) {
            return utilitiesPayment;
        } else {
            return BigDecimal.ZERO; // Jeśli brak informacji o płatnościach, zwróć zero
        }
    }
}
