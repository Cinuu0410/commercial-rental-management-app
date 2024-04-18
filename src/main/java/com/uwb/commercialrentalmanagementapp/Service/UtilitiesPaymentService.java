package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Repository.UtilitiesPaymentRepository;
import com.uwb.commercialrentalmanagementapp.Model.UtilitiesPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
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


    public BigDecimal getLatestUtilitiesAmountForProperty(Long propertyId) {
        return utilitiesPaymentRepository.findLatestUtilitiesAmountForProperty(propertyId);
    }

    public BigDecimal getUtilitiesAmountForPayment(Long paymentId) {
        Optional<UtilitiesPayment> utilitiesPaymentOptional = utilitiesPaymentRepository.findById(paymentId);

        if (utilitiesPaymentOptional.isPresent()) {
            return utilitiesPaymentOptional.get().getAmount();
        } else {
            throw new RuntimeException("Nie znaleziono płatności za media o podanym ID: " + paymentId);
        }
    }

    public List<String> getUtilitiesMonthsForProperty(Long propertyId) {
        return utilitiesPaymentRepository.findMonthsByPropertyId(propertyId);
    }


    public String getPaymentMonthForProperty(Long propertyId) {
        return utilitiesPaymentRepository.findUnpaidPaymentMonthByPropertyId(propertyId);
    }

    public Long getPaymentIdForProperty(Long propertyId) {
        return utilitiesPaymentRepository.findPaymentIdForUnpaidPaymentByPropertyId(propertyId);
    }

    // Metoda aktualizująca status płatności za media na "paid"
    public void updateUtilitiesStatusToPaid(Long paymentId, Date paymentDate) {
        utilitiesPaymentRepository.updateStatusToPaid(paymentId, paymentDate);
    }
}