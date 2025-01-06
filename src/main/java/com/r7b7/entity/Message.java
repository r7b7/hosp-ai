package com.r7b7.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Message(Role role, Object content, Object images) {

    public Message {
        if (role.equals(Role.system) && !(content instanceof String)) {
            throw new IllegalArgumentException("System messages should be declared of type String");
        }
    }

    public Message(Role role, Object content) {
        this(role, content, null);
    }
}
