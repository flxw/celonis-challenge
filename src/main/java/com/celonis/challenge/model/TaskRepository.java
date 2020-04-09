package com.celonis.challenge.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByState(Task.STATE state);

    @Transactional
    @Modifying
    @Query("UPDATE Task SET state=:state WHERE id=:taskId")
    void setStateFor(@Param("state") Task.STATE state, @Param("taskId") String taskId);

    @Transactional
    @Modifying
    @Query("UPDATE Task SET progress=:progress WHERE id=:taskId")
    void setProgressFor(@Param("progress") double progress, @Param("taskId") String taskId);
}
