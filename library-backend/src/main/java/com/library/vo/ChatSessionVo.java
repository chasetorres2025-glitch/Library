package com.library.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatSessionVo {

    private String sessionId;
    private String welcomeMessage;
    private List<QuickActionVo> quickActions;

}
