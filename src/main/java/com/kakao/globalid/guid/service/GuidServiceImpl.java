package com.kakao.globalid.guid.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kakao.globalid.guid.config.GuidConfig;
import com.kakao.globalid.guid.repository.GuidRepository;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@AllArgsConstructor
@Getter
@Service
public class GuidServiceImpl implements GuidService {

    @Autowired
    private GuidRepository guidRepository;
   // @Autowired
    
    private final GuidConfig guidConfig;

    /* Guid */
    private String guid; 		/* [30] Full GUID */

    /* Guid 구성 항목 */
    private String layer;       /* [2] 요청 시스템 Code */
	private String dateday;     /* [6] 일자 YYMMDD      */
	private String datetime;    /* [9] 시간 HHMISSsss milisec */
	private String worklayer;   /* [2] 발급 시스템 Code */
	private String pid;         /* [7] Process id 64bit    */
	private String serial;      /* [4] Guid 일련번호 0~9999 */
	
    @Override
    //public synchronized String CreateGuid(String layer) throws Exception{
    public String CreateGuid(String layer) throws Exception{

        /* Layer[2]
        * 요청 시스템 Code
        * Layer 입력 값 없을 경우 기본 값 "--"
        */
        if(StringUtils.isBlank(layer) || !isLengthMatch(layer, 2) ){
        // null & 길이 2자리 체크
        layer="--";
        }

        /* DateDay[6]
        * 일자 YYMMDD
        */
        LocalDateTime Date = LocalDateTime.now(); // 현재 시간 가져오기
        DateTimeFormatter DayFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        dateday = Date.format(DayFormatter);
    
        /* DateTime[9]
        * 시간 HHmmssSSS (1/1000sec)
        */        
        DateTimeFormatter TimeFormatter = DateTimeFormatter.ofPattern("HHmmssSSS");
        datetime = Date.format(TimeFormatter);

        /* WorkLayer[2]
        * 발급 시스템 Code
        * application.properties 의 env.layer code를 셋팅
        */
        worklayer = guidConfig.getWorkLayer();
        
        /* Pid[7]
        * Process id 64bit
        */
        pid = StringUtils.leftPad(Long.toString(ProcessHandle.current().pid()) , 7, "0");

        /* Serial[4]
        * 일련번호 4자리 0000~9999
        */        
        serial=guidRepository.getNextSerial();
        serial = StringUtils.leftPad(serial,4, "0");

        /* Guid 조립 */ 
        StringBuffer sb = new StringBuffer();
        guid = sb.append(layer)
                    .append(dateday)
                    .append(datetime)
                    .append(worklayer)
                    .append(pid)
                    .append(serial).toString();
        //System.out.println(guid);
        
        /*
        try{
            guidRepository.insertGuid(guid, worklayer);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new Exception("### DataBase insertGuid() fail!! ###");
        }*/
        /* insert dup 발생 시, 재 생성 */
        try {
            guidRepository.insertGuid(guid, worklayer);
        } catch (Exception e) {
             // 중복 예외가 발생하면 새로운 GUID 생성
            guid = this.CreateGuid(layer);
          //  System.out.println("### ReCreateGuid !!! ###");
        }

        return guid;
    }

    /* 입력 값 자릿수 체크 */
    public boolean isLengthMatch(String input, int length) {
        return input != null && input.length() == length;
    }
    
    /* Config load */
    @Autowired
    public GuidServiceImpl(GuidConfig guidConfig){
        this.guidConfig = guidConfig;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
