package com.marcinsz.backend.history;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "guarantee_history")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuaranteeHistory {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "guarantee_id")
    @JsonBackReference
    private Guarantee guarantee;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    private GuaranteeStatus status;
    private LocalDateTime changeTime;
    private String notes;
    private boolean positiveFeedback;
}
