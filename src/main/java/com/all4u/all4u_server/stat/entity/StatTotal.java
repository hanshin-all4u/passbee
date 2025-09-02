package com.all4u.all4u_server.stat.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class StatTotal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int baseYear; // 요청한 기준년도

    // 필기 응시/합격
    private Long pilrccnt1, pilrccnt2, pilrccnt3, pilrccnt4, pilrccnt5, pilrccnt6;
    private Long pilpscnt1, pilpscnt2, pilpscnt3, pilpscnt4, pilpscnt5, pilpscnt6;

    // 실기 응시/합격
    private Long silrccnt1, silrccnt2, silrccnt3, silrccnt4, silrccnt5, silrccnt6;
    private Long silpacnt1, silpacnt2, silpacnt3, silpacnt4, silpacnt5, silpacnt6;
}
