package org.pipmycode.sbecomm.payload;

import java.util.List;

public record CategoryResponse (
        List<CategoryDTO> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPage,
        boolean lastPage
) {}

