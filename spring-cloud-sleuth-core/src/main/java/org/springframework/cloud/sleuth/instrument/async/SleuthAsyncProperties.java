/*
 * Copyright 2013-2019 the original author or authors.
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

package org.springframework.cloud.sleuth.instrument.async;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Settings for disable instrumentation of ThreadPoolTaskExecutors.
 *
 * @author Jesus Alonso
 * @since 2.1.0
 */

@ConfigurationProperties(prefix = "spring.sleuth.async")
public class SleuthAsyncProperties {

	/**
	 * List of {@link java.util.concurrent.Executor} bean names that should be ignored and
	 * not wrapped in a trace representation.
	 */
	private List<String> ignoredBeans = Collections.emptyList();

	public List<String> getIgnoredBeans() {
		return this.ignoredBeans;
	}

	public void setIgnoredBeans(List<String> ignoredBeans) {
		this.ignoredBeans = ignoredBeans;
	}

}
