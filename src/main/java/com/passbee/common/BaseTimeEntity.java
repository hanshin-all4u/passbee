package com.passbee.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
// ↓↓↓ 바로 이 어노테이션이 핵심입니다! ↓↓↓
// "이 클래스는 테이블을 만드는 진짜 설계도가 아니라, 다른 설계도들이 공통으로 사용하는 '밑그림'일 뿐이다"
// 라고 JPA에게 알려주어, ID 충돌이나 자동 증가(auto_increment) 관련 문제를 원천 차단합니다.
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 생성일/수정일 자동 기록 기능을 활성화합니다.
public abstract class BaseTimeEntity {

    // 생성 시간을 기록하는 필드
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 마지막 수정 시간을 기록하는 필드
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
