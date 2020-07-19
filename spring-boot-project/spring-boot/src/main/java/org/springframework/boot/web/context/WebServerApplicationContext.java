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

package org.springframework.boot.web.context;

import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;

/**
 * Interface to be implemented by {@link ApplicationContext application contexts} that
 * create and manage the lifecycle of an embedded {@link WebServer}.
 * 由创建和管理嵌入式Web服务器的生命周期的应用程序上下文实现的接口。
 *
 *  WebServerApplicationContext有有5个实现类，一个子接口。
 * @author Phillip Webb
 * @since 2.0.0
 */
public interface WebServerApplicationContext extends ApplicationContext {

	/**
	 * Returns the {@link WebServer} that was created by the context or {@code null} if
	 * the server has not yet been created.
	 * 返回由上下文创建的{@link WebServer}；如果尚未创建服务器，则返回{@code null}。
	 * @return the web server
	 */
	WebServer getWebServer();

	/**
	 * Returns the namespace of the web server application context or {@code null} if no
	 * namespace has been set. Used for disambiguation when multiple web servers are
	 * running in the same application (for example a management context running on a
	 * different port).
	 *
	 * 返回Web服务器应用程序上下文的名称空间；
	 * 如果未设置名称空间，则返回{@code null}。
	 * 当多个Web服务器在同一应用程序中运行时（例如，在不同端口上运行的管理上下文），用于消除歧义。
	 * @return the server namespace
	 */
	String getServerNamespace();

	/**
	 * Returns {@code true} if the specified context is a
	 * {@link WebServerApplicationContext} with a matching server namespace.
	 * 如果指定的上下文是具有匹配服务器名称空间的{@link WebServerApplicationContext}，则返回{@code true}。
	 * @param context the context to check
	 * @param serverNamespace the server namespace to match against
	 * @return {@code true} if the server namespace of the context matches
	 * @since 2.1.8
	 */
	static boolean hasServerNamespace(ApplicationContext context, String serverNamespace) {
		// 上下文是WebServerApplicationContext类或者其子类且服务器名称空间也相等
		return (context instanceof WebServerApplicationContext) && ObjectUtils
				.nullSafeEquals(((WebServerApplicationContext) context).getServerNamespace(), serverNamespace);
	}

}
