package com.uwb.commercialrentalmanagementapp.Service;

import com.uwb.commercialrentalmanagementapp.Model.Tenant;
import com.uwb.commercialrentalmanagementapp.Model.User;
import com.uwb.commercialrentalmanagementapp.Repository.TenantRepository;
import com.uwb.commercialrentalmanagementapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }


    public String getFullNameByTenantId(Long tenantId) {
        Tenant tenantData = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found for tenantId: " + tenantId));

        User appUser = userRepository.findById(tenantData.getUserId())
                .orElseThrow(() -> new RuntimeException("AppUser not found for userId: " + tenantData.getUserId()));

        return appUser.getFirstName() + " " + appUser.getLastName();
    }
}
