/*
 * Copyright 2013-2021 the original author or authors.
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

package org.springframework.cloud.sleuth.instrument.jdbc;

import java.util.concurrent.ConcurrentHashMap;

import javax.sql.CommonDataSource;

/**
 * {@link CommonDataSource} name resolver based on bean name.
 *
 * @author Arthur Gavlyukovskiy
 * @since 3.1.0
 */
public class DataSourceNameResolver {

	private final ConcurrentHashMap<CommonDataSource, String> cachedNames = new ConcurrentHashMap<>();

	public void addDataSource(String name, CommonDataSource dataSource) {
		this.cachedNames.put(dataSource, name);
	}

	public String resolveDataSourceName(CommonDataSource dataSource) {
		return this.cachedNames.getOrDefault(dataSource, "dataSource");
	}

}
