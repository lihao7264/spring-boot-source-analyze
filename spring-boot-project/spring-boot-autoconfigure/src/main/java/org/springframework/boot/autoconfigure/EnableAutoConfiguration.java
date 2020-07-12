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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Enable auto-configuration of the Spring Application Context, attempting to guess and
 * configure beans that you are likely to need. Auto-configuration classes are usually
 * applied based on your classpath and what beans you have defined. For example, if you
 * have {@code tomcat-embedded.jar} on your classpath you are likely to want a
 * {@link TomcatServletWebServerFactory} (unless you have defined your own
 * {@link ServletWebServerFactory} bean).
 * <p>
 * When using {@link SpringBootApplication}, the auto-configuration of the context is
 * automatically enabled and adding this annotation has therefore no additional effect.
 * <p>
 * Auto-configuration tries to be as intelligent as possible and will back-away as you
 * define more of your own configuration. You can always manually {@link #exclude()} any
 * configuration that you never want to apply (use {@link #excludeName()} if you don't
 * have access to them). You can also exclude them via the
 * {@code spring.autoconfigure.exclude} property. Auto-configuration is always applied
 * after user-defined beans have been registered.
 * <p>
 * The package of the class that is annotated with {@code @EnableAutoConfiguration},
 * usually via {@code @SpringBootApplication}, has specific significance and is often used
 * as a 'default'. For example, it will be used when scanning for {@code @Entity} classes.
 * It is generally recommended that you place {@code @EnableAutoConfiguration} (if you're
 * not using {@code @SpringBootApplication}) in a root package so that all sub-packages
 * and classes can be searched.
 * <p>
 * Auto-configuration classes are regular Spring {@link Configuration} beans. They are
 * located using the {@link SpringFactoriesLoader} mechanism (keyed against this class).
 * Generally auto-configuration beans are {@link Conditional @Conditional} beans (most
 * often using {@link ConditionalOnClass @ConditionalOnClass} and
 * {@link ConditionalOnMissingBean @ConditionalOnMissingBean} annotations).
 *
 * 启用Spring-ApplicationContext的自动配置，并且会尝试猜测和配置您可能需要的Bean。
 * 通常根据您的类路径和定义的Bean来应用自动配置类。例如，如果您的类路径上有 tomcat-embedded.jar，
 * 则可能需要 TomcatServletWebServerFactory (除非自己已经定义了 ServletWebServerFactory 的Bean)。
 *
 * 使用 @SpringBootApplication 时，将自动启用上下文的自动配置，因此再添加该注解不会产生任何其它影响。
 * 自动配置会尝试尽可能地智能化，并且在您定义更多自定义配置时会自动退出（被覆盖）。
 * 您始终可以手动排除掉任何您不想应用的配置（如果您无法访问它们，请使用 excludeName() 方法），
 * 您也可以通过 spring.autoconfigure.exclude 属性排除它们。自动配置始终在注册用户自定义的Bean之后应用。
 *
 * 通常被 @EnableAutoConfiguration 标注的类（如 @SpringBootApplication）的包具有特定的意义，
 * 通常被用作"默认值"。例如，在扫描@Entity类时将使用它。通常建议您将 @EnableAutoConfiguration
 * （如果您未使用 @SpringBootApplication）放在根包中，以便可以搜索所有包及子包下的类。
 *
 * 自动配置类也是常规的Spring配置类。它们使用 SpringFactoriesLoader 机制定位（针对此类）。
 * 通常自动配置类也是 @Conditional Bean（最经常的情况下是使用 @ConditionalOnClass 和 @ConditionalOnMissingBean 标注）。
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @since 1.0.0
 * @see ConditionalOnBean
 * @see ConditionalOnMissingBean
 * @see ConditionalOnClass
 * @see AutoConfigureAfter
 * @see SpringBootApplication
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

	/**
	 * Exclude specific auto-configuration classes such that they will never be applied.
	 * @return the classes to exclude
	 */
	Class<?>[] exclude() default {};

	/**
	 * Exclude specific auto-configuration class names such that they will never be
	 * applied.
	 * @return the class names to exclude
	 * @since 1.3.0
	 */
	String[] excludeName() default {};

}
