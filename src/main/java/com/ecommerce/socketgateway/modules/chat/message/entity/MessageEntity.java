package com.ecommerce.socketgateway.modules.chat.message.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "conversation_id", nullable = false)
	private com.ecommerce.socketgateway.modules.chat.conversation.entity.ConversationEntity conversation;

	@Column(name = "sender_id", nullable = false, length = 128)
	private String senderId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(name = "client_message_id", length = 128)
	private String clientMessageId;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

}
