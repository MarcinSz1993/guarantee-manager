package com.marcinsz.backend.guarantee;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marcinsz.backend.history.GuaranteeHistory;
import com.marcinsz.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

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
    private String notes;
    @Enumerated(EnumType.STRING)
    private Product kindOfProduct;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean sentExpirationMessage;
    @Enumerated(EnumType.STRING)
    private GuaranteeStatus guaranteeStatus;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    @OneToMany(mappedBy = "guarantee",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<GuaranteeHistory> guaranteeHistory;
}
