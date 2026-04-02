package com.sparta.lucky.hub.infrastructure.tmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapRouteResponse {

    private List<Feature> features;

    public int getTotalDistance() {
        return features.get(0).getProperties().getTotalDistance();
    }

    public int getTotalTime() {
        return features.get(0).getProperties().getTotalTime();
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        private Properties properties;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private int totalDistance;
        private int totalTime;
    }
}