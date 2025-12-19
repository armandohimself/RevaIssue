package com.abra.RevaIssue.service;

import com.abra.RevaIssue.entity.Comment;
import com.abra.RevaIssue.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
