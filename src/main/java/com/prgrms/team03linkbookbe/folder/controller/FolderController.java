package com.prgrms.team03linkbookbe.folder.controller;

import com.prgrms.team03linkbookbe.folder.dto.CreateFolderRequest;
import com.prgrms.team03linkbookbe.folder.dto.FolderDetailResponse;
import com.prgrms.team03linkbookbe.folder.dto.FolderIdResponse;
import com.prgrms.team03linkbookbe.folder.dto.FolderListResponse;
import com.prgrms.team03linkbookbe.folder.service.FolderService;
import com.prgrms.team03linkbookbe.user.entity.User;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FolderController {

    FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/api/folders")
    public ResponseEntity<FolderListResponse> readAllByUser(@AuthenticationPrincipal User user){
        FolderListResponse allByUser = folderService.getAllByUser(user);
        return ResponseEntity.ok(allByUser);
    }

    @GetMapping("/api/folders/{id}")
    public ResponseEntity<FolderDetailResponse> readDetail(@PathVariable Long id){
        FolderDetailResponse detail = folderService.detail(id);
        return ResponseEntity.ok(detail);
    }

    @PostMapping("/api/folders")
    public ResponseEntity<FolderIdResponse> create(@Valid @RequestBody CreateFolderRequest createFolderRequest, @AuthenticationPrincipal User user){
        FolderIdResponse create = folderService.create(user, createFolderRequest);
        return ResponseEntity.ok(create);
    }

    // TODO : 본인인증
    @PutMapping("/api/folder/{id}")
    public ResponseEntity<FolderIdResponse> update(@PathVariable Long id, @Valid @RequestBody CreateFolderRequest createFolderRequest, @AuthenticationPrincipal User user){
        FolderIdResponse update = folderService.update(id, createFolderRequest);
        return ResponseEntity.ok(update);
    }

    // TODO : 본인인증
    @DeleteMapping("/api/folder/{id}")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user){
        folderService.delete(id);
    }



}
