package dev.ngb.base_stack.system;

import dev.ngb.base_stack.application.shared.ApplicationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = "dev.ngb.base_stack",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = ApplicationService.class
        )
)
public class BaseStackSystemApplication {
    static void main(String[] args) {
        SpringApplication.run(BaseStackSystemApplication.class, args);
    }
}
