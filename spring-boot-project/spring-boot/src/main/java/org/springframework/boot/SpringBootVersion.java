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

package org.springframework.boot;

/**
 * Class that exposes the Spring Boot version. Fetches the "Implementation-Version"
 * manifest attribute from the jar file.
 * <p>
 * Note that some ClassLoaders do not expose the package metadata, hence this class might
 * not be able to determine the Spring Boot version in all environments. Consider using a
 * reflection-based check instead: For example, checking for the presence of a specific
 * Spring Boot method that you intend to call.
 *
 * 公开Spring Boot版本的类。 从jar文件中获取"实现版本"属性。
 *
 * @author Drummond Dawson
 * @since 1.3.0
 */
public final class SpringBootVersion {

	private SpringBootVersion() {
	}

	/**
	 * Return the full version string of the present Spring Boot codebase, or {@code null}
	 * if it cannot be determined.
	 *
	 * 返回当前Spring Boot代码库的完整版本字符串，如果无法确定，则返回{@code null}。
	 *
	 * @return the version of Spring Boot or {@code null}  Spring Boot的版本或{@code null}
	 * @see Package#getImplementationVersion()
	 */
	public static String getVersion() {
		// 包名（org.springframework.boot）
		Package pkg = SpringBootVersion.class.getPackage();
		// 包名不为空，则返回版本号
		return (pkg != null) ? pkg.getImplementationVersion() : null;
	}
}
