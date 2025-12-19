package com.abra.revaissue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.repository.CommentRepository;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
