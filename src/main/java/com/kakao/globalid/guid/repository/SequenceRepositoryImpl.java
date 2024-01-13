package com.kakao.globalid.guid.repository;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kakao.globalid.guid.dto.SequenceCommdateDto;

@Repository
public class SequenceRepositoryImpl implements SequenceRepository {
    
    @Autowired
	JdbcTemplate jdbcTemplate;

    /* 10자리 시퀀스 획득 */
    public String getNextSequence() {
        
        Long l_seq = jdbcTemplate.queryForObject("select GLOBALID_SEQ.nextval as SEQ", Long.class);
        String seq=Long.toString(l_seq);
        seq=StringUtils.leftPad(seq, 10, "0");
        
        return seq;
    }

    /* 10자리 시퀀스 조회 */
    public String getCurrentSequence() {
    
        Long l_seq = jdbcTemplate.queryForObject("select GLOBALID_SEQ.currval as SEQ", Long.class);
        String seq=Long.toString(l_seq);
        seq=StringUtils.leftPad(seq, 10, "0");
        
        return seq;
    }

    /* 영업일자, 업데이트 상태 조회 */
    public SequenceCommdateDto selectCommdate(){

        Map<String, Object> rs = jdbcTemplate.queryForMap("SELECT CUR_DATE, UPDATE_PROGRESS FROM COMM_DATE for update");
        String cur_date = (String) rs.get("CUR_DATE");
        String update_progress = (String) rs.get("UPDATE_PROGRESS");

        SequenceCommdateDto sequenceCommdateDto = new SequenceCommdateDto();
        sequenceCommdateDto.setCur_date(cur_date);
        sequenceCommdateDto.setUpdate_progress(update_progress);

        return sequenceCommdateDto;
    }

    /* 영업일자 변경 */
    public void updateCommdateP() throws Exception{
        // 업데이트 상태 변경
        String sql="UPDATE COMM_DATE set UPDATE_PROGRESS='P', last_updated = CURRENT_TIMESTAMP(3) where UPDATE_PROGRESS='C';";
    try {
        jdbcTemplate.execute(sql);

    } catch (Exception e) {
        throw new Exception("### Database updateCommdate() Fail !!!");
    }
    }

    public void updateCommdateC() throws Exception{
    
        // 영업일자 업데이트
        String sql2="UPDATE COMM_DATE SET before_date = FORMATDATETIME(CURRENT_DATE - 1, 'yyMMdd'), \r\n" + //
                "cur_date = FORMATDATETIME(CURRENT_DATE, 'yyMMdd'), \r\n" + //
                "after_date = FORMATDATETIME(CURRENT_DATE + 1, 'yyMMdd');";
        
        // 완료 상태 변경
        String sql3="UPDATE COMM_DATE set UPDATE_PROGRESS='C', last_updated = CURRENT_TIMESTAMP(3) where UPDATE_PROGRESS='P';";
    try {
        jdbcTemplate.execute(sql2);
        jdbcTemplate.execute(sql3);

    } catch (Exception e) {
        throw new Exception("### Database updateCommdateC() Fail !!!");
    }
    }

    /* 시퀀스 초기화 */
    @Transactional
    public void resetSequence() throws Exception{
        String sql="select GLOBALID_SEQ.nextval ";
        String sql1="alter sequence GLOBALID_SEQ increment by ('-'||select GLOBALID_SEQ.currval )";
	    String sql2="select GLOBALID_SEQ.nextval ";
	    String sql3="alter sequence GLOBALID_SEQ increment by 1";
       

    try {
        jdbcTemplate.execute(sql);
        jdbcTemplate.execute(sql1);
	    jdbcTemplate.execute(sql2);
	    jdbcTemplate.execute(sql3);
        
    } catch (Exception e) {
        throw new Exception("### Database resetSequence() Fail !!!");
    }
    }


    /* globalid_ext 테이블 입력 */
    @Retryable(maxAttempts=3, backoff=@Backoff(100), value={ Exception.class })
    public void insertGuidExt(String sequence, String guid, String last_work_layer) throws Exception {

        String sql="insert into globalid_ext (sequence, guid, work_layer) values ('"+sequence+"','"+guid+"','"+last_work_layer+"');";
        //System.out.println(sql);

        try {
            jdbcTemplate.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("### Database insertGuidExt() Fail !!!");
        }
    }



}