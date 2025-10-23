package com.passbee.license;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // List import 추가

// JpaRepository<License, String> 으로 변경
public interface LicenseRepository extends JpaRepository<License, String> {

    /**
     * 키워드를 포함하고(Containing), 대소문자를 무시(IgnoreCase)하여
     * 자격증 목록을 이름(jmfldnm)으로 검색하는 기능
     */
    List<License> findByJmfldnmContainingIgnoreCase(String keyword);
}