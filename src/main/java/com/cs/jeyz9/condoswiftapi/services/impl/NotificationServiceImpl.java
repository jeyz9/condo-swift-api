package com.cs.jeyz9.condoswiftapi.services.impl;

import com.cs.jeyz9.condoswiftapi.dto.NotificationDTO;
import com.cs.jeyz9.condoswiftapi.dto.SendNotificationDTO;
import com.cs.jeyz9.condoswiftapi.exceptions.WebException;
import com.cs.jeyz9.condoswiftapi.models.Notification;
import com.cs.jeyz9.condoswiftapi.models.User;
import com.cs.jeyz9.condoswiftapi.repository.NotificationRepository;
import com.cs.jeyz9.condoswiftapi.repository.UserRepository;
import com.cs.jeyz9.condoswiftapi.services.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }
    
    @Override
    public String sendNotification(SendNotificationDTO notification) {
        try{
            User user = userRepository.findById(notification.getUserId()).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            Notification notify = Notification.builder()
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .user(user)
                    .createdDate(LocalDateTime.now())
                    .expiredDate(LocalDateTime.now().plusDays(7))
                    .is_read(false)
                    .build();
            notificationRepository.save(notify);
            return "Send message success.";
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    public List<NotificationDTO> showAllNotificationSelectedByUserId(Long userId){
        try{
            User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedDateDesc(user.getId());
            return mapToDTO(notifications);
        }catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    public NotificationDTO showNotificationDetailsSelected(Long userId, Long notifyId){
        try {
            Notification notification = notificationRepository.findById(notifyId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Notification not found."));
            User user = userRepository.findById(userId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "User not found."));
            if (!notification.getUser().getId().equals(user.getId())) {
                throw new WebException(HttpStatus.FORBIDDEN, "This User id didn't owner.");
            }
            if(!notification.getIs_read()){
                notification.setIs_read(true);
                notificationRepository.save(notification);
            }
            NotificationDTO response = modelMapper.map(notification, NotificationDTO.class);
            response.setIsRead(notification.getIs_read());
            response.setCreatedDate(notification.getCreatedDate());
            return response;
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    @Override
    public String deleteNotification(Long notifyId){
        try {
            Notification notification = notificationRepository.findById(notifyId).orElseThrow(() -> new WebException(HttpStatus.NOT_FOUND, "Notification not found."));
            notificationRepository.delete(notification);
            return "Deleted notification success";
        } catch (Exception e) {
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    
    private List<NotificationDTO> mapToDTO(List<Notification> notifications) {
        return notifications.stream().map(notify -> {
            NotificationDTO response = modelMapper.map(notify, NotificationDTO.class);
            response.setIsRead(notify.getIs_read());
            return response;
        }
        ).toList();
    }
}
