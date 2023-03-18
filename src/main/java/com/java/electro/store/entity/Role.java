package com.java.electro.store.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
public class Role {
    @Id
    private String roleId;

    private String roleName;

}
