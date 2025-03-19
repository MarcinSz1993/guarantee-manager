package com.marcinsz.backend.guarantee;

import com.marcinsz.backend.exception.GuaranteeNotFoundException;
import com.marcinsz.backend.image.ImageResponse;
import com.marcinsz.backend.image.ImageService;
import com.marcinsz.backend.mapper.GuaranteeMapper;
import com.marcinsz.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GuaranteeService {
    private final GuaranteeRepository guaranteeRepository;
    private final ImageService imageService;

    @Transactional
    public void deleteGuarantee(Authentication authentication,Long guaranteeId){
        User user = extractUserFromAuthentication(authentication);
        Guarantee guarantee = guaranteeRepository.findById(guaranteeId).orElseThrow(() -> new GuaranteeNotFoundException("Guarantee not found"));
        checkIfUserHasPermissionToAccessTheGuarantee(user,guarantee);
        guaranteeRepository.delete(guarantee);
    }

    public void editGuaranteeExpiration(Authentication authentication, Long guaranteeId, LocalDate expirationDate) {
        User user = extractUserFromAuthentication(authentication);
        Guarantee guarantee = guaranteeRepository.findById(guaranteeId).orElseThrow(() -> new GuaranteeNotFoundException("Guarantee not found"));
        checkIfUserHasPermissionToAccessTheGuarantee(user,guarantee);
        guarantee.setEndDate(expirationDate);
        guaranteeRepository.save(guarantee);
    }

    public void editGuaranteeStatus(Authentication authentication,Long guaranteeId,GuaranteeStatus guaranteeStatus){
        User user = extractUserFromAuthentication(authentication);
        Guarantee guarantee = guaranteeRepository.findById(guaranteeId).orElseThrow(() -> new GuaranteeNotFoundException("Guarantee not found"));
        checkIfUserHasPermissionToAccessTheGuarantee(user,guarantee);
        guarantee.setGuaranteeStatus(guaranteeStatus);
        guaranteeRepository.save(guarantee);
    }

    public GuaranteeResponse getGuaranteeDetails(Long guaranteeId,
                                                 Authentication authentication){
        User user = extractUserFromAuthentication(authentication);
        Guarantee guarantee = guaranteeRepository.findById(guaranteeId)
                .orElseThrow(() -> new GuaranteeNotFoundException("Guarantee not found"));
        checkIfUserHasPermissionToAccessTheGuarantee(user, guarantee);
        return GuaranteeMapper.mapGuaranteeToGuaranteeResponse(guarantee);
    }

    public Page<GuaranteeResponse> getAllGuarantees(Authentication authentication,
                                                    int page,
                                                    int size) {
        User user = extractUserFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(page,size, Sort.by("endDate").descending());
         return guaranteeRepository.findByUser_IdAndGuaranteeStatusNot(user.getId(),GuaranteeStatus.EXPIRED,pageable)
                .map(GuaranteeMapper::mapGuaranteeToGuaranteeResponse);
    }

    public GuaranteeResponse addGuarantee(Authentication authentication,
                                          AddGuaranteeRequest addGuaranteeRequest,
                                          MultipartFile receiptImage) throws IOException {

        User user = extractUserFromAuthentication(authentication);
        ImageResponse imageResponse = imageService.uploadReceiptImage(receiptImage);
        Guarantee guarantee = createGuaranteeFromMethodArguments(addGuaranteeRequest, imageResponse, user);
        guaranteeRepository.save(guarantee);
        return GuaranteeMapper.mapGuaranteeToGuaranteeResponse(guarantee);
    }

    private Guarantee createGuaranteeFromMethodArguments(AddGuaranteeRequest addGuaranteeRequest, ImageResponse imageResponse, User user) {
        return Guarantee.builder()
                .brand(addGuaranteeRequest.getBrand())
                .model(addGuaranteeRequest.getModel())
                .documentUrl(imageResponse.getImageUrl())
                .notes(addGuaranteeRequest.getNotes())
                .kindOfDevice(addGuaranteeRequest.getKindOfDevice())
                .startDate(addGuaranteeRequest.getStartDate())
                .endDate(addGuaranteeRequest.getEndDate())
                .guaranteeStatus(GuaranteeStatus.ACTIVE)
                .user(user)
                .guaranteeHistory(new ArrayList<>())
                .build();
    }

    private void checkIfUserHasPermissionToAccessTheGuarantee(User user, Guarantee guarantee) {
        if (!user.getId().equals(guarantee.getUser().getId())) {
            throw new SecurityException("You do not have permission to access this resource");
        }
    }

    private User extractUserFromAuthentication(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

}
