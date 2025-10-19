package com.example.datn.Service;


import com.example.datn.DTO.TrangMuaHang.ConversationDTO;
import com.example.datn.DTO.TrangMuaHang.MessageDTO;
import com.example.datn.DTO.TrangMuaHang.SendMessageRequest;
import com.example.datn.DTO.TrangMuaHang.UserDTO;
import com.example.datn.Model.Message;
import com.example.datn.Model.Users;
import com.example.datn.Repository.MessageRepository;

import com.example.datn.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UsersRepository userRepository;
    private final WebSocketService webSocketService;

    @Transactional
    public MessageDTO sendMessage(Integer senderId, SendMessageRequest request) {
        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Users receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setIsRead(false);

        Message savedMessage = messageRepository.save(message);

        MessageDTO messageDTO = convertToDTO(savedMessage);

        // Send via WebSocket
        webSocketService.sendMessageToUser(receiver.getId(), messageDTO);

        log.info("Message sent from user {} to user {}", senderId, request.getReceiverId());

        return messageDTO;
    }

    public MessageListResponse getConversation(Integer userId1, Integer userId2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findConversationBetweenUsers(userId1, userId2, pageable);

        List<MessageDTO> messages = messagePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new MessageListResponse(
                messages,
                messagePage.getTotalPages(),
                messagePage.getTotalElements(),
                messagePage.getNumber()
        );
    }

    @Transactional
    public void markMessagesAsRead(Integer senderId, Integer receiverId) {
        List<Message> unreadMessages = messageRepository.findUnreadMessages(receiverId);

        for (Message message : unreadMessages) {
            if (message.getSender().getId().equals(senderId)) {
                message.setIsRead(true);
            }
        }

        messageRepository.saveAll(unreadMessages);
        log.info("Marked messages as read between users {} and {}", senderId, receiverId);
    }

    public Long getUnreadMessageCount(Integer userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    public List<ConversationDTO> getAdminConversations(Integer adminId) {
        List<Users> usersWithConversations = messageRepository.findUsersWithConversations(adminId);

        return usersWithConversations.stream()
                .map(user -> {
                    Pageable latestMessage = PageRequest.of(0, 1);
                    Page<Message> messagePage = messageRepository.findConversationBetweenUsers(
                            adminId, user.getId(), latestMessage);

                    ConversationDTO conversation = new ConversationDTO();
                    conversation.setUserId(user.getId());
                    conversation.setUserDisplayName(user.getTenHienThi());

                    if (!messagePage.getContent().isEmpty()) {
                        Message latest = messagePage.getContent().get(0);
                        conversation.setLastMessage(latest.getContent());
                        conversation.setLastMessageTime(latest.getTimestamp());
                    }

                    Long unreadCount = messageRepository.countUnreadMessagesBetweenUsers(user.getId(), adminId);
                    conversation.setUnreadCount(unreadCount.intValue());

                    return conversation;
                })
                .collect(Collectors.toList());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUsers().stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getCurrentUserInfo(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertUserToDTO(user);
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setIsRead(message.getIsRead());
        dto.setSenderName(message.getSender().getTenHienThi());
        dto.setReceiverName(message.getReceiver().getTenHienThi());
        return dto;
    }

    private UserDTO convertUserToDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setTenHienThi(user.getTenHienThi());
        dto.setUsername(user.getUsername());
        dto.setHoTen(user.getHoTen());
        dto.setGioiTinh(user.getGioiTinh());
        dto.setEmail(user.getEmail());
        dto.setSoDienThoai(user.getSoDienThoai());
        dto.setDiaChiGiaoHang(user.getDiaChiGiaoHang());
        dto.setRoleName(user.getRole().getRoleName());
        return dto;
    }
}