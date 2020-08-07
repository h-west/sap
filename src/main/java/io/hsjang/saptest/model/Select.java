package io.hsjang.saptest.model;

import java.util.ArrayList;
import java.util.Arrays;

public class Select extends ArrayList<Object>{
    private static final long serialVersionUID = 1L;
    
    public Select(Object objects){
        super(Arrays.asList(objects));
    }

    public Select(Object ...objects){
        super(Arrays.asList(objects));
    }
}