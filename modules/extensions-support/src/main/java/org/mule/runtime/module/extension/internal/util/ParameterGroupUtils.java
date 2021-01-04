/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.util;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.module.extension.api.loader.java.type.ExtensionParameter;
import org.mule.runtime.module.extension.internal.loader.utils.ParameterGroupInfo;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * ADD JDOCS
 */
public class ParameterGroupUtils {

  /**
   * ADD JDOC
   * @param annotations
   * @return
   */
  public static boolean hasParameterGroupAnnotation(Set<Class<? extends Annotation>> annotations) {
    return annotations.contains(ParameterGroup.class)
        || annotations.contains(org.mule.sdk.api.annotation.param.ParameterGroup.class);
  }

  /**
   * ADD JDOC
   * @param annotations
   * @return
   */
  public static Optional<Boolean> isParameterGroupShowInDsl(Map<Class<? extends Annotation>, Annotation> annotations) {
    if (annotations.containsKey(ParameterGroup.class)) {
      return of(((ParameterGroup) annotations.get(ParameterGroup.class)).showInDsl());
    }
    if (annotations.containsKey(org.mule.sdk.api.annotation.param.ParameterGroup.class)) {
      return of(((org.mule.sdk.api.annotation.param.ParameterGroup) annotations
          .get(org.mule.sdk.api.annotation.param.ParameterGroup.class)).showInDsl());
    }
    return empty();
  }

  /**
   *
   * @param extensionParameter
   * @return
   */
  public static Optional<ParameterGroupInfo> getParameterGroupInfo(ExtensionParameter extensionParameter) {
    Optional<ParameterGroup> legacyParameterGroupAnnotation = extensionParameter.getAnnotation(ParameterGroup.class);
    if (legacyParameterGroupAnnotation.isPresent()) {
      ParameterGroup legacyParameterGroupAnnotationObject = legacyParameterGroupAnnotation.get();
      return of(new ParameterGroupInfo(legacyParameterGroupAnnotationObject.name(),
                                       legacyParameterGroupAnnotationObject.showInDsl()));
    }
    Optional<org.mule.sdk.api.annotation.param.ParameterGroup> parameterGroupAnnotation =
        extensionParameter.getAnnotation(org.mule.sdk.api.annotation.param.ParameterGroup.class);
    if (parameterGroupAnnotation.isPresent()) {
      org.mule.sdk.api.annotation.param.ParameterGroup parameterGroupAnnotationObject = parameterGroupAnnotation.get();
      return of(new ParameterGroupInfo(parameterGroupAnnotationObject.name(), parameterGroupAnnotationObject.showInDsl()));
    }
    return empty();
  }
}
