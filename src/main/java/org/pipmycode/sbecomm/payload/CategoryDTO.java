package org.pipmycode.sbecomm.payload;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryDTO(

        Long categoryID,

        @NotBlank(message = "Category name is required")
        @Size(min = 5, max = 50, message = "Category name must be between 5 and 50 characters")
        String categoryName

) {}