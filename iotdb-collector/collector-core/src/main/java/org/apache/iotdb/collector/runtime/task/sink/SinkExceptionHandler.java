/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.collector.runtime.task.sink;

import org.apache.iotdb.collector.runtime.task.event.EventContainer;

import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SinkExceptionHandler implements ExceptionHandler<EventContainer> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SinkExceptionHandler.class);

  @Override
  public void handleEventException(Throwable ex, long sequence, EventContainer event) {
    // TODO: heartbeat
    // TODO: retry strategy
    LOGGER.warn("Failed to handle event", ex);
  }

  @Override
  public void handleOnStartException(Throwable ex) {
    LOGGER.warn("Failed to start sink disruptor", ex);
  }

  @Override
  public void handleOnShutdownException(Throwable ex) {
    LOGGER.warn("Failed to shutdown sink disruptor", ex);
  }
}
