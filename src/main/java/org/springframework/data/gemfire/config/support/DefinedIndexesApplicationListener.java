/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gemfire.config.support;

import com.gemstone.gemfire.cache.query.MultiIndexCreationException;
import com.gemstone.gemfire.cache.query.QueryService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.gemfire.config.xml.GemfireConstants;

/**
 * {@link DefinedIndexesApplicationListener} is a Spring {@link ApplicationListener} used to create all
 * "defined" GemFire {@link com.gemstone.gemfire.cache.query.Index Indexes} by using the {@link QueryService},
 * {@literal defineXxxIndex(..)} methods.
 *
 * @author John Blum
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.event.ContextRefreshedEvent
 * @see com.gemstone.gemfire.cache.query.QueryService
 * @since 1.7.0
 */
public class DefinedIndexesApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

	protected final Log logger = initLogger();

	/**
	 * Attempts to create all defined {@link com.gemstone.gemfire.cache.query.Index Indexes} using
	 * the {@link QueryService}, {@literal defineXxxIndex(..)} API once the Spring {@link ApplicationContext}
	 * has been refreshed.
	 *
	 * @param event {@link ContextRefreshedEvent} fired when the Spring {@link ApplicationContext} gets refreshed.
	 * @see org.springframework.context.event.ContextRefreshedEvent
	 * @see com.gemstone.gemfire.cache.query.QueryService#createDefinedIndexes()
	 * @see #getQueryService(ContextRefreshedEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			QueryService queryService = getQueryService(event);

			if (queryService != null) {
				queryService.createDefinedIndexes();
			}
		}
		catch (MultiIndexCreationException ignore) {
			logger.warn(String.format("Failed to create pre-defined Indexes: %s", ignore.getMessage()), ignore);
		}
	}

	/* (non-Javadoc) */
	Log initLogger() {
		return LogFactory.getLog(getClass());
	}

	/* (non-Javadoc) */
	private QueryService getQueryService(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		String queryServiceBeanName = getQueryServiceBeanName();

		return (applicationContext.containsBean(queryServiceBeanName) ?
			applicationContext.getBean(queryServiceBeanName, QueryService.class) : null);
	}

	/* (non-Javadoc) */
	private String getQueryServiceBeanName() {
		return GemfireConstants.DEFAULT_GEMFIRE_INDEX_DEFINITION_QUERY_SERVICE;
	}
}
