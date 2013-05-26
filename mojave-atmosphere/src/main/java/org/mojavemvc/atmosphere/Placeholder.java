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

public class Placeholder {

    /**
     * NOTES - based on org.atmosphere.jersey.AtmosphereFilter
     * TODO:
     * 
     * - should provide an org.mojavemvc.atmosphere.SuspendResponse class
     * -- can be used to suspend response programmatically, similar to @Suspend
     * -- see org.atmosphere.jersey.SuspendResponse
     * 
     * - if SuspendResponse is returned, suspend response and ignore any annotations
     * - else, process annotations (if present) in following order:
     * -- @Broadcast
     * --- check for @Cluster
     * -- @Asynchronous
     * -- @Suspend
     * -- @Susbscribe
     * -- @Publish
     * -- @Resume
     * -- @Schedule
     * 
     * ---------------------------------------------------------
     * 
     * - for all Atmosphere processing, do the following:
     * 
     * -- check for presence of org.atmosphere.cpr.AtmosphereConfig: if not present, 
     *    report an error--do not continue
     * --- in Jersey, the AtmosphereConfig is an attribute in the HttpServletRequest
     * 
     * -- get an init param from the org.atmosphere.cpr.AtmosphereConfig that specifies the Jersey 
     *    ContainerResponseWriter, and instantiate the ContainerReponseWriter
     * --- an instance of the writer may have been placed in the HttpServletRequest--if so, use that one
     * --- if the writer is not null, use it, otherwise continue
     * 
     * -- get the org.atmosphere.cpr.AtmosphereResource from the HttpServletRequest as an attribute
     * 
     * -- check in org.atmosphere.cpr.AtmosphereConfig for 
     *    org.atmosphere.cpr.ApplicationConfig.SUPPORT_LOCATION_HEADER init param
     * --- if true, sets "useResumeAnnotation" to true
     * 
     * -- if the response status is 204, set it to 200
     * 
     * -----------------------
     * 
     * - handle the following actions, which are specified by the annotations themselves, or by a 
     *   combination of the annotations with various annotation parameters:
     *   
     * -- ASYNCHRONOUS (@Asynchronous present)
     * 
     * -- SUSPEND_RESPONSE (return type of action is SuspendResponse)
     * 
     * -- SUBSCRIBE (@Subscribe present), 
     *    SUSPEND (@Suspend present and @Suspend.resumeOnBroadcast is false), 
     *    or SUSPEND_RESUME (@Suspend present and @Suspend.resumeOnBroadcast is true)
     *    
     * -- RESUME (@Resume present)
     * 
     * -- BROADCAST (@Broadcast present and @Broadcast.resumeOnBroadcast is false),
     *    PUBLISH (@Publish is present),
     *    or RESUME_ON_BROADCAST (@Broadcast present and @Broadcast.resumeOnBroadcast is true)
     *    
     * -- SCHEDULE (@Schedule present and @Schedule.resumeOnBroadcast is false),
     *    or SCHEDULE_RESUME (@Schedule present and @Schedule.resumeOnBroadcast is true)
     *    
     * ------------------------
     * 
     * - SUSPEND_RESPONSE:
     * -- determine if we will resume on broadcast:
     * --- check for org.atmosphere.cpr.HeaderConfig.X_ATMOSPHERE_TRANSPORT header in HttpServletRequest:
     * ---- if it exists and it equals either org.atmosphere.cpr.ApplicationConfig.SUPPORT_LOCATION_HEADER or 
     *      org.atmosphere.cpr.HeaderConfig.LONG_POLLING_TRANSPORT, return true, otherwise use the value of 
     *      org.mojavemvc.atmosphere.SuspendResponse.resumeOnBroadcast()
     * -- add all AtmosphereResourceEventListener in org.mojavemvc.atmosphere.SuspendResponse to 
     *    org.atmosphere.cpr.AtmosphereResource
     * -- get the org.atmosphere.cpr.Broadcaster from org.mojavemvc.atmosphere.SuspendResponse.broadcaster()
     * --- if it does not exist and org.mojavemvc.atmosphere.SuspendResponse.scope() != Suspend.SCOPE.REQUEST
     *     get the INJECTED_BROADCASTER attribute from the HttpServletRequest
     * -- do suspend
     * 
     * ---------------------------------------------------------
     * 
     * - Perhaps create the instance of the AtmosphereFramework in an initializer, and process the 
     *   annotations in a global class-level interceptor? The interceptor can invoke
     *   framework.doCometSupport(AtmosphereRequest.wrap(req), AtmosphereResponse.wrap(res)).
     */
}
