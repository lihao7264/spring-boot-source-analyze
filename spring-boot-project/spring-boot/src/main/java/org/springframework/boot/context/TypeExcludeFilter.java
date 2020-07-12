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

package org.springframework.boot.context;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Provides exclusion {@link TypeFilter TypeFilters} that are loaded from the
 * {@link BeanFactory} and automatically applied to {@code SpringBootApplication}
 * scanning. Can also be used directly with {@code @ComponentScan} as follows:
 * <pre class="code">
 * &#064;ComponentScan(excludeFilters = @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class))
 * </pre>
 * <p>
 * Implementations should provide a subclass registered with {@link BeanFactory} and
 * override the {@link #match(MetadataReader, MetadataReaderFactory)} method. They should
 * also implement a valid {@link #hashCode() hashCode} and {@link #equals(Object) equals}
 * methods so that they can be used as part of Spring test's application context caches.
 * <p>
 * Note that {@code TypeExcludeFilters} are initialized very early in the application
 * lifecycle, they should generally not have dependencies on any other beans. They are
 * primarily used internally to support {@code spring-boot-test}.
 *
 * @author Phillip Webb
 * @since 1.4.0
 *
 * 1、提供从 BeanFactory 加载并自动应用于 @SpringBootApplication 扫描的排除 TypeFilter。
 * 2、实现应提供一个向 BeanFactory 注册的子类，
 *   并重写 match(MetadataReader, MetadataReaderFactory) 方法。
 *   它们还应该实现一个有效的 hashCode 和 equals 方法，
 *   以便可以将它们用作Spring测试的应用程序上下文缓存的一部分。
 * 3、注意：TypeExcludeFilters 在应用程序生命周期的很早就初始化了，
 *         它们通常不应该依赖于任何其他bean。
 *         它们主要在内部用于支持 spring-boot-test 。
 *
 * 总结：它是给了一种扩展机制，能让我们向IOC容器中注册一些自定义的组件过滤器，以在包扫描的过程中过滤它们。
 *       TypeExcludeFilter 的作用是做扩展的组件过滤。
 */
public class TypeExcludeFilter implements TypeFilter, BeanFactoryAware {

	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * 核心方法：实现了过滤的判断逻辑
	 * @param metadataReader
	 * @param metadataReaderFactory
	 * @return
	 * @throws IOException
	 */
	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {
		// 会从 BeanFactory（可以暂时理解成IOC容器）中获取所有类型为 TypeExcludeFilter 的组件
		if (this.beanFactory instanceof ListableBeanFactory && getClass() == TypeExcludeFilter.class) {
			Collection<TypeExcludeFilter> delegates = ((ListableBeanFactory) this.beanFactory)
					.getBeansOfType(TypeExcludeFilter.class).values();
			// 遍历所有的 TypeExcludeFilter 的组件
			for (TypeExcludeFilter delegate : delegates) {
				// 去执行自定义的过滤方法。
				if (delegate.match(metadataReader, metadataReaderFactory)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		throw new IllegalStateException("TypeExcludeFilter " + getClass() + " has not implemented equals");
	}

	@Override
	public int hashCode() {
		throw new IllegalStateException("TypeExcludeFilter " + getClass() + " has not implemented hashCode");
	}

}
