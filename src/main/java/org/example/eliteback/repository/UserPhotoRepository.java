package org.example.eliteback.repository;

import org.example.eliteback.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    List<UserPhoto> findByUserIdOrderBySortOrderAsc(Long userId);
    void deleteByUserId(Long userId);
}
