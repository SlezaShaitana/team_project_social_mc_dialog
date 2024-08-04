package com.project.mc_dialog.web.dto.messageDto;

import com.project.mc_dialog.web.dto.PageableObject;
import com.project.mc_dialog.web.dto.Sort;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageMessageShortDto {

    private Integer totalPages;

    private Long totalElements;

    private Sort sort;

    private Integer numberOfElements;

    private PageableObject pageable;

    private boolean first;

    private boolean last;

    private Integer size;

    private List<MessageShortDto> content;

    private Integer number;

    private boolean empty;
}
