package ru.morningcake.entity.search.data;

public enum FilterOperation {
    EQ,
    NE,
    LT,
    GT,
    LTE,
    GTE,
    BETWEEN,
    LIKE,
    LIKE_IGNORE_CASE,
    IN,
    NOT_IN,
    LIKE_END,
    LIKE_END_IGNORE_CASE,
    LIKE_START,
    LIKE_START_IGNORE_CASE,
    NOT_NULL,
    IS_NULL,
    OR,
    AND,
    NOT_LIKE,
    NOT_LIKE_IGNORE_CASE,
    LIST_IS_EMPTY,
    LIST_IS_NOT_EMPTY
}
