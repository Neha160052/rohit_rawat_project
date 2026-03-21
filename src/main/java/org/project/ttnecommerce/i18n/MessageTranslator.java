package org.project.ttnecommerce.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MessageTranslator {

    private static final Pattern INVALID_SORT_FIELD_PATTERN = Pattern.compile("^Invalid sort field: (.+)$");
    private static final Pattern INVALID_METADATA_FIELD_PATTERN = Pattern.compile("^Invalid metadata field: (.+)$");
    private static final Pattern INVALID_VALUE_FOR_FIELD_PATTERN = Pattern.compile("^Invalid value for field: (.+)$");
    private static final Pattern METADATA_FIELD_CREATED_PATTERN = Pattern.compile("^Metadata field created successfully with ID: (.+)$");
    private static final Pattern CATEGORY_CREATED_PATTERN = Pattern.compile("^Category created successfully with ID: (.+)$");

    private static final Map<String, String> MESSAGE_CODES = createMessageCodes();

    private final MessageSource messageSource;

    public String get(String code, Object... args) {
        return messageSource.getMessage(code, args, code, currentLocale());
    }

    public String get(Locale locale, String code, Object... args) {
        return messageSource.getMessage(code, args, code, locale);
    }

    public String getOrDefault(String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, currentLocale());
    }

    public String getOrDefault(Locale locale, String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public String translate(String message) {
        if (message == null || message.isBlank()) {
            return message;
        }

        String translatedDynamicMessage = translateDynamic(message);
        if (translatedDynamicMessage != null) {
            return translatedDynamicMessage;
        }

        String code = MESSAGE_CODES.get(message);
        if (code == null) {
            return message;
        }

        return get(code);
    }

    private String translateDynamic(String message) {
        Matcher invalidSortFieldMatcher = INVALID_SORT_FIELD_PATTERN.matcher(message);
        if (invalidSortFieldMatcher.matches()) {
            return get("error.sort.invalid.value", invalidSortFieldMatcher.group(1));
        }

        Matcher invalidMetadataFieldMatcher = INVALID_METADATA_FIELD_PATTERN.matcher(message);
        if (invalidMetadataFieldMatcher.matches()) {
            return get("error.metadata.field.invalid", invalidMetadataFieldMatcher.group(1));
        }

        Matcher invalidValueForFieldMatcher = INVALID_VALUE_FOR_FIELD_PATTERN.matcher(message);
        if (invalidValueForFieldMatcher.matches()) {
            return get("error.metadata.value.invalid.for.field", invalidValueForFieldMatcher.group(1));
        }

        Matcher metadataFieldCreatedMatcher = METADATA_FIELD_CREATED_PATTERN.matcher(message);
        if (metadataFieldCreatedMatcher.matches()) {
            return get("response.metadata.field.created.with.id", metadataFieldCreatedMatcher.group(1));
        }

        Matcher categoryCreatedMatcher = CATEGORY_CREATED_PATTERN.matcher(message);
        if (categoryCreatedMatcher.matches()) {
            return get("response.category.created.with.id", categoryCreatedMatcher.group(1));
        }

        return null;
    }

    private Locale currentLocale() {
        return LocaleContextHolder.getLocale();
    }

    private static Map<String, String> createMessageCodes() {
        Map<String, String> codes = new LinkedHashMap<>();

        codes.put("Activation link sent successfully", "response.activation.link.sent");
        codes.put("Account already activated", "error.account.already.activated");
        codes.put("Account is not activated", "error.account.not.activated");
        codes.put("Account locked", "error.account.locked");
        codes.put("Account not activated", "error.account.not.activated.short");
        codes.put("Active flag unchanged", "error.active.flag.unchanged");
        codes.put("Address added successfully", "response.address.added");
        codes.put("Address deleted successfully", "response.address.deleted");
        codes.put("Address does not belong to this seller", "error.address.not.owner.seller");
        codes.put("Address not found", "error.address.not.found");
        codes.put("Address updated successfully", "response.address.updated");
        codes.put("All metadata fields must be provided", "error.metadata.fields.all.required");
        codes.put("At least one field must be provided for update", "error.update.fields.required");
        codes.put("At least one valid field must be provided for update", "error.update.valid.fields.required");
        codes.put("At least one value must be provided", "error.metadata.value.required");
        codes.put("Authentication failed", "security.unauthorized.message");
        codes.put("Brand cannot be empty", "error.brand.empty");
        codes.put("Cannot add subcategory because parent category has products", "error.parent.category.has.products");
        codes.put("Category already exists in hierarchy", "error.category.exists.in.hierarchy");
        codes.put("Category already exists under this parent", "error.category.exists.under.parent");
        codes.put("Category id cannot be null", "error.category.id.null");
        codes.put("Category id is required", "error.category.id.required");
        codes.put("Category is deleted", "error.category.deleted");
        codes.put("Category name cannot be empty", "error.category.name.empty");
        codes.put("Category name cannot be null", "error.category.name.null");
        codes.put("Category not found", "error.category.not.found");
        codes.put("Category updated successfully", "response.category.updated");
        codes.put("Company name already exists", "error.company.exists");
        codes.put("Contact number already exists", "error.contact.exists");
        codes.put("Current password is incorrect", "error.current.password.incorrect");
        codes.put("Customer account activated successfully", "response.customer.activated");
        codes.put("Customer activated successfully", "response.customer.status.activated");
        codes.put("Customer already active", "response.customer.status.already.active");
        codes.put("Customer already deactivated", "response.customer.status.already.deactivated");
        codes.put("Customer deactivated successfully", "response.customer.status.deactivated");
        codes.put("Customer registered successfully", "response.customer.registered");
        codes.put("Deleted user cannot be updated", "error.deleted.user.update");
        codes.put("Description too long", "error.description.too.long");
        codes.put("Duplicate metadata field in request", "error.duplicate.metadata.field.request");
        codes.put("Duplicate metadata values not allowed", "error.duplicate.metadata.values");
        codes.put("Duplicate product detected", "error.duplicate.product");
        codes.put("Duplicate variation already exists", "error.duplicate.variation");
        codes.put("Email already exists", "error.email.exists");
        codes.put("Failed to store image", "error.storage.failed");
        codes.put("Field name cannot be empty", "error.field.name.empty");
        codes.put("File cannot be empty", "error.file.empty");
        codes.put("File size must be less than 5MB", "error.file.size");
        codes.put("GST already registered", "error.gst.exists");
        codes.put("Invalid UUID format", "error.uuid.invalid");
        codes.put("Invalid action", "error.invalid.action");
        codes.put("Invalid activation token", "error.activation.token.invalid");
        codes.put("Invalid email or password", "error.invalid.credentials");
        codes.put("Invalid file name", "error.file.name.invalid");
        codes.put("Invalid image format", "error.image.format.invalid");
        codes.put("Invalid metadata format", "error.metadata.format");
        codes.put("Invalid metadata format (must be JSON)", "error.metadata.format.json");
        codes.put("Invalid metadata key/value", "error.metadata.keyvalue.invalid");
        codes.put("Invalid or missing access token", "error.access.token.invalid");
        codes.put("Invalid order", "error.order.invalid.simple");
        codes.put("Invalid refresh token", "error.refresh.token.invalid");
        codes.put("Invalid request parameter", "error.request.parameter.invalid");
        codes.put("Invalid sort field", "error.sort.invalid");
        codes.put("Invalid status", "error.invalid.status");
        codes.put("Invalid status value", "error.invalid.status.value");
        codes.put("Logout successful", "response.logout.success");
        codes.put("Metadata cannot be empty", "error.metadata.empty");
        codes.put("Metadata comparison failed", "error.metadata.comparison.failed");
        codes.put("Metadata field already assigned to this category", "error.metadata.field.assigned");
        codes.put("Metadata field already exists", "error.metadata.field.exists");
        codes.put("Metadata field id is required", "error.metadata.field.id.required");
        codes.put("Metadata field not found", "error.metadata.field.not.found");
        codes.put("Metadata fields added successfully", "response.metadata.fields.added");
        codes.put("Metadata is same as existing", "error.metadata.same.existing");
        codes.put("Metadata list cannot be empty", "error.metadata.list.empty");
        codes.put("Metadata processing failed", "error.metadata.processing.failed");
        codes.put("Metadata structure mismatch", "error.metadata.structure.mismatch");
        codes.put("Metadata value cannot be empty", "error.metadata.value.empty");
        codes.put("New contact must be different from current contact", "error.new.contact.same");
        codes.put("New password and confirm password must match", "error.new.password.confirm.mismatch");
        codes.put("New password cannot be same as old password", "error.new.password.same.old");
        codes.put("New password must be different from current password", "error.new.password.different.current");
        codes.put("No categories found", "error.no.categories.found");
        codes.put("No changes Made", "response.no.changes.made");
        codes.put("No changes detected", "response.no.changes");
        codes.put("No valid variations available for this product", "error.no.valid.variations");
        codes.put("Not your product", "error.not.your.product");
        codes.put("Only image files are allowed", "error.file.image.only");
        codes.put("Only sellers allowed", "error.only.sellers.allowed");
        codes.put("Page offset cannot be negative", "error.page.offset.negative");
        codes.put("Page size cannot exceed 50", "error.page.size.max50");
        codes.put("Page size must be greater than zero", "error.page.size.positive");
        codes.put("Parent category not found", "error.parent.category.not.found");
        codes.put("Password successfully updated", "response.password.reset.success");
        codes.put("Password updated successfully", "response.password.updated");
        codes.put("Passwords do not match", "error.password.mismatch");
        codes.put("Price cannot be negative", "error.price.negative");
        codes.put("Price is same as existing", "error.price.same.existing");
        codes.put("Product ID cannot be null", "error.product.id.null");
        codes.put("Product ID is mandatory", "error.product.id.mandatory");
        codes.put("Product activated successfully", "response.product.activated");
        codes.put("Product already deleted", "error.product.already.deleted");
        codes.put("Product already exists", "error.product.exists");
        codes.put("Product can only be added to leaf category", "error.product.only.leaf.category");
        codes.put("Product category is invalid", "error.product.category.invalid");
        codes.put("Product category is invalid or deleted", "error.product.category.invalid.deleted");
        codes.put("Product created successfully and is inactive until admin approval", "response.product.created.pending");
        codes.put("Product deactivated successfully", "response.product.deactivated");
        codes.put("Product deleted successfully", "response.product.deleted");
        codes.put("Product is deleted", "error.product.deleted");
        codes.put("Product is not active", "error.product.inactive");
        codes.put("Product name cannot be empty", "error.product.name.empty");
        codes.put("Product not found", "error.product.not.found");
        codes.put("Product not found or not yours", "error.product.not.found.or.not.yours");
        codes.put("Product should be active", "error.product.should.be.active");
        codes.put("Product should be inactive", "error.product.should.be.inactive");
        codes.put("Product updated successfully", "response.product.updated");
        codes.put("Product variation created successfully", "response.product.variation.created");
        codes.put("Product variation updated successfully", "response.product.variation.updated");
        codes.put("Profile image uploaded successfully", "response.profile.image.uploaded");
        codes.put("Profile updated successfully", "response.profile.updated");
        codes.put("Quantity cannot be negative", "error.quantity.negative");
        codes.put("Quantity is same as existing", "error.quantity.same.existing");
        codes.put("Refresh token expired", "error.refresh.token.expired");
        codes.put("Refresh token missing", "error.refresh.token.missing");
        codes.put("Reset password email sent", "response.password.reset.email.sent");
        codes.put("Root category already exists", "error.root.category.exists");
        codes.put("Seller account is not active", "error.seller.account.inactive");
        codes.put("Seller activated successfully", "response.seller.status.activated");
        codes.put("Seller already active", "response.seller.status.already.active");
        codes.put("Seller already deactivated", "response.seller.status.already.deactivated");
        codes.put("Seller deactivated successfully", "response.seller.status.deactivated");
        codes.put("Seller is not approved", "error.seller.not.approved");
        codes.put("Seller registered successfully. Waiting for approval.", "response.seller.registered");
        codes.put("Status is required", "error.status.required");
        codes.put("Token expired", "error.token.expired");
        codes.put("Token not found", "error.token.not.found");
        codes.put("Unauthorized", "security.unauthorized.title");
        codes.put("Unauthorized access to product", "error.product.unauthorized");
        codes.put("User account is deleted", "error.user.account.deleted");
        codes.put("User account is expired", "error.user.account.expired");
        codes.put("User account is locked", "error.user.account.locked");
        codes.put("User account is not active", "error.user.account.inactive");
        codes.put("User is not a customer", "error.user.not.customer");
        codes.put("User is not a seller", "error.user.not.seller");
        codes.put("User not active", "error.user.not.active");
        codes.put("User not authenticated", "error.user.not.authenticated");
        codes.put("User not found", "error.user.not.found");
        codes.put("User with given email does not exist", "error.user.not.found.by.email");
        codes.put("Variation is deleted", "error.variation.deleted");
        codes.put("Variation not found", "error.variation.not.found");
        codes.put("You are not allowed to delete this product", "error.product.delete.not.allowed");
        codes.put("You are not allowed to update this product", "error.product.update.not.allowed");
        codes.put("You do not have permission to access this resource", "security.access.denied.message");
        codes.put("You do not own this product", "error.product.not.owned");
        codes.put("Access Denied", "security.access.denied.title");
        codes.put("'max' must be between 1 and 50", "error.seller.max.range50");
        codes.put("'offset' cannot be negative", "error.seller.offset.negative");
        codes.put("category not found", "error.category.not.found");
        codes.put("max must be between 1 and 100", "error.pagination.max.range");
        codes.put("offset cannot be negative", "error.pagination.offset.negative");
        codes.put("order must be asc or desc", "error.order.invalid");
        codes.put("productId cannot be combined with filters", "error.product.filter.productid.combined");
        codes.put("seller not found", "error.seller.not.found");
        codes.put("Customer role not found", "error.customer.role.not.found");
        codes.put("Seller role not found", "error.seller.role.not.found");

        return Map.copyOf(codes);
    }
}
