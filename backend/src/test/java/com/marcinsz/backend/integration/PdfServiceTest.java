package com.marcinsz.backend.integration;

import com.marcinsz.backend.exception.InvalidInputException;
import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeRepository;
import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.guarantee.Product;
import com.marcinsz.backend.history.GuaranteeHistory;
import com.marcinsz.backend.history.GuaranteeHistoryRepository;
import com.marcinsz.backend.mongodb.*;
import com.marcinsz.backend.pdf.PdfService;
import com.marcinsz.backend.user.Role;
import com.marcinsz.backend.user.User;
import com.marcinsz.backend.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers
@SpringBootTest
public class PdfServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>("postgres:16-alpine3.19");


    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:7.0.11");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuaranteeRepository guaranteeRepository;

    @Autowired
    private GuaranteeHistoryRepository guaranteeHistoryRepository;

    @Autowired
    private GuaranteeHistoryMongoRepository guaranteeHistoryMongoRepository;

    @Autowired
    private ResetPasswordMongoRepository resetPasswordMongoRepository;

    @Autowired
    private RemovedGuaranteeHistoryMongoRepository removedGuaranteeHistoryMongoRepository;

    @Autowired
    private PdfService pdfService;

    @BeforeEach
    void setUp() {
        mongoDBContainer.isRunning();
        postgreSQLContainer.isRunning();
        guaranteeHistoryMongoRepository.deleteAll();
        removedGuaranteeHistoryMongoRepository.deleteAll();
        resetPasswordMongoRepository.deleteAll();
        guaranteeRepository.deleteAll();
        guaranteeHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldValidatePostgresDBConnection() {
        assertTrue(postgreSQLContainer.isCreated());
        assertTrue(postgreSQLContainer.isRunning());
        log.info("Postgres jdbc url: {}", postgreSQLContainer.getJdbcUrl());
        log.info("Postgres username: {}", postgreSQLContainer.getUsername());
        log.info("Postgres password: {}", postgreSQLContainer.getPassword());
        log.info("Postgres docker image: {}", postgreSQLContainer.getDockerImageName());
        log.info("Postgres database name: {}", postgreSQLContainer.getDatabaseName());
    }

    @Test
    void shouldValidateMongoDBConnection() {
        assertTrue(mongoDBContainer.isCreated());
        assertTrue(mongoDBContainer.isRunning());
        log.info("MongoDB container name: {}", mongoDBContainer.getContainerName());
        log.info("MongoDB host: {}", mongoDBContainer.getHost());
        log.info("MongoDB docker image name: {}", mongoDBContainer.getDockerImageName());
        log.info("MongoDB replicate set url: {}", mongoDBContainer.getReplicaSetUrl());
        log.info("MongoDB connection: {}", mongoDBContainer.getConnectionString());

    }
    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionAndReturnAllNullFieldsWhenMoreThanOneFieldIsNull_ADDED_GUARANTEE_HISTORY() {
        User user = createTestUser();
        User savedUser = userRepository.save(user);

        Guarantee guarantee = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee = guaranteeRepository.save(guarantee);
        GuaranteeHistory guaranteeHistory = createTestGuaranteeHistory(savedGuarantee, savedUser);
        GuaranteeHistory savedGuaranteeHistory = guaranteeHistoryRepository.save(guaranteeHistory);
        GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(savedGuaranteeHistory, savedUser);
        guaranteeHistoryDocument.setOperationTime(null);
        guaranteeHistoryDocument.setGuaranteeId(null);
        guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);


        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(savedUser.getEmail()));
        assertEquals("operationTime,guaranteeId cannot be null or empty!", invalidInputException.getMessage());

    }

    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionAndReturnAllNullFieldsWhenMoreThanOneFieldIsNull_RESET_PASSWORD() {
        User user = createTestUser();
        User savedUser = userRepository.save(user);

        ResetPasswordDocument resetPasswordDocument = createTestResetPasswordDocument(savedUser);
        resetPasswordDocument.setAccountCreationDate(null);
        resetPasswordDocument.setOperationTime(null);
        resetPasswordDocument.setId("");
        resetPasswordMongoRepository.save(resetPasswordDocument);

        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(savedUser.getEmail()));
        assertEquals("operationTime,accountCreationDate,id cannot be null or empty!", invalidInputException.getMessage());

    }

    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionAndReturnOneFiledWhichIsNullWhenOneFieldIsNull_RESET_PASSWORD() {
        User user = createTestUser();
        User savedUser = userRepository.save(user);

        ResetPasswordDocument resetPasswordDocument = createTestResetPasswordDocument(savedUser);
        resetPasswordDocument.setAccountCreationDate(null);
        resetPasswordMongoRepository.save(resetPasswordDocument);

        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(savedUser.getEmail()));
        assertEquals("accountCreationDate cannot be null or empty!", invalidInputException.getMessage());
    }

    @Test
    void createUserLogsPdfDocumentShouldGeneratePdfDocumentWhereAllTypesOfLogsAreSortedFromNewestToOldestOne_RESET_PASSWORD() throws IOException {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Guarantee guarantee = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee = guaranteeRepository.save(guarantee);


        long minutes = 0;
        for (int i = 0; i < 5; i++) {
            GuaranteeHistory guaranteeHistory = createTestGuaranteeHistory(savedGuarantee, savedUser);
            guaranteeHistory.setChangeTime(LocalDateTime.of(2025, 8, 10, 8, 20, 10).plusMinutes(minutes));
            GuaranteeHistory savedGuaranteeHistory = guaranteeHistoryRepository.save(guaranteeHistory);
            GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(savedGuaranteeHistory, savedUser);
            guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);

            ResetPasswordDocument resetPasswordDocument = createTestResetPasswordDocument(savedUser);
            resetPasswordDocument.setOperationTime(resetPasswordDocument.getOperationTime().plusMinutes(minutes));
            resetPasswordMongoRepository.save(resetPasswordDocument);

            RemovedGuaranteeHistoryDocument removedGuaranteeHistoryDocument = createTestRemovedGuaranteeHistoryDocument(savedGuaranteeHistory, savedUser);
            removedGuaranteeHistoryDocument.setOperationTime(removedGuaranteeHistoryDocument.getOperationTime().plusMinutes(minutes));
            removedGuaranteeHistoryMongoRepository.save(removedGuaranteeHistoryDocument);

            minutes = minutes + 10;
        }

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(savedUser.getEmail());

        Files.write(Path.of("target/all_types_logs_sorted.pdf"), userLogsPdfDocument);
        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(document);
            int index1 = text.indexOf("Operation time: 2025-09-07T10:50");
            int index2 = text.indexOf("Operation time: 2025-09-07T10:40");
            int index3 = text.indexOf("Operation time: 2025-09-07T10:30");
            int index4 = text.indexOf("Operation time: 2025-09-07T10:20");
            int index5 = text.indexOf("Operation time: 2025-09-07T10:10");
            int index6 = text.indexOf("Operation time: 2025-09-05T15:40");
            int index7 = text.indexOf("Operation time: 2025-09-05T15:30");
            int index8 = text.indexOf("Operation time: 2025-09-05T15:20");
            int index9 = text.indexOf("Operation time: 2025-09-05T15:10");
            int index10 = text.indexOf("Operation time: 2025-09-05T15:00");
            int index11 = text.indexOf("Operation time: 2025-08-10T09:00:10");
            int index12 = text.indexOf("Operation time: 2025-08-10T08:50:10");
            int index13 = text.indexOf("Operation time: 2025-08-10T08:40:10");
            int index14 = text.indexOf("Operation time: 2025-08-10T08:30:10");
            int index15 = text.indexOf("Operation time: 2025-08-10T08:20:10");

            assertTrue(index1 < index2);
            assertTrue(index2 < index3);
            assertTrue(index3 < index4);
            assertTrue(index4 < index5);
            assertTrue(index5 < index6);
            assertTrue(index6 < index7);
            assertTrue(index7 < index8);
            assertTrue(index8 < index9);
            assertTrue(index9 < index10);
            assertTrue(index10 < index11);
            assertTrue(index11 < index12);
            assertTrue(index12 < index13);
            assertTrue(index13 < index14);
            assertTrue(index14 < index15);

        }
    }

    @Test
    void createUserLogsPdfDocumentShouldGeneratePdfDocumentWhereRemovedGuaranteeHistoryLogsAreSortedFromNewestToOldestOne() throws IOException {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Guarantee guarantee = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee = guaranteeRepository.save(guarantee);


        long minutes = 0;
        for (int i = 0; i < 5; i++) {
            GuaranteeHistory guaranteeHistory = createTestGuaranteeHistory(savedGuarantee, savedUser);
            guaranteeHistory.setChangeTime(LocalDateTime.of(2025, 8, 10, 8, 20, 10).plusMinutes(minutes));
            GuaranteeHistory savedGuaranteeHistory = guaranteeHistoryRepository.save(guaranteeHistory);
            RemovedGuaranteeHistoryDocument removedGuaranteeHistoryDocument = createTestRemovedGuaranteeHistoryDocument(savedGuaranteeHistory, savedUser);
            removedGuaranteeHistoryDocument.setOperationTime(savedGuaranteeHistory.getChangeTime());
            removedGuaranteeHistoryMongoRepository.save(removedGuaranteeHistoryDocument);

            minutes = minutes + 10;
        }

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(savedUser.getEmail());

        Files.write(Path.of("target/removed_guarantee_history_sorted.pdf"), userLogsPdfDocument);
        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            int firstIndex = text.indexOf("Operation time: 2025-08-10T09:00:10");
            int secondIndex = text.indexOf("Operation time: 2025-08-10T08:50:10");
            int thirdIndex = text.indexOf("Operation time: 2025-08-10T08:40:10");
            int fourthIndex = text.indexOf("Operation time: 2025-08-10T08:30:10");
            int fifthIndex = text.indexOf("Operation time: 2025-08-10T08:20:10");

            assertTrue(firstIndex < secondIndex);
            assertTrue(secondIndex < thirdIndex);
            assertTrue(thirdIndex < fourthIndex);
            assertTrue(fourthIndex < fifthIndex);

        }
    }

    @Test
    void createUserLogsPdfDocumentShouldGeneratePdfDocumentWhereResetPasswordLogsAreSortedFromNewestToOldestOne() throws IOException {
        User user = createTestUser();
        User savedUser = userRepository.save(user);


        long minutes = 0;
        for (int i = 0; i < 5; i++) {
            ResetPasswordDocument resetPasswordDocument = createTestResetPasswordDocument(savedUser);
            resetPasswordDocument.setOperationTime(LocalDateTime.of(2025, 7, 11, 11, 0, 0).plusMinutes(minutes));
            resetPasswordMongoRepository.save(resetPasswordDocument);
            minutes = minutes + 10;
        }

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(savedUser.getEmail());

        Files.write(Path.of("target/reset_password_sorted.pdf"), userLogsPdfDocument);
        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            int firstIndex = text.indexOf("Operation time: 2025-07-11T11:40");
            int secondIndex = text.indexOf("Operation time: 2025-07-11T11:30");
            int thirdIndex = text.indexOf("Operation time: 2025-07-11T11:20");
            int fourthIndex = text.indexOf("Operation time: 2025-07-11T11:10");
            int fifthIndex = text.indexOf("Operation time: 2025-07-11T11:00");

            assertTrue(firstIndex < secondIndex);
            assertTrue(secondIndex < thirdIndex);
            assertTrue(thirdIndex < fourthIndex);
            assertTrue(fourthIndex < fifthIndex);

        }
    }

    @Test
    void createUserLogsPdfDocumentShouldGeneratePdfDocumentWhereAddedGuaranteeHistoryLogsAreSortedFromNewestToOldestOne() throws IOException {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Guarantee guarantee = createTestGuarantee1(savedUser);
        guaranteeRepository.save(guarantee);

        long minutes = 0;
        for (int i = 0; i < 5; i++) {
            GuaranteeHistory guaranteeHistory = createTestGuaranteeHistory(guarantee, savedUser);
            guaranteeHistory.setNotes("Broken screen " + i);
            guaranteeHistoryRepository.save(guaranteeHistory);
            GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(guaranteeHistory, savedUser);
            guaranteeHistoryDocument.setOperationTime(LocalDateTime.now().plusMinutes(minutes));
            guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);
            minutes = minutes + 10;
        }

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(savedUser.getEmail());

        Files.write(Path.of("target/sorted.pdf"), userLogsPdfDocument);
        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            String firstLog = "Broken screen 4";
            String secondLog = "Broken screen 3";
            String thirdLog = "Broken screen 2";
            String fourthLog = "Broken screen 1";
            String fifthLog = "Broken screen 0";

            int firstIndex = text.indexOf(firstLog);
            int secondIndex = text.indexOf(secondLog);
            int thirdIndex = text.indexOf(thirdLog);
            int fourthIndex = text.indexOf(fourthLog);
            int fifthIndex = text.indexOf(fifthLog);

            assertTrue(firstIndex < secondIndex);
            assertTrue(secondIndex < thirdIndex);
            assertTrue(thirdIndex < fourthIndex);
            assertTrue(fourthIndex < fifthIndex);
        }
    }

    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenAddedGuaranteeHistoryLogHasLongWrittenNote() {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        String note = "a";
        Guarantee guarantee = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee = guaranteeRepository.save(guarantee);
        GuaranteeHistory guaranteeHistory = createTestGuaranteeHistory(savedGuarantee, savedUser);
        guaranteeHistory.setNotes(note.repeat(250));
        GuaranteeHistory savedGuaranteeHistory = guaranteeHistoryRepository.save(guaranteeHistory);

        GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(savedGuaranteeHistory, savedUser);
        guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);

        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(savedUser.getEmail()));
        assertEquals("Written note should have not more than 55 characters", invalidInputException.getMessage());

    }

    @Test
    void createUserLogsPdfDocumentShouldGeneratePdfDocumentWhereAlwaysOnlyFirstPageHasSpecifiedTitle() throws IOException {
        int counter = 1;
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        String expectedTitle = "Below you find logs of user with email " + savedUser.getEmail();
        Guarantee guarantee = createTestGuarantee1(savedUser);
        guarantee.setNotes("Guarantee test " + counter);
        Guarantee savedGuarantee = guaranteeRepository.save(guarantee);

        for (int i = 0; i < 20; i++) {
            GuaranteeHistory guaranteeHistory = createTestGuaranteeHistory(savedGuarantee, savedUser);
            guaranteeHistory.setNotes("Guarantee test " + counter);
            counter++;
            GuaranteeHistory saveGuaranteeHistoryRepository = guaranteeHistoryRepository.save(guaranteeHistory);

            GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(saveGuaranteeHistoryRepository, savedUser);
            guaranteeHistoryDocument.setNotes(saveGuaranteeHistoryRepository.getNotes());
            guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);
        }

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(savedUser.getEmail());
        Files.write(Path.of("target/20logs.pdf"), userLogsPdfDocument);

        try (PDDocument generatedPdfDocument = Loader.loadPDF(userLogsPdfDocument)) {
            int allPages = generatedPdfDocument.getPages().getCount();


            PDFTextStripper stripper = new PDFTextStripper();
            for (int i = 1; i <= allPages; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(generatedPdfDocument);
                if (i == 1) {
                    assertTrue(text.contains(expectedTitle));
                } else {
                    assertFalse(text.contains(expectedTitle));
                }
            }
        }
    }

    @Test
    void createUserLogsPdfDocumentShouldGenerateNextPageWhenThereIsNoEnoughSpaceForAllLogs() throws IOException {
        int counter = 1;
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Guarantee guarantee = createTestGuarantee1(savedUser);
        guarantee.setNotes("Guarantee test " + counter);
        Guarantee savedGuarantee = guaranteeRepository.save(guarantee);

        for (int i = 0; i < 10; i++) {
            GuaranteeHistory guaranteeHistory = createTestGuaranteeHistory(savedGuarantee, savedUser);
            guaranteeHistory.setNotes("Guarantee test " + counter);
            counter++;
            GuaranteeHistory saveGuaranteeHistoryRepository = guaranteeHistoryRepository.save(guaranteeHistory);

            GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(saveGuaranteeHistoryRepository, savedUser);
            guaranteeHistoryDocument.setNotes(saveGuaranteeHistoryRepository.getNotes());
            guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);
        }

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(savedUser.getEmail());
        Files.write(Path.of("target/10logs.pdf"), userLogsPdfDocument);

        try (PDDocument generatedPdfDocument = Loader.loadPDF(userLogsPdfDocument)) {
            assertEquals(2, generatedPdfDocument.getPages().getCount());

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(generatedPdfDocument);

            assertTrue(text.contains("Guarantee test 1"));
            assertTrue(text.contains("Guarantee test 10"));
        }


    }

    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenUserHasGuaranteesAddedButNoHasLogs() {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Guarantee guarantee = createTestGuarantee1(savedUser);
        guaranteeRepository.save(guarantee);

        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(user.getEmail()));
        assertEquals("No audit logs found", invalidInputException.getMessage());
    }

    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenUserDoesNotHaveAnyGuaranteesAddedAndLogs() {
        User user = createTestUser();
        userRepository.save(user);

        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(user.getEmail()));
        assertEquals("No audit logs found", invalidInputException.getMessage());
    }

    @Test
    void createUserLogsPdfDocumentShouldThrowUserNotFoundExceptionWithSpecifiedCommunicationWhenUserDoesNotExist() {
        String notExistingEmail = "notExistingEmail@op.pl";
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> pdfService.createUserLogsPdfDocument(notExistingEmail));
        assertEquals("User with email " + notExistingEmail + " not found", userNotFoundException.getMessage());
    }

    @Test
    void createUserLogsPdfDocumentShouldGeneratePDFSuccessfullyWhenThreeKindsOfLogsExists() throws IOException {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(user.getId(), savedUser.getId());

        Guarantee testGuarantee1 = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee1 = guaranteeRepository.save(testGuarantee1);
        GuaranteeHistory testGuaranteeHistory1 = createTestGuaranteeHistory(savedGuarantee1, savedUser);
        GuaranteeHistory savedGuarantee1History = guaranteeHistoryRepository.save(testGuaranteeHistory1);

        GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(savedGuarantee1History, savedUser);
        guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);

        ResetPasswordDocument resetPasswordDocument = createTestResetPasswordDocument(savedUser);
        resetPasswordMongoRepository.save(resetPasswordDocument);

        RemovedGuaranteeHistoryDocument RemovedGuaranteeHistoryDocument = createTestRemovedGuaranteeHistoryDocument(savedGuarantee1History, savedUser);
        removedGuaranteeHistoryMongoRepository.save(RemovedGuaranteeHistoryDocument);

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(user.getEmail());
        Files.write(Path.of("target/3kindsoflogs.pdf"), userLogsPdfDocument);
        assertTrue(userLogsPdfDocument.length > 0);

        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("1. RESET_PASSWORD"));
            assertTrue(text.contains("Tommy"));
            assertTrue(text.contains("Smith"));
            assertTrue(text.contains("2. REMOVED_GUARANTEE_HISTORY"));
        }
    }

    @Test
    void createUserLogsPdfDocumentShouldGeneratePDFSuccessfullyWhenTwoKindsOfLogsExists() throws IOException {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(user.getId(), savedUser.getId());

        Guarantee testGuarantee1 = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee1 = guaranteeRepository.save(testGuarantee1);
        GuaranteeHistory testGuaranteeHistory1 = createTestGuaranteeHistory(savedGuarantee1, savedUser);
        GuaranteeHistory savedGuarantee1History = guaranteeHistoryRepository.save(testGuaranteeHistory1);

        GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(savedGuarantee1History, savedUser);
        guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);

        ResetPasswordDocument resetPasswordDocument = createTestResetPasswordDocument(savedUser);
        resetPasswordMongoRepository.save(resetPasswordDocument);

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(user.getEmail());
        Files.write(Path.of("target/test-logs-output.pdf"), userLogsPdfDocument);
        assertTrue(userLogsPdfDocument.length > 0);

        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("1. RESET_PASSWORD"));
            assertTrue(text.contains("Tommy"));
            assertTrue(text.contains("Smith"));
            assertTrue(text.contains("Email: tommy@testmail.com"));
            assertTrue(text.contains("2. ADDED_GUARANTEE_HISTORY"));
        }
    }


    @Test
    void createUserLogsPdfDocumentShouldGeneratePDFSuccessfullyWhenOnlyOneKindOfLogsExists() throws IOException {
        User user = createTestUser();
        User savedUser = userRepository.save(user);
        Assertions.assertNotNull(savedUser);
        Assertions.assertEquals(user.getId(), savedUser.getId());

        Guarantee testGuarantee1 = createTestGuarantee1(savedUser);
        Guarantee savedGuarantee1 = guaranteeRepository.save(testGuarantee1);
        GuaranteeHistory testGuaranteeHistory1 = createTestGuaranteeHistory(savedGuarantee1, savedUser);
        GuaranteeHistory savedGuarantee1History = guaranteeHistoryRepository.save(testGuaranteeHistory1);

        GuaranteeHistoryDocument guaranteeHistoryDocument = createTestGuaranteeHistoryDocument(savedGuarantee1History, savedUser);
        guaranteeHistoryMongoRepository.save(guaranteeHistoryDocument);

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(user.getEmail());


        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            assertTrue(text.contains("Tommy"));
            assertTrue(text.contains("Smith"));
            assertTrue(text.contains("Broken screen."));
            assertTrue(text.contains("Feedback was positive: false"));
        }

        assertTrue(userLogsPdfDocument.length > 0);
    }

    private User createTestUser() {
        return User.builder()
                .firstName("Tommy")
                .lastName("Smith")
                .username("tommysmith")
                .password("hashedQwerty")
                .email("tommy@testmail.com")
                .role(Role.USER)
                .createdDate(LocalDateTime.of(2025, 3, 1, 10, 10))
                .userEnabled(true)
                .guarantees(new ArrayList<>())
                .guaranteeHistories(new ArrayList<>())
                .build();
    }

    private Guarantee createTestGuarantee1(User user) {
        return Guarantee.builder()
                .brand("Samsung")
                .model("GALAXY A65")
                .documentUrl("https://www.examplepicutre.com/samsung")
                .notes("Damaged screen.")
                .kindOfProduct(Product.ELECTRONICS)
                .startDate(LocalDate.of(2025, 2, 10))
                .endDate(LocalDate.of(2026, 2, 9))
                .guaranteeStatus(GuaranteeStatus.ACTIVE)
                .user(user)
                .guaranteeHistory(new ArrayList<>())
                .build();
    }

    private GuaranteeHistory createTestGuaranteeHistory(Guarantee guarantee, User user) {
        return GuaranteeHistory.builder()
                .guarantee(guarantee)
                .user(user)
                .status(GuaranteeStatus.PENDING)
                .changeTime(LocalDateTime.now())
                .notes("Broken screen.")
                .positiveFeedback(false)
                .build();
    }

    private GuaranteeHistoryDocument createTestGuaranteeHistoryDocument(GuaranteeHistory guaranteeHistory, User user) {
        return GuaranteeHistoryDocument.builder()
                .guaranteeId(guaranteeHistory.getId())
                .guaranteeOwnerName(user.getFirstName())
                .guaranteeOwnerLastName(user.getLastName())
                .guaranteeOwnerEmail(user.getEmail())
                .notes(guaranteeHistory.getNotes())
                .operationTime(guaranteeHistory.getChangeTime())
                .positiveFeedback(false)
                .build();
    }

    private ResetPasswordDocument createTestResetPasswordDocument(User user) {
        return ResetPasswordDocument.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .operationTime(LocalDateTime.of(2025, 9, 7, 10, 10))
                .accountCreationDate(user.getCreatedDate().toLocalDate())
                .build();
    }

    private RemovedGuaranteeHistoryDocument createTestRemovedGuaranteeHistoryDocument(GuaranteeHistory guaranteeHistory, User user) {
        return RemovedGuaranteeHistoryDocument.builder()
                .guaranteeId(guaranteeHistory.getId())
                .guaranteeOwnerName(user.getFirstName())
                .guaranteeOwnerLastName(user.getLastName())
                .guaranteeOwnerEmail(user.getEmail())
                .operationTime(LocalDateTime.of(2025, 9, 5, 15, 0))
                .build();
    }
}
