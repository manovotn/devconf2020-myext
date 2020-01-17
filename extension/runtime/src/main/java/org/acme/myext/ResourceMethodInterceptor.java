package org.acme.myext;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.logging.Logger;

@ResourceIntercepted
@Priority(1)
@Interceptor
public class ResourceMethodInterceptor {

    private static final Logger LOGGER = Logger.getLogger(ResourceMethodInterceptor.class.getName());

    private static final String INTERCEPTED = "Your invocation got intercepted. ";

    @AroundInvoke
    Object aroundInvoke(InvocationContext context) throws Exception {
        LOGGER.infof("Intercepting resource method: %s", context.getMethod());
        return INTERCEPTED + context.proceed();
    }

}
