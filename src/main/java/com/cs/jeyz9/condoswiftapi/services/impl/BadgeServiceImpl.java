package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.dto.BadgeDTO;
import com.cs.jeyz9.condoswiftapi.dto.TableResponse;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Badge;
import com.cs.jeyz9.condoswiftapi.repository.BadgeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.cs.jeyz9.condoswiftapi.services.BadgeService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class BadgeServiceImpl implements BadgeService {
    private final BadgeRepository badgeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BadgeServiceImpl(BadgeRepository badgeRepository, ModelMapper modelMapper) {
        this.badgeRepository = badgeRepository;
        this.modelMapper = modelMapper;
    }
    
    @Override
    public String addedBadge(BadgeDTO request){
        if (request.getBadgeName() == null || request.getBadgeName().trim().isEmpty()) {
            throw new IllegalArgumentException("Badge name cannot be empty");
        }

        Optional<Badge> existingBadge = badgeRepository.findByBadgeNameIgnoreCase(request.getBadgeName().trim());
        if (existingBadge.isPresent()) {
            throw new WebException(HttpStatus.BAD_REQUEST, "Badge already exists: " + request.getBadgeName());
        }
        
        try {
            Badge badge = new Badge();
            badge.setBadgeName(request.getBadgeName());
            badgeRepository.save(badge);
            return "Added badge success: " + request.getBadgeName();
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while added badge" + e.getMessage());
        }
    };
    
    @Override
    public String updatedBadge(Long id, BadgeDTO request){
        if (request.getBadgeName() == null || request.getBadgeName().trim().isEmpty()) {
            throw new IllegalArgumentException("Badge name cannot be empty");
        }
        
        Optional<Badge> existingBadge = badgeRepository.findByBadgeNameIgnoreCase(request.getBadgeName().trim());
        if (existingBadge.isPresent()) {
            return "Badge already exists: " + request.getBadgeName();
        }
        
        try{
            Badge badge = badgeRepository.findById(id).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Badge not found by id: " + id));
            badge.setBadgeName(request.getBadgeName());
            badgeRepository.save(badge);
            return "Updated badge success: " + request.getBadgeName();
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while updated badge" + e.getMessage());
        }
    }
    
    @Override
    public TableResponse<BadgeDTO> showAllBadge(String keyword, Integer page, Integer size) throws IOException {
        try{
            List<Badge> badgeList = badgeRepository.findAll();
            Stream<Badge> stream = badgeList.stream();
            if(keyword != null && !keyword.trim().isEmpty()){
                stream = stream.filter(b -> b.getBadgeName().toLowerCase().contains(keyword.toLowerCase()));
            }
            
            List<BadgeDTO> badges = mapToDTO(stream.toList());
            Pageable pageable = PageRequest.of(page, size);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), badges.size());
            int total = badges.size();
            
            List<BadgeDTO> paginatedList = badges.subList(start, end);
            Page<BadgeDTO> badgePage = new PageImpl<>(paginatedList, pageable, badges.size());
            
            TableResponse<BadgeDTO> badgeTableResponse = new TableResponse<>();
            badgeTableResponse.setData(badgePage.getContent());
            badgeTableResponse.setPage(page);
            badgeTableResponse.setSize(size);
            badgeTableResponse.setTotal(total);
            return badgeTableResponse;
        }catch(Exception e) {
            throw new IOException("Error while searching for badge", e);
        }
    }
    
    @Override
    public String deletedBadge(Long id) {
        try {
            Badge badge = badgeRepository.findById(id).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Badge not found."));
            badgeRepository.delete(badge);
            return "Deleted badge success by id: " + id;
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while deleted badge" + e.getMessage());
        }
    }
    
    private List<BadgeDTO> mapToDTO(List<Badge> badgeList) {
        return badgeList.stream().map(badge -> modelMapper.map(badge, BadgeDTO.class)).toList();
    }
}
