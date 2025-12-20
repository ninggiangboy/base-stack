package dev.ngb.base_stack.infrastructure.config.transaction;

import dev.ngb.base_stack.application.shared.annonation.TransactionalScope;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class TransactionAdvisorConfig {

    private final TransactionManager txManager;

    @Bean
    public Advisor transactionScopeAdvisor() {
        TransactionAttributeSource txSource = (method, targetClass) -> {
            TransactionalScope annotation = AnnotatedElementUtils.findMergedAnnotation(method, TransactionalScope.class);
            if (annotation == null && targetClass != null) {
                annotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, TransactionalScope.class);
            }

            RuleBasedTransactionAttribute txAttr = new RuleBasedTransactionAttribute();
            txAttr.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);

            txAttr.setReadOnly(annotation != null && annotation.type() == TransactionalScope.Type.READ);

            return txAttr;
        };

        TransactionInterceptor interceptor = new TransactionInterceptor(txManager, txSource);

        return new DefaultPointcutAdvisor(
                new AnnotationMatchingPointcut(TransactionalScope.class, true),
                interceptor
        );
    }
}
