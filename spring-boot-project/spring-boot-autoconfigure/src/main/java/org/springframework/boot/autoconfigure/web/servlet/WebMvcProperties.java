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

package org.springframework.boot.autoconfigure.web.servlet;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.validation.DefaultMessageCodesResolver;

/**
 * {@link ConfigurationProperties properties} for Spring MVC.
 * Spring MVC的{@link ConfigurationProperties 属性}。
 * 前缀是spring.mvc的配置
 *
 * @author Phillip Webb
 * @author Sébastien Deleuze
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 * @author Brian Clozel
 * @since 2.0.0
 */
@ConfigurationProperties(prefix = "spring.mvc")
public class WebMvcProperties {

	/**
	 * Formatting strategy for message codes. For instance, `PREFIX_ERROR_CODE`.
	 * 消息代码的格式化策略。 例如，`PREFIX_ERROR_CODE`。
	 */
	private DefaultMessageCodesResolver.Format messageCodesResolverFormat;

	/**
	 * Locale to use. By default, this locale is overridden by the "Accept-Language"
	 * header.
	 * 使用的语言环境。 默认情况下，此语言环境被“ Accept-Language”标头覆盖。
	 */
	private Locale locale;

	/**
	 * Define how the locale should be resolved.
	 * 定义如何解析语言环境。
	 */
	private LocaleResolver localeResolver = LocaleResolver.ACCEPT_HEADER;

	/**
	 * Date format to use. For instance, `dd/MM/yyyy`.
	 * 要使用的日期格式。 例如，“ dd/MM/yyyy”。
	 */
	private String dateFormat;

	/**
	 * Whether to dispatch TRACE requests to the FrameworkServlet doService method.
	 * 是否将TRACE请求调度到FrameworkServlet doService方法。
	 */
	private boolean dispatchTraceRequest = false;

	/**
	 * Whether to dispatch OPTIONS requests to the FrameworkServlet doService method.
	 * 是否将OPTIONS请求分派到FrameworkServlet doService方法。
	 */
	private boolean dispatchOptionsRequest = true;

	/**
	 * Whether the content of the "default" model should be ignored during redirect
	 * scenarios.
	 * 在重定向时是否忽略默认model的内容，默认为true
	 */
	private boolean ignoreDefaultModelOnRedirect = true;

	/**
	 * Whether a "NoHandlerFoundException" should be thrown if no Handler was found to
	 * process a request.
	 * 如果未找到处理请求的处理程序，是否应该抛出“ NoHandlerFoundException”。
	 */
	private boolean throwExceptionIfNoHandlerFound = false;

	/**
	 * Whether to enable warn logging of exceptions resolved by a
	 * "HandlerExceptionResolver", except for "DefaultHandlerExceptionResolver".
	 * 是否启用"HandlerExceptionResolver"解决的异常的警告日志记录，
	 * "DefaultHandlerExceptionResolver"除外。
	 */
	private boolean logResolvedException = false;

	/**
	 * Path pattern used for static resources.
	 * 用于静态资源的路径模式。
	 */
	private String staticPathPattern = "/**";

	private final Async async = new Async();

	private final Servlet servlet = new Servlet();

	private final View view = new View();

	private final Contentnegotiation contentnegotiation = new Contentnegotiation();

	private final Pathmatch pathmatch = new Pathmatch();

	public DefaultMessageCodesResolver.Format getMessageCodesResolverFormat() {
		return this.messageCodesResolverFormat;
	}

	public void setMessageCodesResolverFormat(DefaultMessageCodesResolver.Format messageCodesResolverFormat) {
		this.messageCodesResolverFormat = messageCodesResolverFormat;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public LocaleResolver getLocaleResolver() {
		return this.localeResolver;
	}

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}

	public String getDateFormat() {
		return this.dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isIgnoreDefaultModelOnRedirect() {
		return this.ignoreDefaultModelOnRedirect;
	}

	public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
		this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
	}

	public boolean isThrowExceptionIfNoHandlerFound() {
		return this.throwExceptionIfNoHandlerFound;
	}

	public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
		this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
	}

	public boolean isLogResolvedException() {
		return this.logResolvedException;
	}

	public void setLogResolvedException(boolean logResolvedException) {
		this.logResolvedException = logResolvedException;
	}

	public boolean isDispatchOptionsRequest() {
		return this.dispatchOptionsRequest;
	}

	public void setDispatchOptionsRequest(boolean dispatchOptionsRequest) {
		this.dispatchOptionsRequest = dispatchOptionsRequest;
	}

	public boolean isDispatchTraceRequest() {
		return this.dispatchTraceRequest;
	}

	public void setDispatchTraceRequest(boolean dispatchTraceRequest) {
		this.dispatchTraceRequest = dispatchTraceRequest;
	}

	public String getStaticPathPattern() {
		return this.staticPathPattern;
	}

	public void setStaticPathPattern(String staticPathPattern) {
		this.staticPathPattern = staticPathPattern;
	}

	public Async getAsync() {
		return this.async;
	}

	public Servlet getServlet() {
		return this.servlet;
	}

	public View getView() {
		return this.view;
	}

	public Contentnegotiation getContentnegotiation() {
		return this.contentnegotiation;
	}

	public Pathmatch getPathmatch() {
		return this.pathmatch;
	}

	public static class Async {

		/**
		 * Amount of time before asynchronous request handling times out. If this value is
		 * not set, the default timeout of the underlying implementation is used.
		 * 异步请求处理超时之前的时间。
		 * 如果未设置此值，则使用基础实现的默认超时。
		 *
		 */
		private Duration requestTimeout;

		public Duration getRequestTimeout() {
			return this.requestTimeout;
		}

		public void setRequestTimeout(Duration requestTimeout) {
			this.requestTimeout = requestTimeout;
		}

	}

	public static class Servlet {

		/**
		 * Path of the dispatcher servlet.
		 * 调度程序Servlet的路径。
		 */
		private String path = "/";

		/**
		 * Load on startup priority of the dispatcher servlet.
		 * 加载调度程序Servlet的启动优先级。
		 */
		private int loadOnStartup = -1;

		public String getPath() {
			return this.path;
		}

		public void setPath(String path) {
			Assert.notNull(path, "Path must not be null");
			Assert.isTrue(!path.contains("*"), "Path must not contain wildcards");
			this.path = path;
		}

		public int getLoadOnStartup() {
			return this.loadOnStartup;
		}

		public void setLoadOnStartup(int loadOnStartup) {
			this.loadOnStartup = loadOnStartup;
		}

		public String getServletMapping() {
			if (this.path.equals("") || this.path.equals("/")) {
				return "/";
			}
			if (this.path.endsWith("/")) {
				return this.path + "*";
			}
			return this.path + "/*";
		}

		public String getPath(String path) {
			String prefix = getServletPrefix();
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			return prefix + path;
		}

		public String getServletPrefix() {
			String result = this.path;
			int index = result.indexOf('*');
			if (index != -1) {
				result = result.substring(0, index);
			}
			if (result.endsWith("/")) {
				result = result.substring(0, result.length() - 1);
			}
			return result;
		}

	}

	public static class View {

		/**
		 * Spring MVC view prefix.
		 * Spring MVC视图前缀。
		 */
		private String prefix;

		/**
		 * Spring MVC view suffix.
         * Spring MVC视图后缀。
		 */
		private String suffix;

		public String getPrefix() {
			return this.prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getSuffix() {
			return this.suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

	}

	public static class Contentnegotiation {

		/**
		 * Whether the path extension in the URL path should be used to determine the
		 * requested media type. If enabled a request "/users.pdf" will be interpreted as
		 * a request for "application/pdf" regardless of the 'Accept' header.
		 * URL路径中的路径扩展是否应该用于确定请求的媒体类型。
		 * 如果启用，则无论"Accept"标头如何，请求"/users.pdf"都将被解释为对"application / pdf"的请求。
		 */
		private boolean favorPathExtension = false;

		/**
		 * Whether a request parameter ("format" by default) should be used to determine
		 * the requested media type.
		 *
		 * 是否应使用请求参数（默认为“格式”）来确定请求的媒体类型。
		 */
		private boolean favorParameter = false;

		/**
		 * Map file extensions to media types for content negotiation. For instance, yml
		 * to text/yaml.
		 * 将文件扩展名映射到媒体类型以进行内容协商。
		 * 例如，将yml转换为text/yaml。
		 */
		private Map<String, MediaType> mediaTypes = new LinkedHashMap<>();

		/**
		 * Query parameter name to use when "favor-parameter" is enabled.
		 * 启用"favor-parameter"时要使用的查询参数名称。
		 */
		private String parameterName;

		public boolean isFavorPathExtension() {
			return this.favorPathExtension;
		}

		public void setFavorPathExtension(boolean favorPathExtension) {
			this.favorPathExtension = favorPathExtension;
		}

		public boolean isFavorParameter() {
			return this.favorParameter;
		}

		public void setFavorParameter(boolean favorParameter) {
			this.favorParameter = favorParameter;
		}

		public Map<String, MediaType> getMediaTypes() {
			return this.mediaTypes;
		}

		public void setMediaTypes(Map<String, MediaType> mediaTypes) {
			this.mediaTypes = mediaTypes;
		}

		public String getParameterName() {
			return this.parameterName;
		}

		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}

	}

	public static class Pathmatch {

		/**
		 * Whether to use suffix pattern match (".*") when matching patterns to requests.
		 * If enabled a method mapped to "/users" also matches to "/users.*".
		 * 在将模式匹配到请求时是否使用后缀模式匹配（".*"）。
		 * 如果启用，则映射到"/users"的方法也将匹配"/users.*"。
		 */
		private boolean useSuffixPattern = false;

		/**
		 * Whether suffix pattern matching should work only against extensions registered
		 * with "spring.mvc.contentnegotiation.media-types.*". This is generally
		 * recommended to reduce ambiguity and to avoid issues such as when a "." appears
		 * in the path for other reasons.
		 *
		 * 后缀模式匹配是否仅适用于在"spring.mvc.contentnegotiation.media-types.*"中注册的扩展。
		 * 通常建议这样做以减少歧义并避免出现诸如“.”之类的问题。 由于其他原因出现在路径中。
		 */
		private boolean useRegisteredSuffixPattern = false;

		public boolean isUseSuffixPattern() {
			return this.useSuffixPattern;
		}

		public void setUseSuffixPattern(boolean useSuffixPattern) {
			this.useSuffixPattern = useSuffixPattern;
		}

		public boolean isUseRegisteredSuffixPattern() {
			return this.useRegisteredSuffixPattern;
		}

		public void setUseRegisteredSuffixPattern(boolean useRegisteredSuffixPattern) {
			this.useRegisteredSuffixPattern = useRegisteredSuffixPattern;
		}

	}

	public enum LocaleResolver {

		/**
		 * Always use the configured locale.
		 * 始终使用配置的语言环境。
		 */
		FIXED,

		/**
		 * Use the "Accept-Language" header or the configured locale if the header is not
		 * set.
		 * 如果未设置标头，请使用"Accept-Language"标头或配置的语言环境。
		 */
		ACCEPT_HEADER

	}

}
