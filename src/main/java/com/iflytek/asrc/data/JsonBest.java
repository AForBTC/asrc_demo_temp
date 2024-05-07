package com.iflytek.asrc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonBest {

    private Integer bg;
    private Integer ed;
    private Double eg;
    private Integer rl;
    private Boolean ls;
    private String msgtype;
    private Integer sn;
    private Integer pa;
    private String sc;
    private List<Ws> ws;

}
