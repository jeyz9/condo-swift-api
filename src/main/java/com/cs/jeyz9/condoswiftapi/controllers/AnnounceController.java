package com.cs.jeyz9.condoswiftapi.controllers;

import com.cs.jeyz9.condoswiftapi.dto.AnnounceApproveDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDraftDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnouncePendingDetailsSelectedDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceResponse;
import com.cs.jeyz9.condoswiftapi.dto.RejectAnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAllAnnounceBadgesDTO;
import com.cs.jeyz9.condoswiftapi.dto.ShowAnnounceWithCategoryResponse;
import com.cs.jeyz9.condoswiftapi.dto.TableResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.services.AnnounceImageService;
import com.cs.jeyz9.condoswiftapi.services.AnnounceService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/announces")
public class AnnounceController {
    private final AnnounceService announceService;
    private final AnnounceImageService announceImageService;

    @Autowired
    public AnnounceController(AnnounceService announceService, AnnounceImageService announceImageService) {
        this.announceService = announceService;
        this.announceImageService = announceImageService;
    }
    
    @PostMapping(value = "/uploadImages/{announceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAnnounceImages(
            @PathVariable Long announceId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws WebException {

        announceImageService.saveImages(announceId, images);

        return ResponseEntity.ok("Images uploaded successfully");
    }
    
    @PutMapping(value = "/editAnnounce/{announceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnnounceDTO> editAnnounce(@PathVariable Long announceId, 
                                                    @Valid @RequestPart("announce") AnnounceRequestDTO announceDTO, 
                                                    @Parameter(description = "List of images") @RequestPart(value = "images", required = false) List<MultipartFile> imageFile,
                                                    Principal principal) throws WebException {
        AnnounceDTO announce = announceService.updateAnnounceWithImage(announceId, announceDTO, imageFile, principal.getName());
        return new ResponseEntity<>(announce, HttpStatus.OK);
    }
    
    @GetMapping(value = "/showAnnounceDetails/{announceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnounceDetailsSelected> showAnnounceDetails(@PathVariable Long announceId) {
        return new ResponseEntity<> (announceService.getAnnounceDetailsById(announceId), HttpStatus.OK);
    }

    @GetMapping(value = "/showAnnouncePendingDetails/{announceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnouncePendingDetailsSelectedDTO> showAnnouncePendingDetails(@PathVariable Long announceId) {
        return new ResponseEntity<> (announceService.getAnnouncePendingDetailsById(announceId), HttpStatus.OK);
    }
    
    @GetMapping(value = "/showAnnounceWithCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShowAnnounceWithCategoryResponse> showAnnounceWithCategory() throws WebException {
        return new ResponseEntity<>(announceService.showAnnounceWithCategory(), HttpStatus.OK);
    }
    
    @GetMapping(value = "/filterAnnounceWithAgent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnounceResponse> filterAnnounce(@RequestParam(defaultValue = "", required = false) String keyword,
                                                           @RequestParam(defaultValue = "", required = false) String type,
                                                           @RequestParam(defaultValue = "", required = false) String station,
                                                           @RequestParam(defaultValue = "", required = false) String province,
                                                           @RequestParam(defaultValue = "", required = false) String saleType,
                                                           @RequestParam(defaultValue = "", required = false) Integer bedroomCount,
                                                           @RequestParam(defaultValue = "", required = false) String badge,
                                                           @RequestParam(defaultValue = "", required = false) Double minPrice,
                                                           @RequestParam(defaultValue = "", required = false) Double maxPrice,
                                                           @RequestParam(defaultValue = "0", required = false) Integer page,
                                                           @RequestParam(defaultValue = "10", required = false) Integer size) throws IOException {
        return new ResponseEntity<>(announceService.filterAnnounceWithAgen(keyword, type, station, province, saleType, bedroomCount, badge, minPrice, maxPrice, page, size), HttpStatus.OK);
    }
    
    @DeleteMapping("/deletedAnnounceImage/{announceImageId}")
    public ResponseEntity<String> deletedAnnounceImage(@PathVariable Long announceImageId){
        announceImageService.deleteImageById(announceImageId);
        return new ResponseEntity<>("Announce image is deleted by Id: " + announceImageId, HttpStatus.OK);
    }
    
    @DeleteMapping("/deletedAnnounce/{announceId}")
    public ResponseEntity<String> deletedAnnounce(@PathVariable Long announceId, Principal principal) {
        return new ResponseEntity<>(announceService.deletedAnnounce(announceId, principal.getName()), HttpStatus.OK);
    }

    @PostMapping(value = "/addAnnounceWithImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnnounceDTO> addAnnounceWithImage(
            @Parameter(description = "Announce data") @Valid @RequestPart("announce") AnnounceRequestDTO announce,
            @Parameter(description = "List of images") @RequestPart(value = "images", required = false) List<MultipartFile> imageFile,
            Principal principal
    ) throws WebException {
        return new ResponseEntity<>(announceService.addAnnounceWithImage(announce, imageFile, principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/showAllAnnouncePendingByAdmin")
    public ResponseEntity<TableResponse<AnnounceApproveDTO>> showAllAnnouncePendingByAdmin(@RequestParam(defaultValue = "", required = false) String keyword,
                                                                                           @RequestParam(defaultValue = "0", required = false) Integer page,
                                                                                           @RequestParam(defaultValue = "10", required = false) Integer size) throws IOException {
        return ResponseEntity.ok(announceService.showAllAnnouncePending(keyword, page, size));
    }
    
    @GetMapping("/showAllAnnounceApproveByAdmin")
    public ResponseEntity<TableResponse<AnnounceApproveDTO>> showAllAnnounceApproveByAdmin(@RequestParam(defaultValue = "", required = false) String keyword,
                                                                                           @RequestParam(defaultValue = "0", required = false) Integer page,
                                                                                           @RequestParam(defaultValue = "10", required = false) Integer size) throws IOException {
        return ResponseEntity.ok(announceService.showAllAnnounceApprove(keyword, page, size));
    }

    @GetMapping("/showAllAnnounceHistoryByAdmin")
    public ResponseEntity<TableResponse<AnnounceApproveDTO>> showAllAnnounceHistoryByAdmin(@RequestParam(defaultValue = "", required = false) String keyword,
                                                                                           @RequestParam(defaultValue = "0", required = false) Integer page,
                                                                                           @RequestParam(defaultValue = "10", required = false) Integer size) throws IOException {
        return ResponseEntity.ok(announceService.showAllAnnounceHistory(keyword, page, size));
    }
    
    @PutMapping("/approveAnnounce/{announceId}")
    public ResponseEntity<String> approveAnnounce(@PathVariable("announceId") Long announceId, Principal principal) {
        return new ResponseEntity<>(announceService.approveAnnounce(announceId, principal.getName()), HttpStatus.OK);
    }
    
    @PutMapping("/rejectAnnounce/{announceId}")
    public ResponseEntity<String> rejectAnnounce(@PathVariable("announceId") Long announceId, @RequestBody RejectAnnounceDTO reject, Principal principal) {
        return new ResponseEntity<>(announceService.rejectAnnounce(announceId, principal.getName(), reject), HttpStatus.OK);
    }
    
    @GetMapping("/showAllAnnounceBadges")
    public ResponseEntity<TableResponse<ShowAllAnnounceBadgesDTO>> showAllAnnounceBadges(@RequestParam(required = false) String keyword, @RequestParam(required = false) String badges, @RequestParam(defaultValue = "0", required = false) Integer page, @RequestParam(defaultValue = "10", required = false) Integer size) throws IOException {
        return new ResponseEntity<>(announceService.showAllAnnounceBadgesSelector(keyword, badges, page, size), HttpStatus.OK);
    }
    
    @GetMapping("/showAllAnnounceDraft")
    public ResponseEntity<List<AnnounceDraftDTO>> showAllAnnounceDraft(Principal principal) {
        return new ResponseEntity<>(announceService.showAllAnnounceDraft(principal.getName()), HttpStatus.OK);
    }
}
