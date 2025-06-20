package com.example.User_Service.domain.repository;


import com.example.User_Service.domain.model.Interest;
import com.example.User_Service.domain.model.User;
import com.example.User_Service.domain.model.UserInterest;
import com.example.User_Service.domain.model.UserInterestId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId>{
    List<UserInterest> findByUserId(Long userId);

    boolean existsByUserAndInterest(User user, Interest interest);
    void deleteByUserAndInterest(User user, Interest interest);

    void deleteByUser(User user);
}
