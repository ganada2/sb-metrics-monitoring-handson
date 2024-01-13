package com.kakao.globalid.guid.controller;

import org.springframework.web.bind.annotation.RestController;
import com.kakao.globalid.guid.model.GuidModel;
import com.kakao.globalid.guid.repository.SequenceRepository;
import com.kakao.globalid.guid.service.GuidService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Tag(name = "GuidController", description = "Globalid API")
@Slf4j
@RestController
public class GuidController {

    @Autowired
    private GuidService guidService;
    @Autowired
    private SequenceRepository sequenceRepository;
    
    private final ExecutorService executor;
    private final Lock lock = new ReentrantLock();

    /* Guid 생성 */
    @PostMapping("/api/v1/guid")
    @Operation(summary = "Globalid API", description = "Globalid 생성 API")
    public CompletableFuture<Object> GuidThread(@RequestParam("layer") String layer) {
        
        GuidModel guidModel = new GuidModel();
        return CompletableFuture.supplyAsync(() -> {
                try {
                    lock.lock(); // 락 획득
                    guidModel.globalid=guidService.CreateGuid(layer);   // GUID 생성
                    
                    log.info("GUID = {}",guidModel.globalid);
                    return guidModel;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    lock.unlock(); // 락 해제
                }
                return guidModel;
                
            
        })
        .thenApply(globalId -> {
            return guidModel;
        }
        );
    }

    /* Guid 생성 */
    @PostMapping("/api/v1/guid2")
    @Operation(summary = "Globalid API", description = "Globalid 생성 API")
    public CompletableFuture<Object> GuidThread2(@RequestParam("layer") String layer) {
        
        GuidModel guidModel = new GuidModel();
        return CompletableFuture.supplyAsync(() -> {
                try {
                    lock.lock(); // 락 획득
                    guidModel.globalid=guidService.CreateGuid(layer);   // GUID 생성
                    //return guidService.CreateGuid(layer);
                    return guidModel;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    lock.unlock(); // 락 해제
                }
                return guidModel;
                
            
        }, executor);
    }

    /* Guid 생성 */
    @PostMapping("/api/v1/guid3")
    @Operation(summary = "Globalid API", description = "Globalid 생성 API")
    public GuidModel GuidThread3(@RequestParam("layer") String layer) throws Exception {
        
        GuidModel guidModel = new GuidModel();
        
        synchronized (this) {
            guidModel.globalid = guidService.CreateGuid(layer); 
        }
            
        return guidModel;
    }

    /* Guid 생성 */
    @PostMapping("/api/v1/guid4")
    @Operation(summary = "Globalid API", description = "Globalid 생성 API")
    public GuidModel GuidThread4(@RequestParam("layer") String layer) throws Exception {
        
        GuidModel guidModel = new GuidModel();
        
        Thread thread=Thread.startVirtualThread(()-> {
        try {
            guidModel.globalid = guidService.CreateGuid(layer);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        );
        thread.join();
        
        return guidModel;

    }

    @Autowired
    public GuidController(GuidService guidService, ExecutorService executorService) {
        this.guidService = guidService;
        this.executor = executorService;
    }

}