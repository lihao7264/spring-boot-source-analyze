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

/**
 * Some named search strategies for beans in the bean factory hierarchy.
 *
 * 在bean工厂层次结构中一些针对bean的命名搜索策略。
 *
 * @author Dave Syer
 * @since 1.0.0
 */
public enum SearchStrategy {

	/**
	 * Search only the current context.
	 * 仅搜索当前上下文。
	 */
	CURRENT,

	/**
	 * Search all ancestors, but not the current context.
	 * 搜索所有祖先，但不搜索当前上下文。
	 */
	ANCESTORS,

	/**
	 * Search the entire hierarchy.
	 * 搜索整个层次结构。
	 */
	ALL

}
