package org.silencer.ai.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;

public class DiscardThinkLabelBeanOutputConverter<T> extends BeanOutputConverter<T> {
    public DiscardThinkLabelBeanOutputConverter(Class<T> clazz) {
        super(clazz);
    }

    public DiscardThinkLabelBeanOutputConverter(Class<T> clazz, ObjectMapper objectMapper) {
        super(clazz, objectMapper);
    }

    public DiscardThinkLabelBeanOutputConverter(ParameterizedTypeReference<T> typeRef) {
        super(typeRef);
    }

    public DiscardThinkLabelBeanOutputConverter(ParameterizedTypeReference<T> typeRef, ObjectMapper objectMapper) {
        super(typeRef, objectMapper);
    }

    @Override
    public T convert(String text) {
        text = text.trim();
        if (StringUtils.contains(text, "</think>")) {
            text = StringUtils.substringAfter(text, "</think>");
        }
        return super.convert(text);
    }
}
