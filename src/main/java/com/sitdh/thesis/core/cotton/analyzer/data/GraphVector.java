package com.sitdh.thesis.core.cotton.analyzer.data;

import java.util.Objects;

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

    public boolean equals(GraphVector o) {
    	
    	return this.getSource().equals(o.getSource()) 
    			&& this.getTarget().equals(o.getTarget()) 
    			&& this.getEdge().equals(o.getEdge());
    }
    
    public int hashCode() {
    	return Objects.hash(this.getSource() + this.getEdge() + this.getSource());
    }
}
