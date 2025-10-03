package com.passbee.favorite;

import com.passbee.common.BaseTimeEntity;
import com.passbee.license.License;
import com.passbee.user.Users;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "favorite")
public class Favorite extends BaseTimeEntity {

    @EmbeddedId
    private FavoriteId id;

    @MapsId("userId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @MapsId("jmcd") // "licenseId" -> "jmcd"
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jmcd") // "license_id" -> "jmcd"
    private License license;
}