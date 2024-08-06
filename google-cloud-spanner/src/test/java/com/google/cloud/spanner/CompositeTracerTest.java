/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner;

import static com.google.api.gax.util.TimeConversionUtils.toJavaTimeDuration;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.gax.tracing.ApiTracer;
import com.google.api.gax.tracing.ApiTracer.Scope;
import com.google.api.gax.tracing.MetricsTracer;
import com.google.common.collect.ImmutableList;
import com.google.spanner.v1.ReadRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.threeten.bp.Duration;

@RunWith(JUnit4.class)
public class CompositeTracerTest {
  @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock private ApiTracer child1;
  @Mock private ApiTracer child2;
  @Mock private OpenTelemetryApiTracer child3;
  @Mock private MetricsTracer child4;

  private CompositeTracer compositeTracer;

  @Before
  public void setup() {
    compositeTracer = new CompositeTracer(ImmutableList.of(child1, child2, child3, child4));
  }

  @Test
  public void testInScope() {
    Scope scope1 = mock(Scope.class);
    when(child1.inScope()).thenReturn(scope1);

    Scope scope2 = mock(Scope.class);
    when(child2.inScope()).thenReturn(scope2);

    Scope scope3 = mock(Scope.class);
    when(child3.inScope()).thenReturn(scope3);

    Scope scope4 = mock(Scope.class);
    when(child4.inScope()).thenReturn(scope4);

    Scope parentScope = compositeTracer.inScope();

    parentScope.close();
    verify(scope1, times(1)).close();
    verify(scope2, times(1)).close();
    verify(scope3, times(1)).close();
    verify(scope4, times(1)).close();
  }

  @Test
  public void testOperationSucceeded() {
    compositeTracer.operationSucceeded();
    verify(child1, times(1)).operationSucceeded();
    verify(child2, times(1)).operationSucceeded();
    verify(child3, times(1)).operationSucceeded();
    verify(child4, times(1)).operationSucceeded();
  }

  @Test
  public void testOperationCancelled() {
    compositeTracer.operationCancelled();
    verify(child1, times(1)).operationCancelled();
    verify(child2, times(1)).operationCancelled();
    verify(child3, times(1)).operationCancelled();
    verify(child4, times(1)).operationCancelled();
  }

  @Test
  public void testOperationFailed() {
    RuntimeException error = new RuntimeException();
    compositeTracer.operationFailed(error);
    verify(child1, times(1)).operationFailed(error);
    verify(child2, times(1)).operationFailed(error);
    verify(child3, times(1)).operationFailed(error);
    verify(child4, times(1)).operationFailed(error);
  }

  @Test
  public void testConnectionSelected() {
    compositeTracer.connectionSelected("connection-one");
    verify(child1, times(1)).connectionSelected("connection-one");
    verify(child2, times(1)).connectionSelected("connection-one");
    verify(child3, times(1)).connectionSelected("connection-one");
    verify(child4, times(1)).connectionSelected("connection-one");
  }

  @Test
  public void testAttemptStarted() {
    ReadRequest request = ReadRequest.getDefaultInstance();
    compositeTracer.attemptStarted(request, 3);
    verify(child1, times(1)).attemptStarted(request, 3);
    verify(child2, times(1)).attemptStarted(request, 3);
    verify(child3, times(1)).attemptStarted(request, 3);
    verify(child4, times(1)).attemptStarted(request, 3);
  }

  @Test
  public void testAttemptSucceeded() {
    compositeTracer.attemptSucceeded();
    verify(child1, times(1)).attemptSucceeded();
    verify(child2, times(1)).attemptSucceeded();
    verify(child3, times(1)).attemptSucceeded();
    verify(child4, times(1)).attemptSucceeded();
  }

  @Test
  public void testAttemptCancelled() {
    compositeTracer.attemptCancelled();
    verify(child1, times(1)).attemptCancelled();
    verify(child2, times(1)).attemptCancelled();
    verify(child3, times(1)).attemptCancelled();
    verify(child4, times(1)).attemptCancelled();
  }

  @Test
  public void testAttemptFailed() {
    RuntimeException error = new RuntimeException();
    Duration delay = Duration.ofMillis(10);
    compositeTracer.attemptFailed(error, delay);

    // CompositeTracer's attemptFailed calls the attemptFailedDuration method. This was part of
    // the java.time migration effort to move away from threetenbp. Verify that each child tracer
    // calls attemptFailedDuration once.
    java.time.Duration delayDuration = toJavaTimeDuration(delay);
    verify(child1, times(1)).attemptFailedDuration(error, delayDuration);
    verify(child2, times(1)).attemptFailedDuration(error, delayDuration);
    verify(child3, times(1)).attemptFailedDuration(error, delayDuration);
    verify(child4, times(1)).attemptFailedDuration(error, delayDuration);
  }

  @Test
  public void testAttemptFailedRetriesExhausted() {
    RuntimeException error = new RuntimeException();
    compositeTracer.attemptFailedRetriesExhausted(error);
    verify(child1, times(1)).attemptFailedRetriesExhausted(error);
    verify(child2, times(1)).attemptFailedRetriesExhausted(error);
    verify(child3, times(1)).attemptFailedRetriesExhausted(error);
    verify(child4, times(1)).attemptFailedRetriesExhausted(error);
  }

  @Test
  public void testAttemptPermanentFailure() {
    RuntimeException error = new RuntimeException();
    compositeTracer.attemptPermanentFailure(error);
    verify(child1, times(1)).attemptPermanentFailure(error);
    verify(child2, times(1)).attemptPermanentFailure(error);
    verify(child3, times(1)).attemptPermanentFailure(error);
    verify(child4, times(1)).attemptPermanentFailure(error);
  }

  @Test
  public void testLroStartFailed() {
    RuntimeException error = new RuntimeException();
    compositeTracer.lroStartFailed(error);
    verify(child1, times(1)).lroStartFailed(error);
    verify(child2, times(1)).lroStartFailed(error);
    verify(child3, times(1)).lroStartFailed(error);
    verify(child4, times(1)).lroStartFailed(error);
  }

  @Test
  public void testLroStartSucceeded() {
    compositeTracer.lroStartSucceeded();
    verify(child1, times(1)).lroStartSucceeded();
    verify(child2, times(1)).lroStartSucceeded();
    verify(child3, times(1)).lroStartSucceeded();
    verify(child4, times(1)).lroStartSucceeded();
  }

  @Test
  public void testResponseReceived() {
    compositeTracer.responseReceived();
    verify(child1, times(1)).responseReceived();
    verify(child2, times(1)).responseReceived();
    verify(child3, times(1)).responseReceived();
    verify(child4, times(1)).responseReceived();
  }

  @Test
  public void testRequestSent() {
    compositeTracer.requestSent();
    verify(child1, times(1)).requestSent();
    verify(child2, times(1)).requestSent();
    verify(child3, times(1)).requestSent();
    verify(child4, times(1)).requestSent();
  }

  @Test
  public void testBatchRequestSent() {
    compositeTracer.batchRequestSent(2, 20);
    verify(child1, times(1)).batchRequestSent(2, 20);
    verify(child2, times(1)).batchRequestSent(2, 20);
    verify(child3, times(1)).batchRequestSent(2, 20);
    verify(child4, times(1)).batchRequestSent(2, 20);
  }

  @Test
  public void testMethodsOverrideMetricsTracer() {
    Method[] metricsTracerMethods = MetricsTracer.class.getDeclaredMethods();
    Method[] compositeTracerMethods = CompositeTracer.class.getDeclaredMethods();

    List<String> visibleForTestingMethods = Arrays.asList("getAttributes", "extractStatus");

    Set<Method> compositeMethodsSet = new HashSet<>(Arrays.asList(compositeTracerMethods));

    for (Method metricsMethod : metricsTracerMethods) {
      if (!visibleForTestingMethods.contains(metricsMethod.getName())
          && !containsMethod(compositeMethodsSet, metricsMethod)) {
        throw new AssertionError("Method not found in compositeTracerMethods: " + metricsMethod);
      }
    }
  }

  @Test
  public void testMethodsOverrideOpenTelemetryTracer() {

    Method[] compositeTracerMethods = CompositeTracer.class.getDeclaredMethods();

    List<Method> openTelemetryTracerMethods =
        Arrays.stream(OpenTelemetryApiTracer.class.getDeclaredMethods())
            .filter(method -> java.lang.reflect.Modifier.isPublic(method.getModifiers()))
            .collect(Collectors.toList());

    Set<Method> compositeMethodsSet = new HashSet<>(Arrays.asList(compositeTracerMethods));

    for (Method metricsMethod : openTelemetryTracerMethods) {
      if (!containsMethod(compositeMethodsSet, metricsMethod)) {
        throw new AssertionError("Method not found in compositeTracerMethods: " + metricsMethod);
      }
    }
  }

  private boolean compareMethods(Method actual, Method expected) {
    return actual.getName().equals(expected.getName())
        && Arrays.equals(actual.getParameterTypes(), expected.getParameterTypes())
        && actual.getModifiers() == expected.getModifiers()
        && actual.getReturnType().equals(expected.getReturnType());
  }

  public boolean containsMethod(Set<Method> methodSet, Method method) {
    for (Method m : methodSet) {
      if (compareMethods(m, method)) {
        return true;
      }
    }
    return false;
  }
}
