/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.resolver;

import static org.mule.runtime.module.extension.api.runtime.privileged.ExecutionContextProperties.COMPLETION_CALLBACK_CONTEXT_PARAM;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.extension.api.runtime.operation.ExecutionContext;
import org.mule.runtime.module.extension.internal.runtime.execution.adapter.SdkVoidCompletionCallbackAdapter;
import org.mule.sdk.api.runtime.process.VoidCompletionCallback;;

/**
 * ADD JDOC
 */
public final class SdkVoidCallbackArgumentResolver implements ArgumentResolver<VoidCompletionCallback> {

  private final ArgumentResolver<org.mule.runtime.extension.api.runtime.process.VoidCompletionCallback> voidCompletionCallbackArgumentResolver;

  public SdkVoidCallbackArgumentResolver(ArgumentResolver<org.mule.runtime.extension.api.runtime.process.VoidCompletionCallback> voidCompletionCallbackArgumentResolver) {
    this.voidCompletionCallbackArgumentResolver = voidCompletionCallbackArgumentResolver;
  }

  @Override
  public VoidCompletionCallback resolve(ExecutionContext executionContext) {
    org.mule.runtime.extension.api.runtime.process.VoidCompletionCallback voidCompletionCallback =
        voidCompletionCallbackArgumentResolver.resolve(executionContext);
    return voidCompletionCallback == null ? null : new SdkVoidCompletionCallbackAdapter(voidCompletionCallback);
  }
}
