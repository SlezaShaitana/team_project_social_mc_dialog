package com.project.mc_dialog.web.dto;

import lombok.Data;

@Data
public class PageMessageShortDto {

    private Integer totalPages;

    private Integer totalElements;

    private Sort sort;

    private Integer numberOfElements;

    private PageableObject pageable;

    private boolean first;

    private boolean last;

    private Integer size;

    private MessageShortDto content;

    private Integer number;

    private boolean empty;
}
