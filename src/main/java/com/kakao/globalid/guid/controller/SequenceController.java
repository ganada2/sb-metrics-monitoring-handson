package com.kakao.globalid.guid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kakao.globalid.guid.dto.SequenceCommdateDto;
import com.kakao.globalid.guid.model.GuidModel;
import com.kakao.globalid.guid.model.SequenceModel;
import com.kakao.globalid.guid.service.SequenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "SequenceController", description = "Sequence API")
@RestController
public class SequenceController {

    @Autowired
    private SequenceService sequenceService;

    /* 시퀀스 생성 */
    @PostMapping("/api/v1/sequence")
    @Operation(summary = "Sequence 생성 API", description = "Sequence 생성 API")
    public SequenceModel Sequence(
        @RequestParam("guid") String guid,
        @RequestParam("last_work_layer") String last_work_layer ) throws Exception{
        
        SequenceModel sequenceModel = new SequenceModel();
        
        // 시퀀스 생성
        sequenceModel.sequence=sequenceService.CreateSequence(guid, last_work_layer);

        sequenceModel.setGlobalid(guid);
        sequenceModel.setSequence(sequenceModel.sequence);
        return sequenceModel;
    }

    /* 마지막 시퀀스 조회 */
    @GetMapping("/api/v1/cursequence")
    @Operation(summary = "Sequence 조회 API", description = "Sequence 조회 API")
    public SequenceModel selectSequence() throws Exception{
        
        SequenceModel sequenceModel = new SequenceModel();
        
        // 현재 시퀀스 조회
        sequenceModel.sequence=sequenceService.getCurrentSequence();

        return sequenceModel;
    }

    /* 영업일자 조회 */
    @GetMapping("/api/v1/commdate")
    @Operation(summary = "영업일자 조회 API", description = "영업일자 조회 API")
    public SequenceModel selectCommdate() throws Exception{
        
        SequenceModel sequenceModel = new SequenceModel();
        
        // 현재 영업일자 조회
        SequenceCommdateDto sequenceCommdateDto = sequenceService.getCommdate();
        
        sequenceModel.cur_date = sequenceCommdateDto.getCur_date();
        sequenceModel.update_progress = sequenceCommdateDto.getUpdate_progress();

        return sequenceModel;
    }
    
    /* 영업일자 변경, 시퀀스 초기화 */
    @GetMapping("/api/v1/resetcommdate")
    @Operation(summary = "영업일자, 시퀀스 초기화 API", description = "영업일자, 시퀀스 초기화 API")
    public void updateCommdate() throws Exception {

        sequenceService.rollingCommdate();

    }
    

}
