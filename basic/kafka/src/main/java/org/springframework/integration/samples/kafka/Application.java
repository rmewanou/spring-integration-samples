/*
 * Copyright 2015-2018 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * @author Gary Russell
 * @since 4.2
 */
@SpringBootApplication
@EnableConfigurationProperties(KafkaAppProperties.class)
public class Application {

	@Autowired
	private KafkaAppProperties properties;

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context
				= new SpringApplicationBuilder(Application.class)
					.web(WebApplicationType.NONE)
					.run(args);

		context.getBean(Application.class).runDemo(context);
		context.close();
	}

	private void runDemo(ConfigurableApplicationContext context) {

        MessageChannel toKafka = context.getBean("toKafka", MessageChannel.class);
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, this.properties.getTopicOut());

        PollableChannel fromKafka = context.getBean("fromKafka", PollableChannel.class);

		Message<?> received = fromKafka.receive(2000);

		int count = 0;
		while (received != null) {
		    try {
                System.out.println("####  Sending messages no. " + ++count);
                headers.put(KafkaHeaders.MESSAGE_KEY, received.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY));
                // TODO - Ensure we scrambled the payload string before

                toKafka.send(new GenericMessage<>(received.getPayload(), headers));

                received = fromKafka.receive(2000);
                Thread.sleep(5);
            } catch (Exception e) {
		        System.out.println(e);
            }
		}
	}


	@Autowired
	private KafkaProperties kafkaProperties;

}
