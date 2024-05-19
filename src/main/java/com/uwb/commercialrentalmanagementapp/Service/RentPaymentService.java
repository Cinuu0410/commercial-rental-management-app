package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.*;
import com.uwb.commercialrentalmanagementapp.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.*;
import java.time.Year;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
public class RentPaymentService {

    private final RentPaymentRepository rentPaymentRepository;
    private final RentalAgreementRepository rentalAgreementRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final LandlordRepository landlordRepository;
    private final TenantRepository tenantRepository;

    @Autowired
    public RentPaymentService(RentPaymentRepository rentPaymentRepository, RentalAgreementRepository rentalAgreementRepository, UserRepository userRepository, PropertyRepository propertyRepository, LandlordRepository landlordRepository, TenantRepository tenantRepository) {
        this.rentPaymentRepository = rentPaymentRepository;
        this.rentalAgreementRepository = rentalAgreementRepository;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.landlordRepository = landlordRepository;
        this.tenantRepository = tenantRepository;
    }



    public void createInvoice(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));

        RentPayment rentPayment = new RentPayment();
        rentPayment.setIssueDate(new Date());
        rentPayment.setRentalAgreementId(rentalAgreement.getAgreementId());
        rentPayment.setStatus("unpaid");
        rentPayment.setPaymentAmount(rentalAgreement.getRentAmount());
        rentPayment.setVatPaid(false);

        rentPaymentRepository.save(rentPayment);
    }

    public List<RentPayment> getRentPaymentsForPropertyId(Long propertyId) {
        return rentPaymentRepository.findAllByRentalAgreementId(propertyId);
    }

    public RentPayment getRentPaymentById(Long paymentId) {
        return rentPaymentRepository.findByPaymentId(paymentId)
                .orElse(null);
    }

    public BigDecimal calculateVatAmount(BigDecimal paymentAmount) {
        // Zakładam, że stawka VAT wynosi 23%
        BigDecimal vatRate = new BigDecimal("0.23");

        // Obliczanie kwoty VAT
        return paymentAmount.multiply(vatRate);
    }

    public void updateVatPaidStatus(Long paymentId) {
        RentPayment rentPayment = rentPaymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Rent payment not found for paymentId: " + paymentId));

        rentPayment.setVatPaid(true);
        rentPaymentRepository.save(rentPayment);
    }

    public String getFirstAndLastNameOfLandlord(Long propertyId) {
        // Pobierz property na podstawie propertyId
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono nieruchomości o podanym ID"));

        // Pobierz ownera na podstawie owner_id z property
        User owner = userRepository.findById(property.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono właściciela nieruchomości o podanym ID"));

        // Zwróć imię i nazwisko właściciela
        return owner.getFirstName() + " " + owner.getLastName();
    }

    public String getAddressOfLandlord(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono nieruchomości o podanym ID"));

        System.out.println("Owner ID from Property: " + property.getOwnerId()); // Dodaj to logowanie

        // Znajdź właściciela na podstawie user_id
        User user = userRepository.findById(property.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika w tabeli USER_APP o podanym ID"));

        // Znajdź właściciela na podstawie user_id z USER_APP
        Landlord landlord = landlordRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono właściciela nieruchomości w tabeli landlords o podanym USER_ID"));

        System.out.println("Landlord Address: " + landlord.getAddress()); // Dodaj to logowanie

        return landlord.getAddress() + ", " + landlord.getPostCode() + " " + landlord.getCity();
    }

    public String getFirstAndLastNameOfTenant(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));
        System.out.println("Rental Agreement: " + rentalAgreement);

        Tenant tenant = rentalAgreement.getTenant(); // Pobierz obiekt Tenant z RentalAgreement
        Long tenantId = tenant.getTenantId(); // Pobierz id Tenant z obiektu Tenant
        System.out.println("Tenant ID from Property: " + tenantId);

        Tenant actualTenant = tenantRepository.findById(tenantId) // Użyj instancji tenantRepository
                .orElseThrow(() -> new RuntimeException("Tenant not found for tenantId: " + tenantId));

        User appUser = userRepository.findById(actualTenant.getUserId()) // Użyj user_id z actualTenant
                .orElseThrow(() -> new RuntimeException("AppUser not found for userId: " + actualTenant.getUserId()));

        return appUser.getFirstName() + " " + appUser.getLastName();
    }

    public String getAddressOfTenant(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));
        System.out.println("Rental Agreement: " + rentalAgreement);

        Tenant tenant = rentalAgreement.getTenant(); // Pobierz obiekt Tenant z RentalAgreement

        return tenant.getAddress() + ", " + tenant.getPostCode() + " " + tenant.getCity();
    }

    public BigDecimal getRentAmount(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));

        System.out.println(rentalAgreement.getRentAmount());
        return rentalAgreement.getRentAmount();
    }


    public BigDecimal getAnnualPaymentAmount(Long rentalAgreementId) {
        // Pobierz wszystkie płatności czynszu dla danej umowy najmu
        List<RentPayment> rentPayments = rentPaymentRepository.findAllByRentalAgreementId(rentalAgreementId);

        // Pobierz aktualny rok
        int currentYear = Year.now().getValue();

        BigDecimal totalPaymentAmount = BigDecimal.ZERO;
        for (RentPayment payment : rentPayments) {
            // Przekształć Date na LocalDate
            LocalDate paymentLocalDate = payment.getIssueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int paymentYear = paymentLocalDate.getYear();
            if (paymentYear < currentYear) {
                // Dodaj kwotę płatności do całkowitej sumy
                totalPaymentAmount = totalPaymentAmount.add(payment.getPaymentAmount());
            }
        }

        System.out.println("Rental Agreement ID: " + rentalAgreementId);
        System.out.println("Total payment amount for previous year: " + totalPaymentAmount);

        return totalPaymentAmount;
    }

    public BigDecimal getTotalRevenue(Long rentalAgreementId) {
        // Pobierz wszystkie płatności czynszu dla danej umowy najmu
        List<RentPayment> rentPayments = rentPaymentRepository.findAllByRentalAgreementId(rentalAgreementId);

        BigDecimal totalPaymentAmount = BigDecimal.ZERO;
        for (RentPayment payment : rentPayments) {
                totalPaymentAmount = totalPaymentAmount.add(payment.getPaymentAmount());
        }

        System.out.println("Rental Agreement ID: " + rentalAgreementId);
        System.out.println("Total payment amount for previous year: " + totalPaymentAmount);

        return totalPaymentAmount;
    }

//    public Map<String, BigDecimal> getAnnualRevenueForYear(Long rentalAgreementId, int year) {
//        // Pobierz wszystkie płatności czynszu dla danej umowy najmu
//        List<RentPayment> rentPayments = rentPaymentRepository.findAllByRentalAgreementId(rentalAgreementId);
//
//        Map<String, BigDecimal> yearlyIncome = new HashMap<>();
//
//        List<RentPayment> paymentsForYear = rentPayments.stream()
//                .filter(payment -> {
//                    LocalDate paymentLocalDate = payment.getIssueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                    int paymentYear = paymentLocalDate.getYear();
//                    return paymentYear == year;
//                })
//                .toList();
//
//        BigDecimal totalPaymentAmountForYear = BigDecimal.ZERO;
//        for (RentPayment payment : paymentsForYear) {
//            totalPaymentAmountForYear = totalPaymentAmountForYear.add(payment.getPaymentAmount());
//        }
//        System.out.println("Rental Agreement ID: " + rentalAgreementId);
//        System.out.println("Total payment amount for year 2024: " + totalPaymentAmountForYear);
//        yearlyIncome.put(String.valueOf(year), totalPaymentAmountForYear);
//
//        return yearlyIncome;
//    }

    public Map<Integer, BigDecimal> getAnnualRevenues(Long rentalAgreementId) {
        // Pobierz wszystkie płatności czynszu dla danej umowy najmu
        List<RentPayment> rentPayments = rentPaymentRepository.findAllByRentalAgreementId(rentalAgreementId);

        Map<Integer, BigDecimal> yearlyIncomes = new HashMap<>();

        for (RentPayment payment : rentPayments) {
            LocalDate paymentLocalDate = payment.getIssueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int paymentYear = paymentLocalDate.getYear();

            BigDecimal paymentAmount = payment.getPaymentAmount();
            yearlyIncomes.merge(paymentYear, paymentAmount, BigDecimal::add);
        }

        return yearlyIncomes;
    }

    public List<RentPayment> getLastRentInfoForProperty(Long propertyId) {
        return rentPaymentRepository.findLastRentInfoByRentalAgreementId(propertyId);
    }

    public BigDecimal calculateIncomeTax(BigDecimal totalRevenue, BigDecimal percentageRate) {
        // Wzór na obliczenie podatku dochodowego: podatek = przychód * stawka_podatkowa
        // Załóżmy, że stawka podatkowa jest podana jako wartość procentowa, więc konwertujemy ją na dziesiętną
        BigDecimal taxRateDecimal = percentageRate.divide(BigDecimal.valueOf(100)); // konwersja procentów na dziesiętne
        return totalRevenue.multiply(taxRateDecimal);
    }
}



//        public String getStatus(Long propertyId) {
//        try {
//            // Znajdź umowę najmu dla podanej nieruchomości
//            Optional<RentalAgreement> rentalAgreementOptional = rentalAgreementRepository.findByProperty_PropertyId(propertyId);
//            if (rentalAgreementOptional.isPresent()) {
//                RentalAgreement rentalAgreement = rentalAgreementOptional.get();
//
//                // Znajdź płatność za umowę najmu
//                Optional<RentPayment> rentPaymentOptional = rentPaymentRepository.findByRentalAgreementId(rentalAgreement.getAgreementId());
//                if (rentPaymentOptional.isPresent()) {
//                    RentPayment rentPayment = rentPaymentOptional.get();
//
//                    // Sprawdź status płatności i zwróć odpowiedni komunikat
//                    return rentPayment.getStatus().equalsIgnoreCase("paid") ? "opłacone" : "nieopłacone";
//                } else {
//                    throw new IllegalArgumentException("Rent payment not found for propertyId: " + propertyId);
//                }
//            } else {
//                throw new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId);
//            }
//        } catch (IllegalArgumentException e) {
//            if (e.getMessage().contains("Rent payment not found")) {
//                return "brak rent payment dla danej nieruchomości";
//            } else if (e.getMessage().contains("Rental agreement not found")) {
//                return "brak umowy najmu dla danej nieruchomości";
//            }
//            // jeśli wystąpi inny błąd, możesz go tutaj obsłużyć
//            return "Wystąpił błąd";
//        }
//    }

    //    public String getPaymentDate(Long propertyId) {
//        try {
//            // Znajdź umowę najmu dla podanej nieruchomości
//            Optional<RentalAgreement> rentalAgreementOptional = rentalAgreementRepository.findByProperty_PropertyId(propertyId);
//            if (rentalAgreementOptional.isPresent()) {
//                RentalAgreement rentalAgreement = rentalAgreementOptional.get();
//
//                // Znajdź najnowszą datę płatności dla umowy najmu
//                Optional<Date> paymentDateOptional = rentPaymentRepository.findLatestPaymentDateByRentalAgreementId(rentalAgreement.getAgreementId());
//
//                if (paymentDateOptional.isPresent()) {
//                    Date paymentDate = paymentDateOptional.get();
//
//                    // Konwersja Date na LocalDate
//                    LocalDate localPaymentDate = Instant.ofEpochMilli(paymentDate.getTime())
//                            .atZone(ZoneId.systemDefault())
//                            .toLocalDate();
//
//                    // Formatowanie daty do postaci "yyyy-MM-dd"
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//                    return localPaymentDate.format(formatter); // Zwróć sformatowaną datę
//                } else {
//                    throw new IllegalArgumentException("Rent payment not found for propertyId: " + propertyId);
//                }
//            } else {
//                throw new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId);
//            }
//        } catch (IllegalArgumentException e) {
//            if (e.getMessage().contains("Rent payment not found")) {
//                return "brak historii płatności dla danej nieruchomości";
//            } else if (e.getMessage().contains("Rental agreement not found")) {
//                return "brak umowy najmu dla danej nieruchomości";
//            }
//            // jeśli wystąpi inny błąd, możesz go tutaj obsłużyć
//            return "Wystąpił błąd";
//        }
//    }

    //    public String getNextInvoiceIssueDate(Long propertyId) {
//        String paymentDateStr = getPaymentDate(propertyId);
//
//        try {
//            LocalDate paymentDate = LocalDate.parse(paymentDateStr);
//            LocalDate nextInvoiceIssueDate = paymentDate.plusMonths(1);
//
//            // Formatuj datę do postaci "yyyy-MM-dd"
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            return nextInvoiceIssueDate.format(formatter);
//        } catch (DateTimeParseException e) {
//            if (e.getMessage().contains("Rent payment not found")) {
//                return "brak historii płatności dla danej nieruchomości";
//            } else if (e.getMessage().contains("Rental agreement not found")) {
//                return "brak umowy najmu dla danej nieruchomości";
//            }
//            // jeśli wystąpi inny błąd, możesz go tutaj obsłużyć
//            return "Wystąpił błąd: " + e.getMessage();
//        }
//    }
