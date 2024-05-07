package com.iflytek.asrc.data;

import lombok.Data;

import java.util.List;

@Data
public class RequestCall {
    private State state;
    private String aid;
    private List<Lattices> lattices;

}
