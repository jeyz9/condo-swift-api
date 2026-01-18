package com.cs.jeyz9.condoswiftapi.services;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AnnounceService {
    AnnounceDTO addAnnounceWithImage(AnnounceRequestDTO announceDTO, List<MultipartFile> imageFile, String email) throws WebException;
    AnnounceDTO updateAnnounceWithImage(Long announceId, AnnounceRequestDTO announceDTO, List<MultipartFile> imageFiles, String email);
    AnnounceDetailsSelected getAnnounceDetailsById(Long announceId);
    ShowAnnounceWithCategoryResponse showAnnounceWithCategory() throws WebException;
    String deletedAnnounce(Long announceId, String email);
    AnnounceResponse filterAnnounceWithAgen(String keyword, String type, String station, String province, String saleType, Integer bedroomCount, String badge, Double minPrice, Double maxPrice, Integer page, Integer size) throws IOException;
    TableResponse<AnnounceApproveDTO> showAllAnnouncePending(String keyword, Integer page, Integer size) throws IOException;
    TableResponse<AnnounceApproveDTO> showAllAnnounceApprove(String keyword, Integer page, Integer size) throws IOException;
    TableResponse<AnnounceApproveDTO> showAllAnnounceHistory(String keyword, Integer page, Integer size) throws IOException;
    String approveAnnounce(Long announceId, String officialEmail);
    String rejectAnnounce(Long announceId, String officialEmail, RejectAnnounceDTO reject);
    TableResponse<ShowAllAnnounceBadgesDTO> showAllAnnounceBadgesSelector(String keyword, String badges, Integer page, Integer size) throws IOException;
    List<AnnounceDraftDTO> showAllAnnounceDraft(String email);
    AnnouncePendingDetailsSelectedDTO getAnnouncePendingDetailsById(Long announceId);
    AnnounceDetailsSelected getAnnounceDetailsByAgent(String email, Long announceId);
}
