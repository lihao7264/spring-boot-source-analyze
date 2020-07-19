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

package org.springframework.boot.autoconfigure.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.http.CacheControl;

/**
 * Properties used to configure resource handling.
 * 用于配置资源处理的属性。
 *
 * @author Phillip Webb
 * @author Brian Clozel
 * @author Dave Syer
 * @author Venil Noronha
 * @author Kristine Jetzke
 * @since 1.1.0
 */
@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
public class ResourceProperties {

	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	/**
	 * Locations of static resources. Defaults to classpath:[/META-INF/resources/,
	 * /resources/, /static/, /public/].
	 * 静态资源的位置。 默认为类路径：[/META-INF/resources /、/resources/、/static/、/public/]。
	 */
	private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;

	/**
	 * Whether to enable default resource handling.
	 * 是否启用默认资源处理。
	 */
	private boolean addMappings = true;

	private final Chain chain = new Chain();

	private final Cache cache = new Cache();

	public String[] getStaticLocations() {
		return this.staticLocations;
	}

	public void setStaticLocations(String[] staticLocations) {
		this.staticLocations = appendSlashIfNecessary(staticLocations);
	}

	private String[] appendSlashIfNecessary(String[] staticLocations) {
		String[] normalized = new String[staticLocations.length];
		for (int i = 0; i < staticLocations.length; i++) {
			String location = staticLocations[i];
			//若路径为/resources，则处理为/resources/
			normalized[i] = location.endsWith("/") ? location : location + "/";
		}
		return normalized;
	}

	public boolean isAddMappings() {
		return this.addMappings;
	}

	public void setAddMappings(boolean addMappings) {
		this.addMappings = addMappings;
	}

	public Chain getChain() {
		return this.chain;
	}

	public Cache getCache() {
		return this.cache;
	}

	/**
	 * Configuration for the Spring Resource Handling chain.
	 * Spring资源处理链的配置。
	 */
	public static class Chain {

		/**
		 * Whether to enable the Spring Resource Handling chain. By default, disabled
		 * unless at least one strategy has been enabled.
		 * 是否启用Spring Resource Handling链。
		 * 默认情况下，除非已启用至少一种策略，否则禁用。
		 */
		private Boolean enabled;

		/**
		 * Whether to enable caching in the Resource chain.
		 * 是否在资源链中启用缓存。(默认启用)
		 */
		private boolean cache = true;

		/**
		 * Whether to enable HTML5 application cache manifest rewriting.
		 * 是否启用HTML5应用程序缓存清单重写。
		 */
		private boolean htmlApplicationCache = false;

		/**
		 * Whether to enable resolution of already compressed resources (gzip, brotli).
		 * Checks for a resource name with the '.gz' or '.br' file extensions.
		 * 是否启用已压缩资源（gzip，brotli）的解析。
		 * 检查带有".gz"或".br"文件扩展名的资源名称。
		 */
		private boolean compressed = false;

		private final Strategy strategy = new Strategy();

		/**
		 * Return whether the resource chain is enabled. Return {@code null} if no
		 * specific settings are present.
		 *
		 * 返回是否启用资源链。 如果没有特定设置，则返回{@code null}。
		 *
		 * @return whether the resource chain is enabled or {@code null} if no specified
		 * settings are present.
		 * 是启用资源链，还是如果没有指定的设置，则返回{@code null}。
		 */
		public Boolean getEnabled() {
			return getEnabled(getStrategy().getFixed().isEnabled(), getStrategy().getContent().isEnabled(),
					this.enabled);
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isCache() {
			return this.cache;
		}

		public void setCache(boolean cache) {
			this.cache = cache;
		}

		public Strategy getStrategy() {
			return this.strategy;
		}

		public boolean isHtmlApplicationCache() {
			return this.htmlApplicationCache;
		}

		public void setHtmlApplicationCache(boolean htmlApplicationCache) {
			this.htmlApplicationCache = htmlApplicationCache;
		}

		public boolean isCompressed() {
			return this.compressed;
		}

		public void setCompressed(boolean compressed) {
			this.compressed = compressed;
		}

		static Boolean getEnabled(boolean fixedEnabled, boolean contentEnabled, Boolean chainEnabled) {
			return (fixedEnabled || contentEnabled) ? Boolean.TRUE : chainEnabled;
		}

	}

	/**
	 * Strategies for extracting and embedding a resource version in its URL path.
	 * 在资源版本的URL路径中提取和嵌入资源版本的策略。
	 */
	public static class Strategy {

		private final Fixed fixed = new Fixed();

		private final Content content = new Content();

		public Fixed getFixed() {
			return this.fixed;
		}

		public Content getContent() {
			return this.content;
		}

	}

	/**
	 * Version Strategy based on content hashing.
	 * 基于内容哈希的版本策略。
	 */
	public static class Content {

		/**
		 * Whether to enable the content Version Strategy.
		 * 是否启用内容版本策略。
		 */
		private boolean enabled;

		/**
		 * Comma-separated list of patterns to apply to the content Version Strategy.
		 * 以逗号分隔的模式列表，这些模式应用于内容版本策略。
		 */
		private String[] paths = new String[] { "/**" };

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String[] getPaths() {
			return this.paths;
		}

		public void setPaths(String[] paths) {
			this.paths = paths;
		}

	}

	/**
	 * Version Strategy based on a fixed version string.
	 * 基于固定版本字符串的版本策略。
	 */
	public static class Fixed {

		/**
		 * Whether to enable the fixed Version Strategy.
		 * 是否启用固定的版本策略。
		 */
		private boolean enabled;

		/**
		 * Comma-separated list of patterns to apply to the fixed Version Strategy.
		 * 逗号分隔的模式列表，适用于固定的版本策略。
		 */
		private String[] paths = new String[] { "/**" };

		/**
		 * Version string to use for the fixed Version Strategy.
		 * 用于固定版本策略的版本字符串。
		 */
		private String version;

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String[] getPaths() {
			return this.paths;
		}

		public void setPaths(String[] paths) {
			this.paths = paths;
		}

		public String getVersion() {
			return this.version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

	}

	/**
	 * Cache configuration.
	 * 缓存配置。
	 */
	public static class Cache {

		/**
		 * Cache period for the resources served by the resource handler. If a duration
		 * suffix is not specified, seconds will be used. Can be overridden by the
		 * 'spring.resources.cache.cachecontrol' properties.
		 * 资源处理程序服务的资源的缓存周期。
		 * 如果未指定持续时间后缀，则将使用秒。
		 * 可以被'spring.resources.cache.cachecontrol'属性覆盖。
		 */
		@DurationUnit(ChronoUnit.SECONDS)
		private Duration period;

		/**
		 * Cache control HTTP headers, only allows valid directive combinations. Overrides
		 * the 'spring.resources.cache.period' property.
		 * 缓存控制HTTP标头，仅允许有效的指令组合。 覆盖“spring.resources.cache.period”属性。
		 */
		private final Cachecontrol cachecontrol = new Cachecontrol();

		public Duration getPeriod() {
			return this.period;
		}

		public void setPeriod(Duration period) {
			this.period = period;
		}

		public Cachecontrol getCachecontrol() {
			return this.cachecontrol;
		}

		/**
		 * Cache Control HTTP header configuration.
		 * 缓存控制HTTP请求头配置。
		 */
		public static class Cachecontrol {

			/**
			 * Maximum time the response should be cached, in seconds if no duration
			 * suffix is not specified.
			 * 应该缓存响应的最长时间，如果未指定持续时间后缀，则以秒为单位。
			 */
			@DurationUnit(ChronoUnit.SECONDS)
			private Duration maxAge;

			/**
			 * Indicate that the cached response can be reused only if re-validated with
			 * the server.
			 * 表示仅在与服务器重新验证后才可以重用缓存的响应。
			 */
			private Boolean noCache;

			/**
			 * Indicate to not cache the response in any case.
			 * 表示在任何情况下都不缓存响应。
			 */
			private Boolean noStore;

			/**
			 * Indicate that once it has become stale, a cache must not use the response
			 * without re-validating it with the server.
			 * 表示一旦过时，缓存必须在未与服务器重新验证响应的情况下使用响应。
			 */
			private Boolean mustRevalidate;

			/**
			 * Indicate intermediaries (caches and others) that they should not transform
			 * the response content.
			 * 表示中介（缓存和其他中介）不应转换响应内容。
			 */
			private Boolean noTransform;

			/**
			 * Indicate that any cache may store the response.
			 * 表示任何缓存都可以存储响应。
			 */
			private Boolean cachePublic;

			/**
			 * Indicate that the response message is intended for a single user and must
			 * not be stored by a shared cache.
			 * 表示响应消息是针对单个用户的，并且不得由共享缓存存储。
			 */
			private Boolean cachePrivate;

			/**
			 * Same meaning as the "must-revalidate" directive, except that it does not
			 * apply to private caches.
			 *
			 * 与“必须重新验证”指令的含义相同，只不过它不适用于专用缓存。
			 */
			private Boolean proxyRevalidate;

			/**
			 * Maximum time the response can be served after it becomes stale, in seconds
			 * if no duration suffix is not specified.
			 */
			@DurationUnit(ChronoUnit.SECONDS)
			private Duration staleWhileRevalidate;

			/**
			 * Maximum time the response may be used when errors are encountered, in
			 * seconds if no duration suffix is not specified.
			 * 遇到错误时可以使用响应的最长时间，如果未指定持续时间后缀，则以秒为单位。
			 */
			@DurationUnit(ChronoUnit.SECONDS)
			private Duration staleIfError;

			/**
			 * Maximum time the response should be cached by shared caches, in seconds if
			 * no duration suffix is not specified.
			 * 如果未指定持续时间后缀，则响应应由共享缓存缓存的最长时间（以秒为单位）。
			 */
			@DurationUnit(ChronoUnit.SECONDS)
			private Duration sMaxAge;

			public Duration getMaxAge() {
				return this.maxAge;
			}

			public void setMaxAge(Duration maxAge) {
				this.maxAge = maxAge;
			}

			public Boolean getNoCache() {
				return this.noCache;
			}

			public void setNoCache(Boolean noCache) {
				this.noCache = noCache;
			}

			public Boolean getNoStore() {
				return this.noStore;
			}

			public void setNoStore(Boolean noStore) {
				this.noStore = noStore;
			}

			public Boolean getMustRevalidate() {
				return this.mustRevalidate;
			}

			public void setMustRevalidate(Boolean mustRevalidate) {
				this.mustRevalidate = mustRevalidate;
			}

			public Boolean getNoTransform() {
				return this.noTransform;
			}

			public void setNoTransform(Boolean noTransform) {
				this.noTransform = noTransform;
			}

			public Boolean getCachePublic() {
				return this.cachePublic;
			}

			public void setCachePublic(Boolean cachePublic) {
				this.cachePublic = cachePublic;
			}

			public Boolean getCachePrivate() {
				return this.cachePrivate;
			}

			public void setCachePrivate(Boolean cachePrivate) {
				this.cachePrivate = cachePrivate;
			}

			public Boolean getProxyRevalidate() {
				return this.proxyRevalidate;
			}

			public void setProxyRevalidate(Boolean proxyRevalidate) {
				this.proxyRevalidate = proxyRevalidate;
			}

			public Duration getStaleWhileRevalidate() {
				return this.staleWhileRevalidate;
			}

			public void setStaleWhileRevalidate(Duration staleWhileRevalidate) {
				this.staleWhileRevalidate = staleWhileRevalidate;
			}

			public Duration getStaleIfError() {
				return this.staleIfError;
			}

			public void setStaleIfError(Duration staleIfError) {
				this.staleIfError = staleIfError;
			}

			public Duration getSMaxAge() {
				return this.sMaxAge;
			}

			public void setSMaxAge(Duration sMaxAge) {
				this.sMaxAge = sMaxAge;
			}

			public CacheControl toHttpCacheControl() {
				PropertyMapper map = PropertyMapper.get();
				CacheControl control = createCacheControl();
				map.from(this::getMustRevalidate).whenTrue().toCall(control::mustRevalidate);
				map.from(this::getNoTransform).whenTrue().toCall(control::noTransform);
				map.from(this::getCachePublic).whenTrue().toCall(control::cachePublic);
				map.from(this::getCachePrivate).whenTrue().toCall(control::cachePrivate);
				map.from(this::getProxyRevalidate).whenTrue().toCall(control::proxyRevalidate);
				map.from(this::getStaleWhileRevalidate).whenNonNull()
						.to((duration) -> control.staleWhileRevalidate(duration.getSeconds(), TimeUnit.SECONDS));
				map.from(this::getStaleIfError).whenNonNull()
						.to((duration) -> control.staleIfError(duration.getSeconds(), TimeUnit.SECONDS));
				map.from(this::getSMaxAge).whenNonNull()
						.to((duration) -> control.sMaxAge(duration.getSeconds(), TimeUnit.SECONDS));
				// check if cacheControl remained untouched
				if (control.getHeaderValue() == null) {
					return null;
				}
				return control;
			}

			private CacheControl createCacheControl() {
				if (Boolean.TRUE.equals(this.noStore)) {
					return CacheControl.noStore();
				}
				if (Boolean.TRUE.equals(this.noCache)) {
					return CacheControl.noCache();
				}
				if (this.maxAge != null) {
					return CacheControl.maxAge(this.maxAge.getSeconds(), TimeUnit.SECONDS);
				}
				return CacheControl.empty();
			}

		}

	}

}
