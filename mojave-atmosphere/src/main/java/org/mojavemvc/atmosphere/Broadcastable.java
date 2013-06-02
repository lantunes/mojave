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

import org.atmosphere.cpr.Broadcaster;
import org.mojavemvc.annotations.Marshall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is based on org.atmosphere.jersey.Broadcastable.
 * 
 * @author Luis Antunes
 */
/*
 * TODO rename to Broadcast
 */
public class Broadcastable {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.atmosphere");
    
    private final Broadcaster broadcaster;
    private final Object message;
    private final Object callerMessage;

    public Broadcastable(Broadcaster b) {
        
        this.broadcaster = b;
        this.message = "";
        this.callerMessage = "";
    }

    /**
     * Broadcast the <b>message</b> to the set of suspended AtmosphereResource, and write back
     * the <b>message</b> to the request which invoked the current resource method.
     *
     * @param message the message which will be broadcasted
     * @param broadcaster the Broadcaster
     */
    public Broadcastable(Object message, Broadcaster broadcaster) {
        
        this.broadcaster = broadcaster;
        this.message = message;
        this.callerMessage = message;
    }

    /**
     * Broadcast the <b>message</b> to the set of suspended AtmosphereResource, and write back
     * the <b>callerMessage</b> to the request which invoked the current resource method.
     *
     * @param message the message which will be broadcasted
     * @param callerMessage the message which will be sent back to the request.
     * @param broadcaster the Broadcaster
     */
    public Broadcastable(Object message, Object callerMessage, Broadcaster broadcaster) {
        
        this.broadcaster = broadcaster;
        this.message = message;
        this.callerMessage = callerMessage;
    }
    
    /**
     * Broadcast the message.
     *
     * @return the transformed message (BroadcastFilter)
     */
    public Object broadcast() {
        try {
            return broadcaster.broadcast(message).get();
        } catch (Exception ex) {
            logger.error("failed to broadcast message: " + message, ex);
        }
        return null;
    }

    public Broadcaster getBroadcaster() {
        return broadcaster;
    }
    
    public Object getMessage() {
        return message;
    }

    @Marshall
    public Object getResponseMessage() {
        return callerMessage;
    }
}
