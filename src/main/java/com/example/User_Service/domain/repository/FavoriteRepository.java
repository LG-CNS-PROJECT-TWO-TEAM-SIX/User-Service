package com.example.User_Service.domain.repository;

import com.example.User_Service.domain.model.Favorite;
import com.example.User_Service.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>{
    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserAndNewsLink(User user, String newsLink);

    void deleteByUser(User user);
}
