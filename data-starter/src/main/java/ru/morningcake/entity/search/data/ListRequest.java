package ru.morningcake.entity.search.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ListRequest {
    private int page;
    private int limit;
    private Sort sort;
    private List<Sort> multisort;
    private List<ListFilter> filters = List.of();
    private String search;
}
