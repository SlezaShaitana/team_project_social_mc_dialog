package com.project.mc_dialog.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageableObject {

    private Sort sort;

    private boolean unpaged;

    private boolean paged;

    private Integer pageSize;

    private Integer pageNumber;

    private Integer offset;
}
