package com.ecommerce.socketgateway.modules.chat.message;

import com.ecommerce.socketgateway.modules.chat.message.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

	Page<MessageEntity> findByConversation_IdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

}
