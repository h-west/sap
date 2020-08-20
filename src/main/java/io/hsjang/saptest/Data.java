package io.hsjang.saptest;

import java.util.HashMap;

public class Data  extends HashMap<String,Object>{

    private static final long serialVersionUID = 1L;

    public Data(){
        super();
    }


    public Data(String k, Object v){
        this();
        put(k,v);
    }

    public Data add(String k, Object v){
        put(k,v);
        return this;
    }

    public static Data of(String k, Object v){
        return new Data(k, v);
    }
    
}