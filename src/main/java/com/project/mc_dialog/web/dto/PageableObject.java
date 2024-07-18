package com.project.mc_dialog.web.dto;

import lombok.Data;

@Data
public class PageableObject {

    private Sort sort;

    private boolean unpaged;

    private boolean paged;

    private Integer pageSize;

    private Integer pageNumber;

    private Integer offset;
}
