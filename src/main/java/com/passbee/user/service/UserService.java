package com.passbee.user.service;

import com.passbee.common.exception.ResourceNotFoundException;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import com.passbee.user.dto.NicknameUpdateRequestDto;
import com.passbee.user.dto.PasswordUpdateRequestDto;
import com.passbee.user.dto.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 1. 내 정보 조회
     */
    public UserInfoResponseDto getUserInfo(String userEmail) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        return UserInfoResponseDto.from(user);
    }

    /**
     * 2. 닉네임 변경
     */
    @Transactional
    public UserInfoResponseDto updateNickname(String userEmail, NicknameUpdateRequestDto requestDto) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        String newNickname = requestDto.nickname();

        // 닉네임 중복 검사
        if (usersRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        user.setNickname(newNickname);
        Users savedUser = usersRepository.save(user);

        return UserInfoResponseDto.from(savedUser);
    }

    /**
     * 3. 비밀번호 변경
     */
    @Transactional
    public void updatePassword(String userEmail, PasswordUpdateRequestDto requestDto) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 1. 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2. 새 비밀번호와 현재 비밀번호가 다른지 확인 (선택 사항이지만 권장)
        if (requestDto.oldPassword().equals(requestDto.newPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 3. 새 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
        usersRepository.save(user);
    }
}