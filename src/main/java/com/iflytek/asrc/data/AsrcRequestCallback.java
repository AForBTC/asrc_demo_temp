package com.iflytek.asrc.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsrcRequestCallback implements Serializable {

    private State state;

    private RequestCall body;

}
