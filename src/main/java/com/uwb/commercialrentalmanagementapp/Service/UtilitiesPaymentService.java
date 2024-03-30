package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.UtilitiesPayment;
import com.uwb.commercialrentalmanagementapp.Repository.UtilitiesPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public List<BigDecimal> getUtilitiesAmountsForProperty(Long propertyId) {
        List<BigDecimal> utilitiesPayments = utilitiesPaymentRepository.findAmountsByPropertyId(propertyId);
        return utilitiesPayments != null ? utilitiesPayments : Collections.emptyList();
    }

//    public BigDecimal getLatestUtilitiesAmountForProperty(Long propertyId){
//        // Pobierz najnowszy rekord opłat za media dla danej nieruchomości
//        Optional<UtilitiesPayment> latestPayment = utilitiesPaymentRepository.findTopByPropertyIdOrderByPaymentDateDesc(propertyId);
//        if (latestPayment.isPresent()) {
//            return latestPayment.get().getAmount();
//        } else {
//            return BigDecimal.ZERO; // Zwróć zero, jeśli brak informacji o płatnościach
//        }
//    }

    public BigDecimal getLatestUtilitiesAmountForProperty(Long propertyId) {
        return utilitiesPaymentRepository.findLatestUtilitiesAmountForProperty(propertyId);
    }
    public List<String> getUtilitiesMonthsForProperty(Long propertyId) {
        return utilitiesPaymentRepository.findMonthsByPropertyId(propertyId);
    }



}
