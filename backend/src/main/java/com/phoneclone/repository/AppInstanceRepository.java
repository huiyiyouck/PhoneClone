package com.phoneclone.repository;

import com.phoneclone.entity.AppInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppInstanceRepository extends JpaRepository<AppInstance, Long> {
    List<AppInstance> findByAppId(Long appId);
}

