/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.integration.redis.config;

import static junit.framework.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.redis.inbound.RedisInboundChannelAdapter;
import org.springframework.integration.support.converter.SimpleMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Oleg Zhurakousky
 * @author Mark Fisher
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisInboundChannelAdapterParserTests {

	@Autowired
	private ApplicationContext context;

	@Test @Ignore
	public void validateConfiguration() {
		RedisInboundChannelAdapter adapter = context.getBean("adapter", RedisInboundChannelAdapter.class);
		assertEquals("adapter", adapter.getComponentName());
		assertEquals("redis:inbound-channel-adapter", adapter.getComponentType());
		DirectFieldAccessor accessor = new DirectFieldAccessor(adapter);
		Object errorChannelBean = context.getBean("testErrorChannel");
		assertEquals(errorChannelBean, accessor.getPropertyValue("errorChannel"));
		Object converterBean = context.getBean("testConverter");
		assertEquals(converterBean, accessor.getPropertyValue("messageConverter"));
	}

	@Test @Ignore
	public void testInboundChannelAdapterMessaging() {
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
		connectionFactory.setPort(7379);
		connectionFactory.afterPropertiesSet();
		connectionFactory.getConnection().publish("foo".getBytes(), "Hello Redis from foo".getBytes());
		QueueChannel receiveChannel = context.getBean("receiveChannel", QueueChannel.class);
		assertEquals("Hello Redis from foo", receiveChannel.receive(1000).getPayload());
		connectionFactory.getConnection().publish("bar".getBytes(), "Hello Redis from bar".getBytes());
		assertEquals("Hello Redis from bar", receiveChannel.receive(1000).getPayload());
	}


	@SuppressWarnings("unused")
	private static class TestMessageConverter extends SimpleMessageConverter {
	}

}