/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Auto-configuration specific variant of Spring Framework's {@link Order} annotation.
 * Allows auto-configuration classes to be ordered among themselves without affecting the
 * order of configuration classes passed to
 * {@link AnnotationConfigApplicationContext#register(Class...)}.
 *
 * Spring框架的{@link Order}注解的自动配置特定变体。
 * 允许自动配置类在它们之间进行排序，而不会影响传递到{@link AnnotationConfigApplicationContext＃register（Class ...）}的配置类的顺序。
 * @author Andy Wilkinson
 * @since 1.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Documented
public @interface AutoConfigureOrder {

	// 默认次序
	int DEFAULT_ORDER = 0;

	/**
	 * The order value. Default is {@code 0}.
	 * 次序值(默认为0)
	 * @see Ordered#getOrder()
	 * @return the order value
	 */
	int value() default DEFAULT_ORDER;

}
