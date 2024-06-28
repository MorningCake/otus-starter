package ru.morningcake.handler;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    private String message;
    private String invalidValue;
    private String propertyPath;
}
