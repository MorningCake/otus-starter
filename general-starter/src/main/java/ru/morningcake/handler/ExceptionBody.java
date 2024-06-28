package ru.morningcake.handler;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionBody {
    private List<Problem> problems;
    private String code;
    private String reason;
    private int count;
}
