package com.abra.revaissue.dto;

import java.util.UUID;

public record CommentRequestDTO(String message, UUID issueId) {
}
