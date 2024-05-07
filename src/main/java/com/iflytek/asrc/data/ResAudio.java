package com.iflytek.asrc.data;

import lombok.Data;

@Data
public class ResAudio {
    private String aid;
    private Integer bits;
    private Integer chnl;
    private Integer encoding;
    private Integer offset;
    private Integer rate;
    private Integer spnk;
    private String uri;

}
