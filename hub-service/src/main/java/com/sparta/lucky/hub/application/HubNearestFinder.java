package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.GetHubResult;
import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
public class HubNearestFinder {

    // 주어진 좌표에서 가장 가까운 허브를 찾기
    public GetHubResult findNearestHub(List<GetHubResult> hubs, BigDecimal targetLat, BigDecimal targetLong) {
        return hubs.stream()
                .min(Comparator.comparingDouble(hub -> haversine(
                        hub.getLatitude().doubleValue(), hub.getLongitude().doubleValue(),
                        targetLat.doubleValue(), targetLong.doubleValue()
                )))
                .orElseThrow(() -> new BusinessException(HubErrorCode.HUB_NOT_FOUND));
    }

    // Haversine 공식으로 두 좌표 간 거리(km) 계산
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}