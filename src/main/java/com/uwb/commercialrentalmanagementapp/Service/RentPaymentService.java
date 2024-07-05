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
//stare
//    public List<RentPayment> getRentPaymentsForPropertyId(Long propertyId) {
//        return rentPaymentRepository.findAllByRentalAgreementId(propertyId);
//    }
//nowe
public List<RentPayment> getRentPaymentsForRentalAgreementId(Long rentalAgreementId) {
    return rentPaymentRepository.findAllByRentalAgreementId(rentalAgreementId);
}
    public List<RentPayment> getRentPaymentsForPropertyId(Long propertyId) {
        return rentPaymentRepository.findAllByPropertyId(propertyId);
    }

    public RentPayment getRentPaymentById(Long paymentId) {
        return rentPaymentRepository.findByPaymentId(paymentId)
                .orElse(null);
    }

    public BigDecimal calculateVatAmount(BigDecimal paymentAmount) {
        BigDecimal vatRate = new BigDecimal("0.23");

        return paymentAmount.multiply(vatRate);
    }

    public void updateVatPaidStatus(Long paymentId) {
        RentPayment rentPayment = rentPaymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Rent payment not found for paymentId: " + paymentId));

        rentPayment.setVatPaid(true);
        rentPaymentRepository.save(rentPayment);
    }

    public String getFirstAndLastNameOfLandlord(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono nieruchomości o podanym ID"));

        User owner = userRepository.findById(property.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono właściciela nieruchomości o podanym ID"));

        return owner.getFirstName() + " " + owner.getLastName();
    }

    public String getAddressOfLandlord(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono nieruchomości o podanym ID"));

        System.out.println("Owner ID from Property: " + property.getOwnerId());

        User user = userRepository.findById(property.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika w tabeli USER_APP o podanym ID"));

        Landlord landlord = landlordRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono właściciela nieruchomości w tabeli landlords o podanym USER_ID"));

        System.out.println("Landlord Address: " + landlord.getAddress());

        return landlord.getAddress() + ", " + landlord.getPostCode() + " " + landlord.getCity();
    }

    public String getFirstAndLastNameOfTenant(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));
        System.out.println("Rental Agreement: " + rentalAgreement);

        Tenant tenant = rentalAgreement.getTenant();
        Long tenantId = tenant.getTenantId();
        System.out.println("Tenant ID from Property: " + tenantId);

        Tenant actualTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found for tenantId: " + tenantId));

        User appUser = userRepository.findById(actualTenant.getUserId())
                .orElseThrow(() -> new RuntimeException("AppUser not found for userId: " + actualTenant.getUserId()));

        return appUser.getFirstName() + " " + appUser.getLastName();
    }

    public String getAddressOfTenant(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));
        System.out.println("Rental Agreement: " + rentalAgreement);

        Tenant tenant = rentalAgreement.getTenant();

        return tenant.getAddress() + ", " + tenant.getPostCode() + " " + tenant.getCity();
    }

    public BigDecimal getRentAmount(Long propertyId) {
        RentalAgreement rentalAgreement = rentalAgreementRepository.findByProperty_PropertyId(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Rental agreement not found for propertyId: " + propertyId));

        System.out.println(rentalAgreement.getRentAmount());
        return rentalAgreement.getRentAmount();
    }


    public BigDecimal getAnnualPaymentAmount(Long propertyId) {
//        List<RentPayment> rentPayments = rentPaymentRepository.findAllByRentalAgreementId(rentalAgreementId);
       List<RentPayment> rentPayments = rentPaymentRepository.findAllByPropertyId(propertyId);

        int currentYear = Year.now().getValue();

        BigDecimal totalPaymentAmount = BigDecimal.ZERO;
        for (RentPayment payment : rentPayments) {
            LocalDate paymentLocalDate = payment.getIssueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int paymentYear = paymentLocalDate.getYear();
            if (paymentYear < currentYear) {
                totalPaymentAmount = totalPaymentAmount.add(payment.getPaymentAmount());
            }
        }

        System.out.println("Property ID: " + propertyId);
        System.out.println("Total payment amount for previous year: " + totalPaymentAmount);

        return totalPaymentAmount;
    }

    public BigDecimal getTotalRevenue(Long propertyId) {
        List<RentPayment> rentPayments = rentPaymentRepository.findAllByPropertyId(propertyId);

        BigDecimal totalPaymentAmount = BigDecimal.ZERO;
        for (RentPayment payment : rentPayments) {
                totalPaymentAmount = totalPaymentAmount.add(payment.getPaymentAmount());
        }

        System.out.println("Property ID: " + propertyId);
        System.out.println("Total payment amount for previous year: " + totalPaymentAmount);

        return totalPaymentAmount;
    }


    public Map<Integer, BigDecimal> getAnnualRevenues(Long propertyId) {
        List<RentPayment> rentPayments = rentPaymentRepository.findAllByPropertyId(propertyId);

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
        BigDecimal taxRateDecimal = percentageRate.divide(BigDecimal.valueOf(100));
        return totalRevenue.multiply(taxRateDecimal);
    }
}
