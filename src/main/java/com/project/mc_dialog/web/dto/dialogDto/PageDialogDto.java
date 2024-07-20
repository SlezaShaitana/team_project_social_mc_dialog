package com.project.mc_dialog.web.dto.dialogDto;

import com.project.mc_dialog.web.dto.PageableObject;
import com.project.mc_dialog.web.dto.Sort;
import lombok.Data;

@Data
public class PageDialogDto {

    private Integer totalPages;

    private Integer totalElements;

    private Sort sort;

    private Integer numberOfElements;

    private PageableObject pageable;

    private boolean first;

    private boolean last;

    private Integer size;

    private DialogDto content;

    private Integer number;

    private boolean empty;
}
