package com.library.dto;

import lombok.Data;

@Data
public class AssignTagRequest {

    private Long bookId;
    private Long tagId;
    private Double weight;

}
