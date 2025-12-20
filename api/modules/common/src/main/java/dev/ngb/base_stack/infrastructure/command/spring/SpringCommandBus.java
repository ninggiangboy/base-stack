package dev.ngb.base_stack.infrastructure.command.spring;

import dev.ngb.base_stack.application.command.Command;
import dev.ngb.base_stack.application.command.dispatcher.CommandBus;
import dev.ngb.base_stack.application.command.hander.CommandHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class SpringCommandBus implements CommandBus {

    private final Map<Class<? extends Command<?>>, CommandHandler<?, ?>> handlerMap;

    public SpringCommandBus(List<CommandHandler<?, ?>> handlers) {
        this.handlerMap = resolveHandlers(handlers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R dispatch(Command<R> command) {
        CommandHandler<Command<R>, R> handler =
                (CommandHandler<Command<R>, R>) handlerMap.get(command.getClass());

        if (handler == null) {
            throw new IllegalStateException(
                    "No handler found for command: " + command.getClass().getName()
            );
        }

        return handler.handle(command);
    }

    private Map<Class<? extends Command<?>>, CommandHandler<?, ?>> resolveHandlers(
            List<CommandHandler<?, ?>> handlers
    ) {
        Map<Class<? extends Command<?>>, CommandHandler<?, ?>> map = new HashMap<>();

        for (CommandHandler<?, ?> handler : handlers) {
            Class<?> handlerClass = handler.getClass();

            Type[] genericInterfaces = handlerClass.getGenericInterfaces();
            for (Type type : genericInterfaces) {
                if (!(type instanceof ParameterizedType parameterizedType)) {
                    continue;
                }

                if (!CommandHandler.class.equals(parameterizedType.getRawType())) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                Class<? extends Command<?>> commandType =
                        (Class<? extends Command<?>>) parameterizedType.getActualTypeArguments()[0];

                if (map.containsKey(commandType)) {
                    throw new IllegalStateException(
                            "Multiple handlers found for command: " + commandType.getName()
                    );
                }

                map.put(commandType, handler);
            }
        }

        return Map.copyOf(map);
    }
}
