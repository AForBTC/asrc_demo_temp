package com.iflytek.asrc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lattice implements Serializable {

    private Integer rl;
    private Integer spk;
    private String text;

}
