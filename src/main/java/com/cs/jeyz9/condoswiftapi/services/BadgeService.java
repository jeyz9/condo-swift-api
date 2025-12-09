package com.cs.jeyz9.condoswiftapi.services;

import com.cs.jeyz9.condoswiftapi.dto.BadgeDTO;
import com.cs.jeyz9.condoswiftapi.dto.TableResponse;

import java.io.IOException;
import java.util.List;

public interface BadgeService {
    String addedBadge(BadgeDTO request);
    String updatedBadge(Long id, BadgeDTO request);
    TableResponse<BadgeDTO> showAllBadge(String keyword, Integer page, Integer size) throws IOException;
    String deletedBadge(Long id);
    List<BadgeDTO> getAllBadges();
    String addAnnounceBadge(Long announceId, Long badgeId);
    String removeBadgeFromAnnounce(Long announceId, Long badgeId);
}
