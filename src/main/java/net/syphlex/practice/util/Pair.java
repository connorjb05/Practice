package net.syphlex.practice.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<X, Y> {
    private X x;
    private Y y;

    public Pair(X x, Y y){
        this.x = x;
        this.y = y;
    }
}
