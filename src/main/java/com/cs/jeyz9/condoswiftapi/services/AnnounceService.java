package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.AnnounceDTO;
import com.cs.jeyz9.condoswiftapi.dto.AnnounceDetailsSelected;
import com.cs.jeyz9.condoswiftapi.dto.ShowAnnounceWithCategoryResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Announce;

public interface AnnounceService {
    Announce getAnnounceById(Long announceId);
    AnnounceDTO addAnnounce(AnnounceDTO announce);
    AnnounceDetailsSelected getAnnounceDetailsById(Long announceId);
    ShowAnnounceWithCategoryResponse showAnnounceWithCategory() throws WebException;
}
