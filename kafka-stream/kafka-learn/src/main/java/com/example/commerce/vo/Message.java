package com.example.commerce.vo;

import lombok.*;

/**
 * 通过 kafka 传递的对象
 */
@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Integer id;
    private String projectName;
}
