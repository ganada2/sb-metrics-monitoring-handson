package com.kakao.globalid.guid.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SequenceModel {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String globalid;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String sequence;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String cur_date;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String update_progress;

}
