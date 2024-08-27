package com.project.mc_dialog.utils;

import org.springframework.data.domain.Sort;

import java.util.List;

public class SortUtils {

    public static Sort getSortFromList(List<String> sortFields) {
        if (sortFields == null || sortFields.isEmpty()) {
            return Sort.unsorted();
        }

        Sort sort = Sort.by(sortFields.get(0));
        for (int i = 1; i < sortFields.size(); i++) {
            if (sortFields.get(i).equals("desc")){
                sort = sort.descending();
            } else if (sortFields.get(i).equals("asc")) {
                sort = sort.ascending();
            } else {
                sort = sort.and(Sort.by(sortFields.get(i)));
            }
        }
        return sort;
    }
}
