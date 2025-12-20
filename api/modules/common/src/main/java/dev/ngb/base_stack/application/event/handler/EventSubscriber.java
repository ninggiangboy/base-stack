package dev.ngb.base_stack.application.event.handler;

import dev.ngb.base_stack.application.event.Event;
import dev.ngb.base_stack.application.shared.ApplicationService;

public interface EventSubscriber<E extends Event> extends ApplicationService {
    void on(E event);
}
