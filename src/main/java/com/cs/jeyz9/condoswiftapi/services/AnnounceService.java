package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.ShowAnnounceWithCategoryResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;

import java.util.List;

public interface AnnounceService {
    Announce getAnnounceById(Long announceId);
    AnnounceDTO addAnnounce(AnnounceDTO announce) throws WebException;
    AnnounceDTO editAnnounce(Long announceId, AnnounceDTO announce) throws WebException;
    AnnounceDetailsSelected getAnnounceDetailsById(Long announceId);
    ShowAnnounceWithCategoryResponse showAnnounceWithCategory() throws WebException;
    String deletedAnnounce(Long announceId);
    List<AnnounceDTO> filterAnnounceWithAgen(String keyword, Integer page, Integer size);
}
