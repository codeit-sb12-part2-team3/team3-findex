package com.codeit.findex.domain.indexdata.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SourceType {
    USER ("사용자","사용자로부터 data update"),
    OPENAPI("Open API", "Open API로부터 data update");

    private final String name;
    private final String description;
}
