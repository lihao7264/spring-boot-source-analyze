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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionMessage.Style;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * {@link Condition} that checks if properties are defined in environment.
 * {@link Condition}，用于检查属性是否在环境中定义。
 *
 * @author Maciej Walkowiak
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @see ConditionalOnProperty
 */
// 优先级
@Order(Ordered.HIGHEST_PRECEDENCE + 40)
class OnPropertyCondition extends SpringBootCondition {

	/**
	 * 获取比对结果
	 * @param context the condition context
	 * @param metadata the annotation metadata
	 * @return  结果
	 */
	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		// 获取@ConditionalOnProperty注解的所有属性值，属性值是MultiValueMap类型
		List<AnnotationAttributes> allAnnotationAttributes = annotationAttributesFromMultiValueMap(
				metadata.getAllAnnotationAttributes(ConditionalOnProperty.class.getName()));
		// 初始化【比对不成功】列表
		List<ConditionMessage> noMatch = new ArrayList<>();
		// 初始化【比对成功】列表
		List<ConditionMessage> match = new ArrayList<>();
		// 循环计算每一个属性的比对结果
		for (AnnotationAttributes annotationAttributes : allAnnotationAttributes) {
			// 计算比对结果
			ConditionOutcome outcome = determineOutcome(annotationAttributes, context.getEnvironment());

			// 判断匹配结果是否成功，如果成功将结果的ConditionMessage加入【比对成功】列表，否则加入【比对不成功】列表
			(outcome.isMatch() ? match : noMatch).add(outcome.getConditionMessage());
		}
		// 如果【比对不成功】列表不为空，返回“匹配失败”的ConditionOutcome，条件不成立
		if (!noMatch.isEmpty()) {
			return ConditionOutcome.noMatch(ConditionMessage.of(noMatch));
		}
		// 返回“匹配成功”的ConditionOutcome，条件成立
		return ConditionOutcome.match(ConditionMessage.of(match));
	}

	/**
	 * 将MultiValueMap中的属性数据转成AnnotationAttributes类型。
	 * @param multiValueMap
	 * @return
	 */
	private List<AnnotationAttributes> annotationAttributesFromMultiValueMap(
			MultiValueMap<String, Object> multiValueMap) {
		List<Map<String, Object>> maps = new ArrayList<>();
		multiValueMap.forEach((key, value) -> {
			for (int i = 0; i < value.size(); i++) {
				Map<String, Object> map;
				if (i < maps.size()) {
					map = maps.get(i);
				}
				else {
					map = new HashMap<>();
					maps.add(map);
				}
				map.put(key, value.get(i));
			}
		});
		List<AnnotationAttributes> annotationAttributes = new ArrayList<>(maps.size());
		for (Map<String, Object> map : maps) {
			annotationAttributes.add(AnnotationAttributes.fromMap(map));
		}
		return annotationAttributes;
	}

	/**
	 * 计算比对结果。
	 * @param annotationAttributes
	 * @param resolver
	 * @return
	 */
	private ConditionOutcome determineOutcome(AnnotationAttributes annotationAttributes, PropertyResolver resolver) {
		// 用属性值实例化一个内部类Spec
		Spec spec = new Spec(annotationAttributes);
		// 初始化【未配置属性】列表
		List<String> missingProperties = new ArrayList<>();
		// 初始化【匹配失败属性】列表
		List<String> nonMatchingProperties = new ArrayList<>();
		// 匹配属性(传入missingProperties和nonMatchingProperties)
		spec.collectProperties(resolver, missingProperties, nonMatchingProperties);
		// 如果【未配置属性】列表不为空，返回实例化匹配失败的ConditionOutcome
		if (!missingProperties.isEmpty()) {
			return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
					.didNotFind("property", "properties").items(Style.QUOTE, missingProperties));
		}
		// 如果【匹配失败属性】列表不为空，返回实例化匹配失败的ConditionOutcome
		if (!nonMatchingProperties.isEmpty()) {
			return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnProperty.class, spec)
					.found("different value in property", "different value in properties")
					.items(Style.QUOTE, nonMatchingProperties));
		}
		// 返回实例化匹配成功的ConditionOutcome
		return ConditionOutcome
				.match(ConditionMessage.forCondition(ConditionalOnProperty.class, spec).because("matched"));
	}

	/**
	 * 私有静态内部类。
	 */
	private static class Spec {

		// 每个要匹配的属性前缀。如果没有指定前缀，前缀将自动以"."结尾。
		private final String prefix;

		// 属性的预期值。如果没有指定预期值，属性不能等于false字符串。
		private final String havingValue;

		//  要匹配的属性的名称。如果定义了前缀prefix，那么将使用prefix+name的形式进行匹配。
		private final String[] names;

		//  如果没有设置要匹配的属性，那么指定条件是否应该匹配成功，默认为false。
		private final boolean matchIfMissing;

		Spec(AnnotationAttributes annotationAttributes) {
			// 前缀作trim处理。
			String prefix = annotationAttributes.getString("prefix").trim();
			// 如果前缀有值，且不是以"."结尾，在结尾加"."
			// 比如: "spring.mvc"会变为"spring.mvc."
			if (StringUtils.hasText(prefix) && !prefix.endsWith(".")) {
				prefix = prefix + ".";
			}
			// 初始化前缀
			this.prefix = prefix;
			// 初始化预期值
			this.havingValue = annotationAttributes.getString("havingValue");
			// 初始化属性名数组
			this.names = getNames(annotationAttributes);
			// 初始化如果没有配置，条件是否成功
			this.matchIfMissing = annotationAttributes.getBoolean("matchIfMissing");
		}

		/**
		 * 初始化属性名数组。
		 * @param annotationAttributes
		 * @return
		 */
		private String[] getNames(Map<String, Object> annotationAttributes) {
			// 获取value数组(name数组的别名)
			String[] value = (String[]) annotationAttributes.get("value");
			// 获取name数组
			String[] name = (String[]) annotationAttributes.get("name");
			// name和value必须指定，不能同时为空。
			Assert.state(value.length > 0 || name.length > 0,
					"The name or value attribute of @ConditionalOnProperty must be specified");
			// name和value是互斥的，不能同时有值。
			Assert.state(value.length == 0 || name.length == 0,
					"The name and value attributes of @ConditionalOnProperty are exclusive");
			// value和name，哪个有值就返回哪个
			return (value.length > 0) ? value : name;
		}

		/**
		 * 判断属性是否匹配
		 * @param resolver
		 * @param missing      【未配置属性】列表
		 * @param nonMatching  【匹配失败属性】列表
		 */
		private void collectProperties(PropertyResolver resolver, List<String> missing, List<String> nonMatching) {
			// 循环配置属性是否匹配
			for (String name : this.names) {
				// 完整的key
				String key = this.prefix + name;
				// 如果有这个属性的话
				if (resolver.containsProperty(key)) {
					// 是否匹配(先获取属性)
					// 如果处理器(理解为配置文件application.yml)中包含这个属性名，判断属性名对应的值是否和预期值匹配
					if (!isMatch(resolver.getProperty(key), this.havingValue)) {
						nonMatching.add(name);
					}
				}
				else {
					// 如果处理器中(理解为配置文件application.yml)中未包含这个属性，且matchIfMissing为false，将属性名加入【未配置属性】列表
					if (!this.matchIfMissing) {
						missing.add(name);
					}
				}
			}
		}

		/**
		 * 判断属性对应的值是否和预期值匹配。
		 * @param value      属性值
		 * @param requiredValue   预期值
		 * @return   是否匹配成功 true-成功 false-失败
		 */
		private boolean isMatch(String value, String requiredValue) {
			// 如果预期值不为空，忽略大小写判断两者是否相等
			if (StringUtils.hasLength(requiredValue)) {
				return requiredValue.equalsIgnoreCase(value);
			}
			// 预期值为空，忽略大小写判断属性值是否等于false
			return !"false".equalsIgnoreCase(value);
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("(");
			result.append(this.prefix);
			if (this.names.length == 1) {
				result.append(this.names[0]);
			}
			else {
				result.append("[");
				result.append(StringUtils.arrayToCommaDelimitedString(this.names));
				result.append("]");
			}
			if (StringUtils.hasLength(this.havingValue)) {
				result.append("=").append(this.havingValue);
			}
			result.append(")");
			return result.toString();
		}

	}

}
