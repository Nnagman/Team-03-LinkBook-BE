package com.prgrms.team03linkbookbe.comment.repository;

import com.prgrms.team03linkbookbe.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select distinct c from Comment c join fetch c.folder f")
    List<Comment> findCommentFetchJoinByFolderId(@Param("folderId") Long folderId);
}