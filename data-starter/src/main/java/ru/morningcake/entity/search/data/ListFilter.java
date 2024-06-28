package ru.morningcake.entity.search.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ListFilter {
    private String attribute;
    private Object valueOne;
    private Object valueSecond;
    private FilterOperation operation;
    private List<ListFilter> innerFilters = List.of();
}
