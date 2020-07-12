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

package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * {@link Conditional} that matches when the application is a web application. By default,
 * any web application will match but it can be narrowed using the {@link #type()}
 * attribute.
 *
 * 当应用程序是Web应用程序时匹配的{@link Conditional}。
 * 默认情况下，任何Web应用程序都会匹配，但可以使用{@link #type（）}属性来缩小范围。
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @since 1.0.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 当前运行环境的classpath中必须有OnWebApplicationCondition类
@Conditional(OnWebApplicationCondition.class)
public @interface ConditionalOnWebApplication {

	/**
	 * The required type of the web application.
	 * Web应用程序的必需类型。
	 * @return the required web application type   Web应用程序的必需类型。(默认是为)
	 */
	Type type() default Type.ANY;

	/**
	 * Available application types.
	 * 可用的应用程序类型。
	 */
	enum Type {

		/**
		 * Any web application will match.
		 * 任何Web应用程序都将匹配。
		 */
		ANY,

		/**
		 * Only servlet-based web application will match.
		 * 仅基于servlet的Web应用程序将匹配。
		 */
		SERVLET,

		/**
		 * Only reactive-based web application will match.
		 * 仅基于reactive的Web应用程序将匹配。
		 */
		REACTIVE

	}

}
