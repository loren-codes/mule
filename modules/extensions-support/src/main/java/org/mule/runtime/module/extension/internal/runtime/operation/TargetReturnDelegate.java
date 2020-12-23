/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.operation;

import static org.mule.runtime.api.el.BindingContextUtils.getTargetBindingContext;
import static org.mule.runtime.core.api.util.StreamingUtils.updateTypedValueForStreaming;

import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.meta.model.ComponentModel;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.streaming.CursorProviderFactory;
import org.mule.runtime.core.api.streaming.StreamingManager;
import org.mule.runtime.module.extension.api.runtime.privileged.ExecutionContextAdapter;

/**
 * An implementation of {@link AbstractReturnDelegate} which sets the output message on a variable which key is taken from the
 * {@link #target} field.
 * <p>
 * The target variable will always contain a {@link Message}, even if the operation returned a simple value
 * <p>
 * The original message is left untouched.
 *
 * @since 4.0
 */
final class TargetReturnDelegate extends AbstractReturnDelegate {

  private final ExpressionManager expressionManager;

  private final String target;
  private final String targetValue;
  private final StreamingManager streamingManager;

  /**
   * {@inheritDoc}
   *
   * @param target the name of the variable in which the output message will be set
   */
  TargetReturnDelegate(String target,
                       String targetValue,
                       ComponentModel componentModel,
                       ExpressionManager expressionManager,
                       CursorProviderFactory cursorProviderFactory,
                       MuleContext muleContext,
                       StreamingManager streamingManager) {
    super(componentModel, cursorProviderFactory, muleContext);
    this.expressionManager = expressionManager;
    this.target = target;
    this.targetValue = targetValue;
    this.streamingManager = streamingManager;
  }

  @Override
  public CoreEvent asReturnValue(Object value, ExecutionContextAdapter operationContext) {
    CoreEvent event = operationContext.getEvent();
    TypedValue typedValue = expressionManager
        .evaluate(targetValue, getTargetBindingContext(toMessage(value, operationContext)));
    TypedValue managedTypedValue = updateTypedValueForStreaming(typedValue, event, streamingManager);
    return CoreEvent.builder(event)
        .securityContext(operationContext.getSecurityContext())
        .addVariable(this.target, managedTypedValue.getValue(), managedTypedValue.getDataType())
        .build();
  }
}
