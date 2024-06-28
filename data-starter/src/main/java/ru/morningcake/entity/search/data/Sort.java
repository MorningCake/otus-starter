package ru.morningcake.entity.search.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Sort {
    private String attribute;
    private Order order;

    public enum Order {
        ASC,
        DESC
    }
}
