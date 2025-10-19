package com.example.datn.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "senderId", nullable = false)
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiverId", nullable = false)
    private Users receiver;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();

    private Boolean isRead = false;
}
