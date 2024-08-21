package com.bk.ms.kafka.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    private String name;
    private Long sal;
}
