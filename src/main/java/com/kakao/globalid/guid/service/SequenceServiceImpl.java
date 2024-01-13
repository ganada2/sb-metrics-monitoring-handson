package com.kakao.globalid.guid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakao.globalid.guid.dto.SequenceCommdateDto;
import com.kakao.globalid.guid.repository.GuidRepository;
import com.kakao.globalid.guid.repository.SequenceRepository;

@Service
public class SequenceServiceImpl implements SequenceService {
    
    @Autowired
    private SequenceRepository sequenceRepository;
    @Autowired
    private GuidRepository guidRepository;
    
    private String sequence;


    /* 시퀀스 생성  */
    @Transactional
    //public synchronized String CreateSequence(String guid, String last_work_layer) throws Exception{
    public String CreateSequence(String guid, String last_work_layer) throws Exception{
    

        // 0. comm_date update 여부 체크 for updateselectCommdate
        SequenceCommdateDto sequenceCommdateDto = sequenceRepository.selectCommdate();
            if(!sequenceCommdateDto.getUpdate_progress().equals("C") ){
            throw new Exception("### getUpdate_progress check fail!! ###");
        }

        // 1. if C 이면 처리 진행 select ~ forupdate
        String update_progress = guidRepository.selectGuidProgress(guid);
        //System.out.println("### DEBUG update_progress = "+update_progress);
        
        if ( update_progress.equals("C")){
            // System.out.println("### DEBUG update_progress eqaul !");
            
            // 2. globalid 테이블의 해당 guid 상태 변경 C-> P : 진행중
            try {
                guidRepository.updateGuidInProgress(guid);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("### DataBase updateGuidInProgress() fail!! ###");
            }

            // 3. seq 획득
            sequence = sequenceRepository.getNextSequence();
            
            // 4. globalid_ext 테이블 insert
            try {
                sequenceRepository.insertGuidExt(sequence, guid, last_work_layer);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("### DataBase insertGuidExt() fail!! ###");
            }

            // 5. globalid 테이블의 해당 guid 상태 변경 P -> C : 완료
            try {
                guidRepository.updateGuidComplete(guid);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("### DataBase updateGuidInProgress() fail!! ###");
            }

        }
        else{
            //System.out.println("### DEBUG update_progress check error, please retry !");
            throw new Exception("### Database CreateSequence() Fail - Sequence update_progress !!!");
        }
        
        return sequence;
    }

    /* 시퀀스 조회  */
    public String getCurrentSequence() {
        sequence = sequenceRepository.getCurrentSequence();
        return sequence;
    }

    /* 영업일자, 업데이트 상태 조회 */
    public SequenceCommdateDto getCommdate() {
        return sequenceRepository.selectCommdate();

    }
    /* 영업일자 변경 + 시퀀스 초기화 */
    @Scheduled(cron = "0 0 0 * * *") // 매일 00시
    //@Scheduled(cron = "0/5 * * * * *") // 매 05초
    public void rollingCommdate() throws Exception{
        // 영업일자 업데이트

        // 진행중 flag 변경
        sequenceRepository.updateCommdateP();

        // 시퀀스 초기화
        sequenceRepository.resetSequence();

        // 일자변경, 완료 flag 변경
        sequenceRepository.updateCommdateC();
        
    }
    

    
}