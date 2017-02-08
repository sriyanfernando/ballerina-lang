/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.siddhi.core.event.stream.converter;

import org.wso2.siddhi.core.event.ComplexEvent;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.event.stream.StreamEvent;

/**
 * The converter that does no conversion but only copy OutputData of the events into StreamEvents
 */
public class ZeroStreamEventConverter implements StreamEventConverter {

    public void convertData(long timestamp, Object[] data, StreamEvent.Type type, StreamEvent borrowedEvent) {
        System.arraycopy(data, 0, borrowedEvent.getOutputData(), 0, data.length);
        borrowedEvent.setType(type);
        borrowedEvent.setTimestamp(timestamp);
    }

    public void convertEvent(Event event, StreamEvent borrowedEvent) {
        convertData(event.getTimestamp(), event.getData(), event.isExpired() ? StreamEvent.Type.EXPIRED : StreamEvent.Type.CURRENT,
                borrowedEvent);
    }

    public void convertComplexEvent(ComplexEvent complexEvent, StreamEvent borrowedEvent) {
        convertData(complexEvent.getTimestamp(), complexEvent.getOutputData(), complexEvent.getType(),
                borrowedEvent);
    }

    @Override
    public void convertData(long timeStamp, Object[] data, StreamEvent borrowedEvent) {
        convertData(timeStamp, data, StreamEvent.Type.CURRENT, borrowedEvent);
    }

}
