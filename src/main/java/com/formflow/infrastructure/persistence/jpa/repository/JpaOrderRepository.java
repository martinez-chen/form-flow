package com.formflow.infrastructure.persistence.jpa.repository;

import com.formflow.infrastructure.persistence.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    
    List<OrderEntity> findByCreatorId(Long creatorId);
    
    List<OrderEntity> findByAssigneeId(Long assigneeId);
    
    List<OrderEntity> findByGroupId(Long groupId);
    
    List<OrderEntity> findByTeamId(Long teamId);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status ORDER BY o.createdAt DESC")
    List<OrderEntity> findByStatus(@Param("status") String status);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.priority = :priority ORDER BY o.createdAt DESC")
    List<OrderEntity> findByPriority(@Param("priority") String priority);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.category = :category ORDER BY o.createdAt DESC")
    List<OrderEntity> findByCategory(@Param("category") String category);
}