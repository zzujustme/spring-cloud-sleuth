/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package integration;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Collection;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.JdkIdGenerator;
import org.springframework.util.StringUtils;

import com.github.kristofa.brave.SpanCollector;
import com.twitter.zipkin.gen.BinaryAnnotation;
import com.twitter.zipkin.gen.Span;

import integration.MessagingApplicationTests.IntegrationSpanCollectorConfig;
import sample.SampleMessagingApplication;
import tools.AbstractIntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { IntegrationSpanCollectorConfig.class, SampleMessagingApplication.class })
@WebIntegrationTest
@TestPropertySource(properties="sample.zipkin.enabled=true")
public class MessagingApplicationTests extends AbstractIntegrationTest {

	private static int port = 3381;
	private static String sampleAppUrl = "http://localhost:" + port;
	@Autowired IntegrationTestSpanCollector integrationTestSpanCollector;

	@After
	public void cleanup() {
		this.integrationTestSpanCollector.hashedSpans.clear();
	}

	@Test
	public void should_propagate_spans_for_messaging() {
		String traceId = new JdkIdGenerator().generateId().toString();

		await().until(httpMessageWithTraceIdInHeadersIsSuccessfullySent(sampleAppUrl + "/", traceId));

		await().until(() -> {
			thenAllSpansHaveTraceIdEqualTo(traceId);
		});
	}

	@Test
	public void should_propagate_spans_for_messaging_with_async() {
		String traceId = new JdkIdGenerator().generateId().toString();

		await().until(httpMessageWithTraceIdInHeadersIsSuccessfullySent(sampleAppUrl + "/xform", traceId));

		await().until(() -> {
			thenAllSpansHaveTraceIdEqualTo(traceId);
			thenThereIsAtLeastOneBinaryAnnotationWithKey("background-sleep-millis");
		});
	}

	private void thenThereIsAtLeastOneBinaryAnnotationWithKey(String binaryAnnotationKey) {
		then(reporter.hashedSpans.stream()
				.map(s -> s.binaryAnnotations)
				.flatMap(Collection::stream)
				.anyMatch(b -> b.key.equals(binaryAnnotationKey))).isTrue();
	}

	private void thenAllSpansHaveTraceIdEqualTo(String traceId) {
		then(this.integrationTestSpanCollector.hashedSpans.stream().allMatch(span -> span.getTrace_id() == zipkinHashedTraceId(traceId))).isTrue();
	}

	@Configuration
	public static class IntegrationSpanCollectorConfig {
		@Bean
		SpanCollector integrationTestSpanCollector() {
			return new IntegrationTestSpanCollector();
		}
	}

}
