package com.marcinsz.backend.guarantee;

import com.marcinsz.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Setter
@Table(name = "guarantee")
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Guarantee {

    @Id
    @GeneratedValue
    private Long id;

    private String brand;

    private String model;

    private String documentUrl;

    @Lob
    private String notes;

    @Enumerated(EnumType.STRING)
    private Device kindOfDevice;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private GuaranteeStatus guaranteeStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
