/*
 * Copyright 2017 the original author or authors.
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

package org.springframework.integration.samples.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for the kafka sample app.
 *
 * @author Gary Russell
 * @since 5.0
 *
 */
@ConfigurationProperties("kafka")
public class KafkaAppProperties {

	private String topicIn;

	private String topicOut;

	private String messageKey;

	public String getTopicIn() {
		return this.topicIn;
	}

	public void setTopicIn(String topicIn) {
		this.topicIn = topicIn;
	}

	public String getTopicOut() {
		return this.topicOut;
	}

	public void setTopicOut(String topicOut) {
		this.topicOut = topicOut;
	}

	public String getMessageKey() {
		return this.messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

}
