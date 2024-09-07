package com.project.mc_dialog.web.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pageable {

    @Min(value = 0, message = "Значение page должно быть больше или равным 0.")
    private Integer page;

    @Min(value = 1, message = "Значение size должно быть больше или равным 1.")
    private Integer size;

    private List<String> sort;
}
