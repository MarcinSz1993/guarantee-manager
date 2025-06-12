package com.marcinsz.backend.history;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString(exclude = {"guarantee","user"})
@Entity
@Table(name = "guarantee_history")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuaranteeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "guarantee_history_seq")
    @SequenceGenerator(name = "guarantee_history_seq",allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "guarantee_id")
    @JsonBackReference("guarantee-reference")
    private Guarantee guarantee;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-reference")
    private User user;
    @Enumerated(EnumType.STRING)
    private GuaranteeStatus status;
    private LocalDateTime changeTime;
    private String notes;
    private boolean positiveFeedback;
}
