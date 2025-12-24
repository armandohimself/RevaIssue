package com.abra.revaissue.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentDTO(
        UUID commentId,
        String message,
        Instant time,
        String username) {
}
