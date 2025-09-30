package com.all4u.all4u_server.favorite;

import com.all4u.all4u_server.common.BaseTimeEntity;
import com.all4u.all4u_server.license.License;
import com.all4u.all4u_server.user.Users;
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