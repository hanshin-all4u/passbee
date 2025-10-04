package com.passbee.qnet.dto.stat;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class GradPiExamItem {
    private String gradename; // 기술사/기능장/기사/산업기사/전문사무/기능사/기능사보
    private Long statisyy1, statisyy2, statisyy3, statisyy4, statisyy5, statisyy6;
}