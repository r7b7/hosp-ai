package com.r7b7.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Content(String type, String text, String id, String name, Object input) {}
