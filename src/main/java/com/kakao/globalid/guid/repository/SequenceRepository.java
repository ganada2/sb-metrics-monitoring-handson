package com.kakao.globalid.guid.repository;

import com.kakao.globalid.guid.dto.SequenceCommdateDto;

public interface SequenceRepository {
    
    
    String getNextSequence();
    String getCurrentSequence();
    void insertGuidExt(String sequence, String guid, String last_work_layer) throws Exception;

    SequenceCommdateDto selectCommdate();
    void updateCommdateP() throws Exception;
    void updateCommdateC() throws Exception;

    void resetSequence() throws Exception;
}
