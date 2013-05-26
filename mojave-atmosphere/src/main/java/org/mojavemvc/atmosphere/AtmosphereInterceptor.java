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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.FrameworkConfig;
import org.mojavemvc.annotations.AfterAction;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.aop.RequestContext;
import org.mojavemvc.initialization.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Luis Antunes
 */
public class AtmosphereInterceptor {
    
    private static final Logger logger = 
            LoggerFactory.getLogger("org.mojavemvc.atmosphere");

    public static final String ATMOSPHERE_FRAMEWORK = 
            "mojavemvc-internal-atmosphere-framework";
    
    private static final String INSTALLATION_ERROR = 
            "The Atmosphere Framework is not installed properly " +
            "and unexpected results may occur.";
    
    @Inject
    AppProperties appProperties;
    
    @BeforeAction
    public void beforeAction(RequestContext ctx) {
        
        AtmosphereFramework framework = (AtmosphereFramework) appProperties
                .getProperty(ATMOSPHERE_FRAMEWORK);
        HttpServletRequest req = ctx.getRequest();
        HttpServletResponse res = ctx.getResponse();
        
        try {
            
            framework.doCometSupport(
                    AtmosphereRequest.wrap(req), AtmosphereResponse.wrap(res));
            
        } catch (Exception e) {
            logger.error("error invoking Atmosphere framework", e);
        }
    }
    
    @AfterAction
    public void afterAction(RequestContext ctx) {
        
        HttpServletRequest servletReq = ctx.getRequest();
        
        AtmosphereConfig config = (AtmosphereConfig) servletReq
                .getAttribute(FrameworkConfig.ATMOSPHERE_CONFIG);
        if (config == null) {
            logger.error(INSTALLATION_ERROR);
            throw new IllegalStateException(INSTALLATION_ERROR);
        }
        
        AtmosphereResource resource = (AtmosphereResource) servletReq
                .getAttribute(FrameworkConfig.ATMOSPHERE_RESOURCE);
        //TODO debugging
        logger.info("resource: " + resource);
        
        //TODO get current status
        //HttpServletResponse response = ctx.getResponse();
        //if (response.getStatus() == 204) {
        //    response.setStatus(200);
        //}
        
        //TODO handle Atmosphere annotations here
        /*
         * This is modeled after org.atmosphere.jersey.AtmosphereFilter
         * which does its work through a com.sun.jersey.spi.container.ContainerResponseFilter
         * which means that Atmosphere annotation processing is done after
         * the resource method is processed--in fact, it must be, as 
         * org.mojavemvc.atmosphere.SuspendResponse is a possible return value 
         */
        
        Suspend suspendAnnotation = ctx.getActionAnnotation(Suspend.class);
        //TODO debugging
        logger.info("suspend annotation found: " + (suspendAnnotation != null));
        
        Object entity = ctx.getActionReturnValue();
        if (entity != null && entity instanceof SuspendResponse) {
            //handle SUSPEND_RESPONSE action
        }
    }
}
