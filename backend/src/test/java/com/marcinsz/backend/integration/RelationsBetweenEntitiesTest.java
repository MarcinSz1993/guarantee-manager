package com.marcinsz.backend.integration;

import com.marcinsz.backend.guarantee.Product;
import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeRepository;
import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.history.GuaranteeHistory;
import com.marcinsz.backend.history.GuaranteeHistoryRepository;
import com.marcinsz.backend.user.Role;
import com.marcinsz.backend.user.User;
import com.marcinsz.backend.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@DataJpaTest
public class RelationsBetweenEntitiesTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>("postgres:16-alpine3.19");

    @Autowired
    private GuaranteeRepository guaranteeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuaranteeHistoryRepository guaranteeHistoryRepository;

    @BeforeEach
    void setUp(){
        assertTrue(postgreSQLContainer.isRunning());
        userRepository.deleteAll();
        guaranteeRepository.deleteAll();
        guaranteeHistoryRepository.deleteAll();
    }



    @Test
    void shouldValidateConnection(){
        assertTrue(postgreSQLContainer.isCreated());
        assertTrue(postgreSQLContainer.isRunning());
        log.info("Jdbc url: {}",postgreSQLContainer.getJdbcUrl());
        log.info("Username: {}", postgreSQLContainer.getUsername());
        log.info("Password: {}",postgreSQLContainer.getPassword());
        log.info("Docker image: {}",postgreSQLContainer.getDockerImageName());
        log.info("Database name: {}",postgreSQLContainer.getDatabaseName());
    }

    @Test
    @Transactional
    void checkIfGuaranteeAndGuaranteeHistoriesIsRemovedAfterDeletingUser(){
        User user = createTestUser();
        User savedUser = userRepository.save(user);

        Guarantee testGuarantee1 = createTestGuarantee1(user);
        savedUser.getGuarantees().add(testGuarantee1);
        Guarantee savedTestGuarantee1 = guaranteeRepository.save(testGuarantee1);

        GuaranteeHistory testGuaranteeHistory1 = createTestGuaranteeHistory1(savedTestGuarantee1, savedUser);
        GuaranteeHistory savedTestGuaranteeHistory1 = guaranteeHistoryRepository.save(testGuaranteeHistory1);

        savedTestGuarantee1.getGuaranteeHistory().add(savedTestGuaranteeHistory1);
        guaranteeRepository.save(savedTestGuarantee1);

        userRepository.delete(savedUser);
        List<Guarantee> allUserGuarantees = guaranteeRepository.findAll();
        List<GuaranteeHistory> guaranteeHistories = guaranteeHistoryRepository.findAll();
        assertEquals(0, allUserGuarantees.size());
        assertEquals(0, guaranteeHistories.size());
        assertEquals(Optional.empty(), userRepository.findById(savedUser.getId()));
        assertFalse(userRepository.existsById(savedUser.getId()));
    }

    @Test
    @Transactional
    void testIfGuaranteeCanHaveMultipleGuaranteeHistories() {
        User user = createTestUser();
        User savedUser = userRepository.save(user);

        Guarantee testGuarantee = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee = guaranteeRepository.save(testGuarantee);

        savedUser.getGuarantees().add(savedGuarantee);

        GuaranteeHistory testGuaranteeHistory1 = createTestGuaranteeHistory1(savedGuarantee, savedUser);
        GuaranteeHistory testGuaranteeHistory2 = createTestGuaranteeHistory2(savedGuarantee, savedUser);

        savedGuarantee.getGuaranteeHistory().add(testGuaranteeHistory1);
        savedGuarantee.getGuaranteeHistory().add(testGuaranteeHistory2);

        guaranteeHistoryRepository.save(testGuaranteeHistory1);
        guaranteeHistoryRepository.save(testGuaranteeHistory2);

        Guarantee loadedGuarantee = guaranteeRepository.findById(savedGuarantee.getId()).orElseThrow();
        assertEquals(2, loadedGuarantee.getGuaranteeHistory().size());

        List<GuaranteeHistory> guaranteeHistories = guaranteeHistoryRepository.findAll();
        assertEquals(2, guaranteeHistories.size());
        assertEquals(testGuaranteeHistory1.getId(), guaranteeHistories.getFirst().getId());
        assertEquals(testGuaranteeHistory2.getId(), guaranteeHistories.getLast().getId());
    }

    @Test
    @Transactional
    void testIfUserCanHaveMultipleGuarantees() {
        User user = createTestUser();
        User savedUserInTheDatabase = userRepository.save(user);

        Guarantee testGuarantee1 = createTestGuarantee1(savedUserInTheDatabase);
        Guarantee testGuarantee2 = createTestGuarantee2(savedUserInTheDatabase);

        savedUserInTheDatabase.getGuarantees().add(testGuarantee1);
        savedUserInTheDatabase.getGuarantees().add(testGuarantee2);

        userRepository.save(savedUserInTheDatabase);
        userRepository.flush();

        User loadedUser = userRepository.findByUsername("tommysmith").orElseThrow();

        assertNotNull(loadedUser.getGuarantees());
        assertEquals(2, loadedUser.getGuarantees().size());
        assertTrue(loadedUser.getGuarantees()
                .stream()
                .allMatch(guarantee -> guarantee.getUser().getId().equals(loadedUser.getId())));
    }

    private GuaranteeHistory createTestGuaranteeHistory2(Guarantee guarantee, User user){
        return GuaranteeHistory.builder()
                .guarantee(guarantee)
                .user(user)
                .status(GuaranteeStatus.APPROVED)
                .changeTime(LocalDateTime.now())
                .notes("Reklamacja uznana")
                .positiveFeedback(true)
                .build();
    }

    private GuaranteeHistory createTestGuaranteeHistory1(Guarantee guarantee, User user){
            return GuaranteeHistory.builder()
                    .guarantee(guarantee)
                    .user(user)
                    .status(GuaranteeStatus.PENDING)
                    .changeTime(LocalDateTime.now())
                    .notes("Broken screen.")
                    .positiveFeedback(false)
                    .build();
    }

    private User createTestUser(){
        return User.builder()
                .firstName("Tommy")
                .lastName("Smith")
                .username("tommysmith")
                .password("hashedQwerty")
                .email("tommy@testmail.com")
                .role(Role.USER)
                .createdDate(LocalDateTime.of(2025,3,1,10,10))
                .userEnabled(false)
                .guarantees(new ArrayList<>())
                .guaranteeHistories(new ArrayList<>())
                .build();
    }

    private Guarantee createTestGuarantee1(User user){
        return Guarantee.builder()
                .brand("Samsung")
                .model("GALAXY A65")
                .documentUrl("https://www.examplepicutre.com/samsung")
                .notes("Damaged screen.")
                .kindOfProduct(Product.ELECTRONICS)
                .startDate(LocalDate.of(2025,2,10))
                .endDate(LocalDate.of(2026,2,9))
                .guaranteeStatus(GuaranteeStatus.ACTIVE)
                .user(user)
                .guaranteeHistory(new ArrayList<>())
                .build();
    }

    private Guarantee createTestGuarantee2(User user){
        return Guarantee.builder()
                .brand("Nike")
                .model("Airmax")
                .documentUrl("https://www.examplepicutre.com/nike")
                .notes("Big scratch on the left shoe.")
                .kindOfProduct(Product.CLOTHES)
                .startDate(LocalDate.of(2025,1,1))
                .endDate(LocalDate.of(2026,1,1))
                .guaranteeStatus(GuaranteeStatus.ACTIVE)
                .user(user)
                .guaranteeHistory(new ArrayList<>())
                .build();
    }
}
