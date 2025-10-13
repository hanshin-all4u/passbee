package com.passbee.favorite.service;

import com.passbee.favorite.domain.Favorite;
import com.passbee.favorite.domain.FavoriteType;
import com.passbee.favorite.repo.FavoriteRepository;
import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UsersRepository usersRepository;
    private final LicenseRepository licenseRepository;

    @Transactional
    public void addLicenseFavorite(String userEmail, String jmcd) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        License license = licenseRepository.findById(jmcd)
                .orElseThrow(() -> new IllegalArgumentException("자격증 정보를 찾을 수 없습니다: " + jmcd));

        if (favoriteRepository.existsByUserIdAndTargetTypeAndTargetId(user.getId(), FavoriteType.LICENSE, jmcd)) {
            throw new IllegalArgumentException("이미 즐겨찾기에 추가된 자격증입니다.");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .targetType(FavoriteType.LICENSE)
                .targetId(jmcd)
                .build();
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeLicenseFavorite(String userEmail, String jmcd) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        favoriteRepository.deleteByUserIdAndTargetTypeAndTargetId(user.getId(), FavoriteType.LICENSE, jmcd);
    }

    @Transactional(readOnly = true)
    public List<License> getLicenseFavorites(String userEmail) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        return user.getFavorites().stream()
                .filter(fav -> fav.getTargetType() == FavoriteType.LICENSE)
                .map(fav -> licenseRepository.findById(fav.getTargetId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
}