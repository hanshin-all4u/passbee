package com.passbee.favorite;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
@Embeddable
public class FavoriteId implements Serializable {
    private Long userId;
    private String jmcd; // Integer licenseId -> String jmcd
}