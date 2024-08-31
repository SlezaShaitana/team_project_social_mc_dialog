package com.project.mc_dialog.utils;

import com.project.mc_dialog.web.dto.Pageable;
import org.springframework.data.domain.PageRequest;

public class PageableUtils {

    public static org.springframework.data.domain.Pageable getPageable(Pageable pageableDto, org.springframework.data.domain.Sort sort) {
        org.springframework.data.domain.Pageable pageable;

        if (pageableDto.getSize() == null && pageableDto.getPage() == null) {
            pageable = PageRequest.of(0,10, sort);
        } else if (pageableDto.getSize() == null) {
            pageable = PageRequest.of(pageableDto.getPage(), 10, sort);
        } else if (pageableDto.getPage() == null) {
            pageable = PageRequest.of(0, pageableDto.getSize(), sort);
        } else {
            pageable = PageRequest.of(pageableDto.getPage(),
                    pageableDto.getSize(), sort);
        }
        return pageable;
    }
}
