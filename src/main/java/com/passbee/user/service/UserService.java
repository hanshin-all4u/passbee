package com.passbee.user.service;

import com.passbee.common.exception.ResourceNotFoundException; // ResourceNotFoundException import 확인
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import com.passbee.user.dto.NicknameUpdateRequestDto;
import com.passbee.user.dto.PasswordUpdateRequestDto;
import com.passbee.user.dto.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException; // BadCredentialsException import 확인
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용 트랜잭션
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
    @Transactional // 쓰기 작업이므로 @Transactional 추가
    public UserInfoResponseDto updateNickname(String userEmail, NicknameUpdateRequestDto requestDto) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        String newNickname = requestDto.nickname();

        // 닉네임 중복 검사
        if (!user.getNickname().equals(newNickname) && usersRepository.existsByNickname(newNickname)) { // 자신의 닉네임으로 변경하는 경우는 제외
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }

        user.setNickname(newNickname);
        Users savedUser = usersRepository.save(user);

        return UserInfoResponseDto.from(savedUser);
    }

    /**
     * 3. 비밀번호 변경
     */
    @Transactional // 쓰기 작업이므로 @Transactional 추가
    public void updatePassword(String userEmail, PasswordUpdateRequestDto requestDto) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 1. 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2. 새 비밀번호와 현재 비밀번호가 다른지 확인
        if (requestDto.oldPassword().equals(requestDto.newPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }

        // 3. 새 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
        usersRepository.save(user);
    }

    /**
     * 4. 회원 탈퇴
     */
    @Transactional // 쓰기 작업이므로 @Transactional 추가
    public void deleteUser(String userEmail) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + userEmail));

        // 사용자와 연관된 데이터 처리 로직 (필요 시 추가)
        // 예: user.getReviews().clear(); user.getComments().clear(); user.getFavorites().clear();
        // 또는 Cascade 설정 활용

        usersRepository.delete(user);
    }
}