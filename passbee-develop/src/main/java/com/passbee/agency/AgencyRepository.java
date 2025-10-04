package com.passbee.agency;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Agency, String> 으로 변경
public interface AgencyRepository extends JpaRepository<Agency, String> {
}