package com.passbee.related;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseRelationRepository extends JpaRepository<LicenseRelation, Long> {
    List<LicenseRelation> findBySrcJmcdAndTypeOrderByWeightDesc(String srcJmcd, RelationType type);
}