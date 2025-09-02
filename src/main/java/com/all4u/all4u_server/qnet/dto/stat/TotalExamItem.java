package com.all4u.all4u_server.qnet.dto.stat;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class TotalExamItem {
    // 기준년도(=1) ~ 과거(=6)
    private Long pilrccnt1; private Long pilrccnt2; private Long pilrccnt3;
    private Long pilrccnt4; private Long pilrccnt5; private Long pilrccnt6;

    private Long silrccnt1; private Long silrccnt2; private Long silrccnt3;
    private Long silrccnt4; private Long silrccnt5; private Long silrccnt6;

    private Long pilpscnt1; private Long pilpscnt2; private Long pilpscnt3;
    private Long pilpscnt4; private Long pilpscnt5; private Long pilpscnt6;

    private Long silpacnt1; private Long silpacnt2; private Long silpacnt3;
    private Long silpacnt4; private Long silpacnt5; private Long silpacnt6;
}
