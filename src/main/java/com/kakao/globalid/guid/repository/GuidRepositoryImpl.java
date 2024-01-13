package com.kakao.globalid.guid.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

//import org.springframework.retry.annotation.Backoff;
//import org.springframework.retry.annotation.Retryable;


@Repository
public class GuidRepositoryImpl implements GuidRepository{

    @Autowired
	JdbcTemplate jdbcTemplate;

    StringBuilder sb = new StringBuilder();

    /* GUID 테이블 입력 */
    //@Retryable(maxAttempts=3, backoff=@Backoff(100), value={ Exception.class })
    public void insertGuid(String guid, String work_layer) throws Exception {

    String sql="insert into globalid (guid, work_layer) values ('"+guid+"','"+work_layer+"');";

        try {
            jdbcTemplate.execute(sql);
            
        } catch (DataIntegrityViolationException e) {
          //  ex.printStackTrace();
            throw new Exception("### Database insertGuid() Fail - DataIntegrityViolationException!!!");
        } catch (Exception e) {
          //  ex.printStackTrace();
            throw new Exception("### Database insertGuid() Fail !!!");
        }
    }

    /* 4자리 Guid 시리얼 획득 */
    public synchronized String getNextSerial() {
        // guid 내 [4]자리 serial, sequence next 값 가져오기 
        Long l_seq = jdbcTemplate.queryForObject("select SERIAL_SEQ.nextval as SEQ", Long.class);
        String seq=Long.toString(l_seq);
        seq=StringUtils.leftPad(seq, 4, "0");

        return seq;
	}

    /* globalid 테이블 update_progress 조회 */
    /* H2 database lock time 2sec , transaction  */
    public String selectGuidProgress(String guid) throws Exception{

        String sql="SELECT UPDATE_PROGRESS FROM GLOBALID where GUID='"+guid+"' FOR UPDATE;";

        String update_progress;
        try {
        update_progress = jdbcTemplate.queryForObject(sql, String.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("### Database selectGuidProgress() Fail !!!");
        }

        return update_progress;
    }

    /* globalid 테이블 update_progress 상태 업데이트 P : 진행중 */
    public void updateGuidInProgress(String guid) throws Exception{

    String sql="UPDATE GLOBALID set UPDATE_PROGRESS='P' where GUID='"+guid+"';";

    try {
        jdbcTemplate.execute(sql);
    } catch (Exception e) {
        throw new Exception("### Database updateGuidInProgress() Fail !!!");
    }

    }
    /* globalid 테이블 update_progress 상태 업데이트 C : 완료 */
    public void updateGuidComplete(String guid) throws Exception{

    String sql="UPDATE GLOBALID set UPDATE_PROGRESS='C' where GUID='"+guid+"';";
    
    try {
        jdbcTemplate.execute(sql);
    } catch (Exception e) {
        throw new Exception("### Database updateGuidInProgress() Fail !!!");
    }

    }

}

