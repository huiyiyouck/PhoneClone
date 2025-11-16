package com.phoneclone.repository;

import com.phoneclone.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {
    List<App> findByUserId(Long userId);
    boolean existsByUserIdAndPackageName(Long userId, String packageName);
}

