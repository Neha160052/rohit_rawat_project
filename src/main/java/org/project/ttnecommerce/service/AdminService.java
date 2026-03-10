package org.project.ttnecommerce.service;
import lombok.RequiredArgsConstructor;
import org.project.ttnecommerce.dto.AdminCustomerResponse;
import org.project.ttnecommerce.dto.AdminSellerResponse;
import org.project.ttnecommerce.entity.Address;
import org.project.ttnecommerce.entity.Seller;
import org.project.ttnecommerce.entity.User;
import org.project.ttnecommerce.exception.InvalidInputException;
import org.project.ttnecommerce.exception.InvalidRequestException;
import org.project.ttnecommerce.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "email", "firstName", "lastName");

    public List<AdminCustomerResponse> getAllCustomers(
            int pageOffset,
            int pageSize,
            String sort,
            String email
    ) {

        validatePagination(pageOffset, pageSize);
        validateSort(sort);

        Pageable pageable = PageRequest.of(
                pageOffset,
                pageSize,
                Sort.by(sort).ascending()
        );

        Page<User> customers;

        if (email != null && !email.isBlank()) {
            customers = userRepository.findByCustomerIsNotNullAndEmailContainingIgnoreCaseAndIsDeletedFalse(email, pageable);
        }
        else {
            customers = userRepository.findByCustomerIsNotNullAndIsDeletedFalse(pageable);
        }
        return customers.map(this::convertCustomerToDTO).getContent();
    }


    //Seller
    public List<AdminSellerResponse> getAllSellers(
            int pageOffset,
            int pageSize,
            String sort,
            String email
    ) {

        validatePagination(pageOffset, pageSize);
        validateSort(sort);

        Pageable pageable = PageRequest.of(
                pageOffset,
                pageSize,
                Sort.by(sort).ascending()
        );

        Page<User> sellers;
        if (email != null && !email.isBlank()) {
            sellers = userRepository.findBySellerIsNotNullAndEmailContainingIgnoreCaseAndIsDeletedFalse(email, pageable);
        }
        else {
            sellers = userRepository.findBySellerIsNotNullAndIsDeletedFalse(pageable);
        }

        return sellers.map(this::convertSellerToDTO).getContent();
    }


    // Activate Customer
    public String activateCustomer(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (user.getIsDeleted()) {
            throw new InvalidRequestException("Cannot activate a deleted user");
        }
        if (user.getCustomer() == null) {
            throw new InvalidRequestException("User is not a customer");
        }
        if (user.getIsActive()) {
            return "Customer already active";
        }
        user.setIsActive(true);
        userRepository.save(user);
        emailService.sendCustomerActivationEmailByAdmin(user);
        return "Customer activated successfully";
    }

    // Deactivate Account
    public String deactivateCustomer(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidInputException("User not found"));

        if (user.getIsDeleted()) {
            throw new InvalidInputException("Cannot deactivate a deleted user");
        }
        if (user.getCustomer() == null) {
            throw new InvalidInputException("User is not a customer");
        }
        if (!user.getIsActive()) {
            return "Customer already deactivated";
        }
        user.setIsActive(false);
        userRepository.save(user);
        emailService.sendCustomerDeactivationEmailByAdmin(user);

        return "Customer deactivated successfully";
    }



    private void validatePagination(int pageOffset, int pageSize) {

        if (pageOffset < 0) {
            throw new InvalidRequestException("Page offset cannot be negative");
        }

        if (pageSize <= 0) {
            throw new InvalidRequestException("Page size must be greater than zero");
        }

        if (pageSize > 50) {
            throw new InvalidRequestException("Page size cannot exceed 50");
        }
    }

    private void validateSort(String sort) {

        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            throw new InvalidRequestException("Invalid sort field: " + sort);
        }
    }


    private AdminCustomerResponse convertCustomerToDTO(User user) {

        String fullName = user.getFirstName() + " " + (user.getMiddleName() != null ? user.getMiddleName() + " " : "") +
                        user.getLastName();

        return new AdminCustomerResponse(
                user.getId(),
                fullName,
                user.getEmail(),
                user.getIsActive()
        );
    }


    private AdminSellerResponse convertSellerToDTO(User user) {

        Seller seller = user.getSeller();

        String fullName = user.getFirstName() + " " + (user.getMiddleName() != null ? user.getMiddleName() + " " : "") +
                        user.getLastName();
        Address address = user.getAddresses()
                .stream()
                .findFirst()
                .orElse(null);

        String companyAddress = null;
        if (address != null) {
            companyAddress = address.getAddressLine() + ", " +
                            address.getCity() + ", " +
                            address.getState() + ", " +
                            address.getCountry();
        }

        return new AdminSellerResponse(
                user.getId(),
                fullName,
                user.getEmail(),
                user.getIsActive(),
                seller.getCompanyName(),
                companyAddress,
                seller.getCompanyContact()
        );
    }
}