package com.uwb.commercialrentalmanagementapp.Repository;

import com.uwb.commercialrentalmanagementapp.Model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository  extends JpaRepository<Tenant, Long> {
}
