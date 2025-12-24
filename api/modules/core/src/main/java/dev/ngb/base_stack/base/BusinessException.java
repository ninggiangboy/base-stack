package dev.ngb.base_stack.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class BusinessException extends RuntimeException {
    private final DomainError error;
    private Map<String, Object> data;
}
