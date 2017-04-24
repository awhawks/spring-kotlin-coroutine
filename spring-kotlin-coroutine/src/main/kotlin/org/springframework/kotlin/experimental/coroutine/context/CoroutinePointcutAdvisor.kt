/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.kotlin.experimental.coroutine.context

import org.aopalliance.aop.Advice
import org.springframework.aop.Pointcut
import org.springframework.aop.support.AbstractPointcutAdvisor
import org.springframework.aop.support.ComposablePointcut
import org.springframework.aop.support.StaticMethodMatcher
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut
import org.springframework.beans.factory.BeanFactory
import org.springframework.kotlin.experimental.coroutine.annotation.Coroutine
import org.springframework.kotlin.experimental.coroutine.isSuspend
import java.lang.reflect.Method

internal open class CoroutinePointcutAdvisor(
        beanFactory: BeanFactory,
        resolvers: Set<CoroutineContextResolver>
) : AbstractPointcutAdvisor() {
    private val advice: Advice
    private val pointcut: Pointcut

    init {
        val classMatchingPointcut = AnnotationMatchingPointcut(Coroutine::class.java, true)
        val methodMatchingPointcut = AnnotationMatchingPointcut.forMethodAnnotation(Coroutine::class.java)
        pointcut = ComposablePointcut(classMatchingPointcut).union(methodMatchingPointcut).intersection(CoroutineMethodMatcher)

        advice = CoroutineMethodInterceptor(beanFactory, resolvers)
    }

    override fun getAdvice(): Advice = advice

    override fun getPointcut(): Pointcut = pointcut
}

private object CoroutineMethodMatcher: StaticMethodMatcher() {
    override fun matches(method: Method, targetClass: Class<*>) = method.isSuspend
}
