/*
 * Copyright (C) 2011-2013 Mojavemvc.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mojavemvc.atmosphere;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.atmosphere.annotation.Suspend;
import org.atmosphere.annotation.Suspend.SCOPE;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.Broadcaster;
import org.mojavemvc.marshalling.Marshallable;

/**
 * This class is based on org.atmosphere.jersey.SuspendResponse.
 * 
 * @author Luis Antunes
 */
public class SuspendResponse<E> implements Marshallable<E> {

    private final E entity;
    private final Suspend.SCOPE scope;
    private final Broadcaster broadcaster;
    private final TimeSpan suspendTimeout;
    private final boolean writeEntity;
    private final boolean resumeOnBroadcast;
    private final Collection<AtmosphereResourceEventListener> listeners;
    
    public SuspendResponse(E entity, SCOPE scope, Broadcaster broadcaster, 
            TimeSpan suspendTimeout, boolean writeEntity, boolean resumeOnBroadcast,
            Collection<AtmosphereResourceEventListener> listeners) {
        
        this.entity = entity;
        this.scope = scope;
        this.broadcaster = broadcaster;
        this.suspendTimeout = suspendTimeout;
        this.writeEntity = writeEntity;
        this.resumeOnBroadcast = resumeOnBroadcast;
        this.listeners = listeners;
    }

    @Override
    public E getEntity() {
        return entity;
    }

    public Suspend.SCOPE scope() {
        return scope;
    }

    public Broadcaster broadcaster() {
        return broadcaster;
    }

    public TimeSpan period() {
        return suspendTimeout;
    }

    public boolean writeEntity() {
        return writeEntity;
    }

    public boolean resumeOnBroadcast() {
        return resumeOnBroadcast;
    }
    
    public Collection<AtmosphereResourceEventListener> listeners() {
        return listeners;
    }

    public static class TimeSpan {

        private final TimeUnit timeUnit;
        private final int period;

        public TimeSpan(int period, TimeUnit timeUnit) {
            this.period = period;
            this.timeUnit = timeUnit;
        }

        public int value() {
            return period;
        }

        public TimeUnit timeUnit() {
            return timeUnit;
        }
    }
}
