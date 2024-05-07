package com.iflytek.asrc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lattices implements Serializable {

    private Integer lid;
    private Integer spk;
    private Integer begin;
    private Integer end;
    private Double sc;
    private String json_cn1best;
    private String onebest;

}
