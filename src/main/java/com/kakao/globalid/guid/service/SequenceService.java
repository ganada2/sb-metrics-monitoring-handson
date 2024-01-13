package com.kakao.globalid.guid.service;

import com.kakao.globalid.guid.dto.SequenceCommdateDto;

public interface SequenceService {
    
    String CreateSequence(String guid, String last_work_layer) throws Exception;

    String getCurrentSequence();
    SequenceCommdateDto getCommdate();
    void rollingCommdate() throws Exception;
}
