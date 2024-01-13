package com.kakao.globalid.guid.repository;

public interface GuidRepository {

    void insertGuid(String guid, String work_layer) throws Exception;
    String getNextSerial();

    String selectGuidProgress(String guid) throws Exception; // globalid 테이블에서 update_progress,guid 조회

    void updateGuidInProgress(String guid) throws Exception; // globalid 테이블 update_progress 상태 업데이트 P : 진행중
    void updateGuidComplete(String guid) throws Exception; // globalid 테이블 update_progress 상태 업데이트 P : 진행중

}
