package com.project.mc_dialog.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sort {

    private boolean sorted;

    private boolean unsorted;

    private boolean empty;
}
