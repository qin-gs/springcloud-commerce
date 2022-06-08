package com.example.commerce.vo;

import lombok.*;

/**
 * 消息传递对象
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageObj {

    private Integer id;
    private String projectName;
    private String org;
    private String author;
    private String version;

    public static MessageObj defaultMessage() {
        return MessageObj.builder()
                .id(1)
                .projectName("projectName")
                .author("qqq")
                .org("org")
                .author("author")
                .version("1.0")
                .build();
    }
}
