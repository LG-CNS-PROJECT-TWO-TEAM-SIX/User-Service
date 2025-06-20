package com.example.User_Service.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(UserInterestId.class)
@Table(name = "user_interest")
public class UserInterest {
    @Id
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "interest_id")
    private Interest interest;
    
}
