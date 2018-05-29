package com.sitdh.thesis.core.cotton.analyzer.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class GraphVector {

    @Getter @Setter
    private String source;

    @Getter @Setter
    private String edge;

    @Getter @Setter
    private String target;

}
