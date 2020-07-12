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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.annotation.DeterminableImports;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Class for storing auto-configuration packages for reference later (e.g. by JPA entity
 * scanner).
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Oliver Gierke
 * @since 1.0.0
 */
public abstract class AutoConfigurationPackages {

	private static final Log logger = LogFactory.getLog(AutoConfigurationPackages.class);

	//AutoConfigurationPackages
	private static final String BEAN = AutoConfigurationPackages.class.getName();

	/**
	 * Determine if the auto-configuration base packages for the given bean factory are
	 * available.
	 * 确定给定bean工厂的自动配置基础包是否可用。
	 * @param beanFactory the source bean factory  bean 工厂
	 * @return true if there are auto-config packages available  如果有自动配置包可用，则返回true
	 */
	public static boolean has(BeanFactory beanFactory) {
		//bean工厂包含AutoConfigurationPackages的bean，且
		return beanFactory.containsBean(BEAN) && !get(beanFactory).isEmpty();
	}

	/**
	 * Return the auto-configuration base packages for the given bean factory.
	 *
	 * 返回给定bean工厂的自动配置基本软件包。
	 * @param beanFactory the source bean factory   bean 工厂
	 * @return a list of auto-configuration packages   自动配置程序包列表
	 * @throws IllegalStateException if auto-configuration is not enabled
	 */
	public static List<String> get(BeanFactory beanFactory) {
		try {
			// 从bean工厂获取bean对象(BasePackages类)，然后获取被包装的基础包集合
			return beanFactory.getBean(BEAN, BasePackages.class).get();
		}
		catch (NoSuchBeanDefinitionException ex) {
			throw new IllegalStateException("Unable to retrieve @EnableAutoConfiguration base packages");
		}
	}

	/**
	 * Programmatically registers the auto-configuration package names. Subsequent
	 * invocations will add the given package names to those that have already been
	 * registered. You can use this method to manually define the base packages that will
	 * be used for a given {@link BeanDefinitionRegistry}. Generally it's recommended that
	 * you don't call this method directly, but instead rely on the default convention
	 * where the package name is set from your {@code @EnableAutoConfiguration}
	 * configuration class or classes.
	 * 以编程方式注册自动配置包名称。
	 * 后续调用会将给定的包名称添加到已注册的包名称中。
	 * 可以使用此方法来手动定义将用于给定{@link BeanDefinitionRegistry}的基本软件包。
	 * 通常，建议不要直接调用此方法，而应使用默认约定，该约定是从{@code @EnableAutoConfiguration}配置类或类中设置软件包名称的。
	 * @param registry the bean definition registry  Bean定义注册表
	 * @param packageNames the package names to set  要设置的包名称
	 */
	public static void register(BeanDefinitionRegistry registry, String... packageNames) {
		/*
		  它要判断当前IOC容器中是否包含 AutoConfigurationPackages 。
		   如果有，就会拿到刚才传入的包名，设置到一个 basePackage 里面。
		*/
		// 判断 BeanFactory 中是否包含 AutoConfigurationPackages
		if (registry.containsBeanDefinition(BEAN)) {
			// 获取AutoConfigurationPackages bean定义
			BeanDefinition beanDefinition = registry.getBeanDefinition(BEAN);
			// 获取构造参数
			ConstructorArgumentValues constructorArguments = beanDefinition.getConstructorArgumentValues();
			// addBasePackages：添加根包扫描包
			constructorArguments.addIndexedArgumentValue(0, addBasePackages(constructorArguments, packageNames));
		}
		else {
			/**
			 * 实际上，AutoConfigurationPackages 对应的 Bean 还没有创建，所以会走到这里，
			 * 直接把主启动类所在包放入 BasePackages 中，与上面 if 结构中最后一句一样，
			 * 都是调用 addIndexedArgumentValue 方法。那这个 BasePackages 中设置了构造器参数，一定会有对应的成员：
			 */
			GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
			//设置bean的class对象（基本包的持有类对象）
			beanDefinition.setBeanClass(BasePackages.class);
			beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, packageNames);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			//注册为AutoConfigurationPackages bean定义
			registry.registerBeanDefinition(BEAN, beanDefinition);
		}
	}

	private static String[] addBasePackages(ConstructorArgumentValues constructorArguments, String[] packageNames) {
		String[] existing = (String[]) constructorArguments.getIndexedArgumentValue(0, String[].class).getValue();
		Set<String> merged = new LinkedHashSet<>();
		merged.addAll(Arrays.asList(existing));
		merged.addAll(Arrays.asList(packageNames));
		return StringUtils.toStringArray(merged);
	}

	/**
	 * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
	 * configuration.
	 *
	 * 用于保存导入的配置类所在的根包。
	 *
	 * Registrar 实现了 ImportBeanDefinitionRegistrar 接口，它向IOC容器中要手动注册组件。
	 */
	static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

		//重写了registerBeanDefinitions方法
		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
			// 调用外部类 AutoConfigurationPackages 的register方法
			// 入参：new PackageImport(metadata).getPackageName()
			register(registry, new PackageImport(metadata).getPackageName());
		}

		@Override
		public Set<Object> determineImports(AnnotationMetadata metadata) {
			return Collections.singleton(new PackageImport(metadata));
		}

	}

	/**
	 * Wrapper for a package import.
	 * 包装导入包。
	 */
	private static final class PackageImport {

		private final String packageName;

		//实例化的 PackageImport 对象的构造方法
		PackageImport(AnnotationMetadata metadata) {
			// 获取了一个 metadata 的所在包名
			this.packageName = ClassUtils.getPackageName(metadata.getClassName());
		}

		public String getPackageName() {
			return this.packageName;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			return this.packageName.equals(((PackageImport) obj).packageName);
		}

		@Override
		public int hashCode() {
			return this.packageName.hashCode();
		}

		@Override
		public String toString() {
			return "Package Import " + this.packageName;
		}

	}

	/**
	 * Holder for the base package (name may be null to indicate no scanning).
	 * 基本包的持有类（名称可以为null，表示不进行扫描）。
	 */
	static final class BasePackages {

		//基础包集合
		private final List<String> packages;

		private boolean loggedBasePackageInfo;

		//构造函数
		BasePackages(String... names) {
			List<String> packages = new ArrayList<>();
			for (String name : names) {
				//包含除空格之外的数据
				if (StringUtils.hasText(name)) {
					packages.add(name);
				}
			}
			this.packages = packages;
		}

		//获取基础包集合
		public List<String> get() {
			if (!this.loggedBasePackageInfo) {
				if (this.packages.isEmpty()) {
					if (logger.isWarnEnabled()) {
						logger.warn("@EnableAutoConfiguration was declared on a class "
								+ "in the default package. Automatic @Repository and "
								+ "@Entity scanning is not enabled.");
					}
				}
				else {
					if (logger.isDebugEnabled()) {
						String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
						logger.debug("@EnableAutoConfiguration was declared on a class " + "in the package '"
								+ packageNames + "'. Automatic @Repository and @Entity scanning is " + "enabled.");
					}
				}
				this.loggedBasePackageInfo = true;
			}
			return this.packages;
		}

	}

}
