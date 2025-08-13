package org.libre.ai.api.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.libre.ai.exception.AuthException;
import org.libre.ai.modules.rag.store.AppChannelStore;
import org.libre.ai.modules.rag.utils.ServletUtil;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Aspect
@Configuration
@AllArgsConstructor
public class OpenapiAuthAspect {

    private final AppChannelStore channelStore;

    @Around("@annotation(openapiAuth)")
    public Object around(ProceedingJoinPoint point, OpenapiAuth openapiAuth) throws Throwable {
        String authorization = ServletUtil.getAuthorizationToken();

        if (authorization == null) {
            throw new AuthException(401, "Authentication Token invalid");
        }

        String value = openapiAuth.value();
        channelStore.isExpired(value);
        return point.proceed();
    }

}