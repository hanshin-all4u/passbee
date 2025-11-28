package com.passbee.related;

import com.passbee.related.dto.RelatedLicensesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatedLicenseService {

    private final LicenseRelationRepository repo;

    private static final Set<String> TYPES = Set.of("similar","higher","lower");

    public RelatedLicensesResponse getRelated(String jmcd, String type, int limit){
        if(!TYPES.contains(type)) throw new IllegalArgumentException("invalid type");
        var relType = RelationType.valueOf(type);
        var rows = repo.findBySrcJmcdAndTypeOrderByWeightDesc(jmcd, relType);
        var items = rows.stream()
                .limit(Math.min(limit, 50))
                .map(r -> new RelatedLicensesResponse.Item(
                        r.getDstJmcd(),
                        resolveName(r.getDstJmcd()), // ← 임시 이름 해석
                        r.getWeight(),
                        r.getNote()
                ))
                .collect(Collectors.toList());
        return new RelatedLicensesResponse(jmcd, type, items);
    }

    /** TODO: 실제 License 이름 조회 서비스로 교체하세요. */
    private String resolveName(String jmcd) {
        return jmcd; // 임시
    }
}