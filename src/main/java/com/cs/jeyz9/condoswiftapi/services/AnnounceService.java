package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceRequestDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceResponse;
import com.cs.jeyz9.condoswiftapi.dto.ShowAnnounceWithCategoryResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AnnounceService {
    Announce getAnnounceById(Long announceId);
    AnnounceRequestDTO addAnnounce(AnnounceDTO announce) throws WebException;
    AnnounceDTO editAnnounce(Long announceId, AnnounceDTO announce) throws WebException;
    AnnounceDetailsSelected getAnnounceDetailsById(Long announceId);
    ShowAnnounceWithCategoryResponse showAnnounceWithCategory() throws WebException;
    String deletedAnnounce(Long announceId);
    AnnounceResponse filterAnnounceWithAgen(String keyword, String type, String saleType, Integer bedroomCount, Double minPrice, Double maxPrice, Integer page, Integer size) throws IOException;
    AnnounceRequestDTO addAnnounceWithImage(AnnounceDTO announceDTO, List<MultipartFile> imageFile) throws WebException;
}
