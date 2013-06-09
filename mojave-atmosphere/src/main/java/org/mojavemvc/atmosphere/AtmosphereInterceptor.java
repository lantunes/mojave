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

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.annotation.Asynchronous;
import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Cluster;
import org.atmosphere.annotation.Publish;
import org.atmosphere.annotation.Resume;
import org.atmosphere.annotation.Schedule;
import org.atmosphere.annotation.Subscribe;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterConfig;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.ClusterBroadcastFilter;
import org.atmosphere.cpr.FrameworkConfig;
import org.atmosphere.cpr.HeaderConfig;
import org.atmosphere.di.InjectorProvider;
import org.mojavemvc.annotations.AfterAction;
import org.mojavemvc.annotations.BeforeAction;
import org.mojavemvc.aop.RequestContext;
import org.mojavemvc.initialization.AppProperties;
import org.mojavemvc.views.EmptyView;
import org.mojavemvc.views.View;
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
    
    private final static String SUSPENDED_RESOURCE = 
            AtmosphereInterceptor.class.getName() + ".suspendedResource";
    
    private final static String INJECTED_BROADCASTER = 
            AtmosphereInterceptor.class.getName() + "injectedBroadcaster";
    
    private final static String RESUME_UUID = 
            AtmosphereInterceptor.class.getName() + ".uuid";
    
    private final static String RESUME_CANDIDATES = 
            AtmosphereInterceptor.class.getName() + ".resumeCandidates";
    
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
    public View afterAction(RequestContext ctx) {
        
        HttpServletRequest req = ctx.getRequest();
        HttpServletResponse resp = ctx.getResponse();
        
        AtmosphereConfig config = (AtmosphereConfig) req
                .getAttribute(FrameworkConfig.ATMOSPHERE_CONFIG);
        if (config == null) {
            logger.error(INSTALLATION_ERROR);
            throw new IllegalStateException(INSTALLATION_ERROR);
        }
        
        boolean useResumeAnnotation = false;
        if (Boolean.parseBoolean(
                config.getInitParameter(ApplicationConfig.SUPPORT_LOCATION_HEADER)) || 
                ctx.getActionAnnotation(Resume.class) != null) {
            useResumeAnnotation = true;
        }
        
        AtmosphereResource resource = (AtmosphereResource) req
                .getAttribute(FrameworkConfig.ATMOSPHERE_RESOURCE);
        //TODO debugging
        logger.info("resource: " + resource);
        
        //TODO get current status
        //HttpServletResponse response = ctx.getResponse();
        //if (response.getStatus() == 204) {
        //    response.setStatus(200);
        //}
        
        //TODO handle Atmosphere annotations
        /*
         * This is modeled after org.atmosphere.jersey.AtmosphereFilter
         * which does its work through a com.sun.jersey.spi.container.ContainerResponseFilter
         * which means that Atmosphere annotation processing is done after
         * the resource method is processed--in fact, it must be, as 
         * org.mojavemvc.atmosphere.SuspendResponse is a possible return value 
         */
        
        View view = null;
        Object entity = ctx.getActionReturnValue();
        View marshalledEntity = ctx.getMarshalledReturnValue();
        
        if (SuspendResponse.class.isAssignableFrom(entity.getClass())) {
            view = doSuspendResponse(entity, req, resp, resource, useResumeAnnotation);
            /* don't process any annotations */
            /*
             * what is the @Returns content-type on an action that
             * returns a SuspendResponse?--what will the action invoker
             * marshall this object to?
             * -- the SuspendResponse wraps an entity--the content-type
             *    applies to that entity
             * --- the action invoker will marshall the embedded entity
             */
            return view;
        }
        
        Broadcast broadcastAnnotation = ctx.getActionAnnotation(Broadcast.class);
        if (broadcastAnnotation != null) {
            
            List<ClusterBroadcastFilter> clusterBroadcastFilters = 
                    new ArrayList<ClusterBroadcastFilter>();
            
            Cluster clusterAnnotation = ctx.getActionAnnotation(Cluster.class);
            if (clusterAnnotation != null) {
                Class<? extends ClusterBroadcastFilter>[] clusterFilters = clusterAnnotation.value();
                for (Class<? extends ClusterBroadcastFilter> c : clusterFilters) {
                    try {
                        ClusterBroadcastFilter cbf = c.newInstance();
                        InjectorProvider.getInjector().inject(cbf);
                        cbf.setUri(clusterAnnotation.name());
                        clusterBroadcastFilters.add(cbf);
                    } catch (Throwable t) {
                        logger.warn("Invalid ClusterBroadcastFilter", t);
                    }
                }
            }
            
            view = doBroadcast(entity, req, config, clusterBroadcastFilters, 
                    broadcastAnnotation.delay(), 0, 
                    broadcastAnnotation.filters(), 
                    broadcastAnnotation.writeEntity(), null, 
                    broadcastAnnotation.resumeOnBroadcast());
        }
        
        Asynchronous asyncAnnotation = ctx.getActionAnnotation(Asynchronous.class);
        if (asyncAnnotation != null) {
            doAsynchronous();
        }
        
        Suspend suspendAnnotation = ctx.getActionAnnotation(Suspend.class);
        if (suspendAnnotation != null) {
            
            long suspendTimeout = suspendAnnotation.period();
            TimeUnit tu = suspendAnnotation.timeUnit();
            suspendTimeout = translateTimeUnit(suspendTimeout, tu);
            
            Suspend.SCOPE scope = suspendAnnotation.scope();
            Class<? extends AtmosphereResourceEventListener>[] listeners = 
                    suspendAnnotation.listeners();
            String topic = null;
            boolean writeEntity = true;
            //TODO used eventually in executeSuspend()
            String contentType = suspendAnnotation.contentType();
            
            doSuspend(entity, req, resp, resource, config, topic, suspendTimeout, scope, 
                    writeEntity, listeners, false, suspendAnnotation.resumeOnBroadcast(), 
                    useResumeAnnotation);
        }
        
        Subscribe subscribeAnnotation = ctx.getActionAnnotation(Subscribe.class);
        if (subscribeAnnotation != null) {
            
            int timeout = subscribeAnnotation.timeout();
            Class<? extends AtmosphereResourceEventListener>[] listeners = 
                    subscribeAnnotation.listeners();
            Suspend.SCOPE scope = Suspend.SCOPE.APPLICATION;
            String topic = subscribeAnnotation.value();
            boolean writeEntity = subscribeAnnotation.writeEntity();
            
            doSuspend(entity, req, resp, resource, config, topic, timeout, scope, 
                    writeEntity, listeners, true, false, useResumeAnnotation);
        }
        
        Publish publishAnnotation = ctx.getActionAnnotation(Publish.class);
        if (publishAnnotation != null) {
            view = doPublish(entity, req, config, publishAnnotation.value());
        }
        
        Resume resumeAnnotation = ctx.getActionAnnotation(Resume.class);
        if (resumeAnnotation != null) {
            //TODO suspendTimeout not used
            int suspendTimeout = resumeAnnotation.value();
            doResume(marshalledEntity, req, resp, resource);
        }
        
        Schedule scheduleAnnotation = ctx.getActionAnnotation(Schedule.class);
        if (scheduleAnnotation != null) {
            
            doSchedule(scheduleAnnotation.period(), 
                    scheduleAnnotation.waitFor(), 
                    entity, marshalledEntity, resource, req, resp, 
                    scheduleAnnotation.resumeOnBroadcast());
        }
        
        /*
         * TODO 
         * in org.atmosphere.jersey.AtmosphereFilter there are often 
         * calls to com.sun.jersey.spi.container.ContainerResponse.write(),
         * which writes the response's entity to the outputstream; this seems
         * to commit the response in the process, so it should be sufficient here
         * to simply return a View instead; if not, then try working with the 
         * HttpServletResponse directly instead and return an EmptyView
         * 
         * TODO if the response is properly committed above, there is no
         * need to return an EmptyView, as the action invoker will check
         * for a committed response and return an EmptyView itself
         */
        return view;
    }
    
    private void doResume(View marshalledEntity, HttpServletRequest req, HttpServletResponse res, AtmosphereResource r) {

        write(marshalledEntity, req, res);

        boolean sessionSupported = (Boolean) req.getAttribute(FrameworkConfig.SUPPORT_SESSION);
        if (sessionSupported) {
            r = (AtmosphereResource) req.getSession().getAttribute(SUSPENDED_RESOURCE);
        } else {
            //TODO resumeCandidates may be a global map
            //String path = response.getContainerRequest().getPath();
            //r = resumeCandidates.remove(path.substring(path.lastIndexOf("/") + 1));
        }

        if (r != null) {
            resume(r);
        } else {
            throw new IllegalStateException("Unable to retrieve suspended Response. " +
                            "Either session-support is not enabled in atmosphere.xml or the" +
                            "path used to resume is invalid.");
        }
    }
    
    private void doSuspend(Object entity, HttpServletRequest req, HttpServletResponse res, AtmosphereResource r, AtmosphereConfig config, String topic,
            long timeout, Suspend.SCOPE scope, boolean writeEntity,
            Class<? extends AtmosphereResourceEventListener>[] listeners, boolean subscribe, boolean resume, boolean useResumeAnnotation) {

        boolean resumeOnBroadcast = resumeOnBroadcast(resume, req);

        if (listeners != null) {
            for (Class<? extends AtmosphereResourceEventListener> listener : listeners) {
                try {
                    AtmosphereResourceEventListener el = listener.newInstance();
                    InjectorProvider.getInjector().inject(el);
                    r.addEventListener(el);
                } catch (Throwable t) {
                    throw new IllegalStateException("Invalid AtmosphereResourceEventListener " + listener, t);
                }
            }
        }

        Broadcaster broadcaster = (Broadcaster) req.getAttribute(INJECTED_BROADCASTER);
        if (subscribe) {
            Class<Broadcaster> c = null;
            try {
                c = (Class<Broadcaster>) Class.forName((String) req.getAttribute(ApplicationConfig.BROADCASTER_CLASS));
            } catch (Throwable e) {
                throw new IllegalStateException(e.getMessage());
            }
            broadcaster = config.getBroadcasterFactory().lookup(c, topic, true);
        }

        suspend(entity, resumeOnBroadcast, timeout, req, res, broadcaster, r, scope, writeEntity, useResumeAnnotation);
    }
    
    private View doSuspendResponse(Object entity, HttpServletRequest req, 
            HttpServletResponse res, AtmosphereResource resource, 
            boolean useResumeAnnotation) {
        
        View view = null;
        SuspendResponse<?> s = SuspendResponse.class.cast(entity);
        boolean resumeOnBroadcast = resumeOnBroadcast(s.resumeOnBroadcast(), req);

        for (AtmosphereResourceEventListener el : s.listeners()) {
            resource.addEventListener(el);
        }

        if (s.getEntity() == null) {
            //https://github.com/Atmosphere/atmosphere/issues/423
            view = new EmptyView();
        }

        Broadcaster bc = s.broadcaster();
        if (bc == null && s.scope() != Suspend.SCOPE.REQUEST) {
            /*
             * TODO how does INJECTED_BROADCASTER get set?
             */
            bc = (Broadcaster) req.getAttribute(INJECTED_BROADCASTER);
        }

        suspend(s.getEntity(), resumeOnBroadcast,
                translateTimeUnit(s.period().value(), s.period().timeUnit()), 
                req, res, bc, resource, s.scope(), s.writeEntity(), 
                useResumeAnnotation);
        
        return view;
    }
    
    /////////////////////////////////////////////// start suspend
    
    void suspend(Object entity, boolean resumeOnBroadcast,
            long timeout,
            HttpServletRequest req,
            HttpServletResponse res,
            Broadcaster bc,
            AtmosphereResource r,
            Suspend.SCOPE localScope,
            boolean flushEntity,
            boolean useResumeAnnotation) {

       // Force the status code to 200 events independently of the value of the entity (null or not)
       //TODO get current status
       //HttpServletResponse response = ctx.getResponse();
       //if (response.getStatus() == 204) {
       //    response.setStatus(200);
       //}
    
       BroadcasterFactory broadcasterFactory = 
               (BroadcasterFactory) req.getAttribute(ApplicationConfig.BROADCASTER_FACTORY);
    
       boolean sessionSupported = (Boolean) req.getAttribute(FrameworkConfig.SUPPORT_SESSION);
       URI location = null;
       // Do not add location header if already there.
       if (useResumeAnnotation && !sessionSupported && !resumeOnBroadcast && !res.containsHeader("Location")) {
           String uuid = UUID.randomUUID().toString();
    
           //TODO uriInfo is a JAX-RS object
           //location = uriInfo.getAbsolutePathBuilder().path(uuid).build("");
    
           //TODO resumeCandidates may be an application global map
           //resumeCandidates.put(uuid, r);
           
           //TODO where are RESUME_UUID and RESUME_CANDIDATES read?
           //req.setAttribute(RESUME_UUID, uuid);
           //req.setAttribute(RESUME_CANDIDATES, resumeCandidates);
       }
    
       if (bc == null && localScope != Suspend.SCOPE.REQUEST) {
           bc = r.getBroadcaster();
       }
    
       if (entity instanceof Broadcastable) {
           Broadcastable b = (Broadcastable) entity;
           bc = b.getBroadcaster();
           //response.setEntity(b.getResponseMessage());
       }
    
       if ((localScope == Suspend.SCOPE.REQUEST) && bc == null) {
           if (bc == null) {
               try {
                   String id = req.getHeader(HeaderConfig.X_ATMOSPHERE_TRACKING_ID);
                   if (id == null) {
                       id = UUID.randomUUID().toString();
                   }
    
                   bc = broadcasterFactory.get(id);
                   bc.setScope(Broadcaster.SCOPE.REQUEST);
               } catch (Exception ex) {
                   logger.error("failed to instantiate broadcaster with factory: " + broadcasterFactory, ex);
               }
           } else {
               bc.setScope(Broadcaster.SCOPE.REQUEST);
           }
       }
       r.setBroadcaster(bc);
    
       if (resumeOnBroadcast) {
           req.setAttribute(ApplicationConfig.RESUME_ON_BROADCAST, new Boolean(true));
       }
    
       //TODO
       //executeSuspend(r, timeout, resumeOnBroadcast, location, flushEntity);
    
    }
    
    /*
     * TODO 
    void executeSuspend(AtmosphereResource r, long timeout, boolean resumeOnBroadcast, 
            URI location, HttpServletRequest req,
            boolean flushEntity) {

        req.setAttribute(FrameworkConfig.CONTAINER_RESPONSE, response);
        boolean sessionSupported = (Boolean) req.getAttribute(FrameworkConfig.SUPPORT_SESSION);
        configureFilter(r.getBroadcaster());
        if (sessionSupported) {
            req.getSession().setAttribute(SUSPENDED_RESOURCE, r);
            req.getSession().setAttribute(FrameworkConfig.CONTAINER_RESPONSE, response);
        }

        req.setAttribute(SUSPENDED_RESOURCE, r);

        // Set the content-type based on the returned entity.
        try {
            MediaType contentType = response.getMediaType();
            if (contentType == null && response.getEntity() != null) {
                LinkedList<MediaType> l = new LinkedList<MediaType>();
                // Will retrun the first
                l.add(request.getAcceptableMediaType(new LinkedList<MediaType>()));
                contentType = response.getMessageBodyWorkers().getMessageBodyWriterMediaType(
                        response.getEntity().getClass(), response.getEntityType(), response.getAnnotations(), l);

                if (contentType == null || contentType.isWildcardType() || contentType.isWildcardSubtype())
                    contentType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
            }

            Object entity = response.getEntity();

            Response.ResponseBuilder b = Response.ok();
            b = configureHeaders(b);

            AtmosphereConfig config = (AtmosphereConfig) servletReq.getAttribute(ATMOSPHERE_CONFIG);

            String defaultCT = config.getInitParameter(DEFAULT_CONTENT_TYPE);
            if (defaultCT == null) {
                defaultCT = "text/plain; charset=ISO-8859-1";
            }

            String ct = contentType == null ? defaultCT : contentType.toString();

            if (defaultContentType != null) {
                ct = defaultContentType;
            }

            if (entity != null) {
                b = b.header("Content-Type", ct);
            }
            req.setAttribute(FrameworkConfig.EXPECTED_CONTENT_TYPE, ct);

            if (entity != null && flushEntity) {
                try {
                    if (Callable.class.isAssignableFrom(entity.getClass())) {
                        entity = Callable.class.cast(entity).call();
                    }
                } catch (Throwable t) {
                    logger.error("Error executing callable {}", entity);
                    entity = null;
                }

                if (location != null) {
                    b = b.header(HttpHeaders.LOCATION, location);
                }

                synchronized (response) {
                    response.setResponse(b.entity(entity).build());
                    response.write();
                }
            }

            response.setEntity(null);
            r.suspend(timeout);
        } catch (IOException ex) {
            throw new WebApplicationException(ex);
        }
    }
    */
    
    ///////////////////////////////////////////// end suspend
    
    private View doBroadcast(Object entity, HttpServletRequest req, AtmosphereConfig config, 
            List<ClusterBroadcastFilter> clusterBroadcastFilters,
            long delay, int waitFor, 
            Class<? extends BroadcastFilter>[] filters, 
            boolean writeEntity, String topic, boolean resume) {
        
        View view = null;
        AtmosphereResource resource = (AtmosphereResource) req.getAttribute(SUSPENDED_RESOURCE);
        
        Broadcaster broadcaster = resource.getBroadcaster();
        Object msg = entity;
        if (entity instanceof Broadcastable) {
            if (((Broadcastable) entity).getBroadcaster() != null) {
                broadcaster = ((Broadcastable) entity).getBroadcaster();
            }
            msg = ((Broadcastable) entity).getMessage();
        }

        if (resume) {
            configureResumeOnBroadcast(broadcaster);
        }

        addFilter(broadcaster, filters, clusterBroadcastFilters);
        if (msg == null) return view;

        if (delay == -1) {
            broadcaster.broadcast(msg);
        } else if (delay == 0) {
            broadcaster.delayBroadcast(msg);
        } else {
            broadcaster.delayBroadcast(msg, delay, TimeUnit.SECONDS);
        }
        
        if (!writeEntity) {
            view = new EmptyView();
        }
        return view;
    }
    
    @SuppressWarnings("unchecked")
    private View doPublish(Object entity, HttpServletRequest req, AtmosphereConfig config, String topic) {
        
        AtmosphereResource resource = (AtmosphereResource) req.getAttribute(SUSPENDED_RESOURCE);

        Class<Broadcaster> broadCasterClass = null;
        try {
            broadCasterClass = 
                    (Class<Broadcaster>) Class.forName(
                            (String) req.getAttribute(ApplicationConfig.BROADCASTER_CLASS));
        } catch (Throwable e) {
            throw new IllegalStateException(e.getMessage());
        }
        resource.setBroadcaster(config.getBroadcasterFactory().lookup(broadCasterClass, topic, true));
        
        return doBroadcast(entity, req, config, new ArrayList<ClusterBroadcastFilter>(), -1, -1, null, true, topic, false);
    }
    
    private void doSchedule(int timeout, int waitFor, Object entity, View marshalledEntity,
            AtmosphereResource resource, HttpServletRequest req, HttpServletResponse resp, 
            boolean resume) {
        
        Object message = entity;
        Broadcaster broadcaster = resource.getBroadcaster();
        if (entity instanceof Broadcastable) {
            broadcaster = ((Broadcastable) entity).getBroadcaster();
            message = ((Broadcastable) entity).getMessage();
            entity = ((Broadcastable) entity).getResponseMessage();
        }

        if (entity != null) {
            //TODO see com.sun.jersey.spi.container.ContainerResponse.write()
            /*
             * in the case of the Mojave framework, the entity here will never be null: it
             * will always be at least a View--unless it is a Broadcastable and the 
             * getResponseMessage() returns null 
             * 
             * 3 possibilities for entity here:
             * 
             * 1. it is a View (that was marshalled to itself in the action invoker)
             * 2. it is a non-Broadcastable entity
             * 3. it was a Broadcastable and is now the Broadcastable response message
             *     
             * NOTE: committed means: write status and headers and flush the outputstream
             * 
             * if the entity is other than a SuspendResponse, as it is in this case,
             * then a @Returns annotation must be present on the method to indicate the
             * return content-type--the action invoker will marshall any embedded entity
             */
            //write entity to response outputstream
            /*
             * NOTE:
             * we can work with the marshalledEntity and we don't need to write out the 
             * entity itself, as it would have been marshalled even if it were embedded, 
             * as in the case of Broadcastable.getResponseMessage()
             */
            write(marshalledEntity, req, resp);
        }

        if (resume) {
            configureResumeOnBroadcast(broadcaster);
        }
        
        broadcaster.scheduleFixedBroadcast(message, waitFor, timeout, TimeUnit.SECONDS);
    }
    
    /*---------------------------------*/
    
    private void addFilter(Broadcaster bc, Class<? extends BroadcastFilter>[] filters, 
            List<ClusterBroadcastFilter> clusterBroadcastFilters) {
        
        configureFilter(bc, filters, clusterBroadcastFilters);
    }
    
    private void configureFilter(Broadcaster bc, Class<? extends BroadcastFilter>[] filters, 
            List<ClusterBroadcastFilter> clusterBroadcastFilters) {
        
        if (bc == null) throw new RuntimeException(new IllegalStateException("Broadcaster cannot be null"));

        /**
         * Here we can't predict if it's the same set of filter shared across all Broadcaster as
         * Broadcaster can have their own BroadcasterConfig instance.
         */
        BroadcasterConfig c = bc.getBroadcasterConfig();
        // Already configured
        if (c.hasFilters()) {
            return;
        }

        if (clusterBroadcastFilters != null) {
            // Always the first one, before any transformation/filtering
            for (ClusterBroadcastFilter cbf : clusterBroadcastFilters) {
                cbf.setBroadcaster(bc);
                c.addFilter(cbf);
            }
        }

        BroadcastFilter f = null;
        if (filters != null) {
            for (Class<? extends BroadcastFilter> filter : filters) {
                try {
                    f = filter.newInstance();
                    InjectorProvider.getInjector().inject(f);
                } catch (Throwable t) {
                    logger.warn("Invalid @BroadcastFilter: " + filter, t);
                }
                c.addFilter(f);
            }
        }
    }
    
    private void write(View marshalledEntity, HttpServletRequest req, HttpServletResponse resp) {
        
        try {
            
            marshalledEntity.render(req, resp, appProperties);
            if (!resp.isCommitted()) {
                resp.getOutputStream().flush();
            }
            
        } catch (Exception e) {
            // TODO 
            logger.error("error writing entity", e);
        }
    }
    
    private void configureResumeOnBroadcast(Broadcaster b) {
        
        Iterator<AtmosphereResource> i = b.getAtmosphereResources().iterator();
        while (i.hasNext()) {
            HttpServletRequest r = (HttpServletRequest) i.next().getRequest();
            r.setAttribute(ApplicationConfig.RESUME_ON_BROADCAST, true);
        }
    }
    
    private boolean resumeOnBroadcast(boolean resumeOnBroadcast, HttpServletRequest req) {
        
        String transport = req.getHeader(HeaderConfig.X_ATMOSPHERE_TRANSPORT);
        if (transport != null && (transport.equals(HeaderConfig.JSONP_TRANSPORT) || 
                transport.equals(HeaderConfig.LONG_POLLING_TRANSPORT))) {
            return true;
        }
        return resumeOnBroadcast;
    }
    
    private long translateTimeUnit(long period, TimeUnit tu) {
        if (period == -1) return period;

        switch (tu) {
            case SECONDS:
                return TimeUnit.MILLISECONDS.convert(period, TimeUnit.SECONDS);
            case MINUTES:
                return TimeUnit.MILLISECONDS.convert(period, TimeUnit.MINUTES);
            case HOURS:
                return TimeUnit.MILLISECONDS.convert(period, TimeUnit.HOURS);
            case DAYS:
                return TimeUnit.MILLISECONDS.convert(period, TimeUnit.DAYS);
            case MILLISECONDS:
                return period;
            case MICROSECONDS:
                return TimeUnit.MILLISECONDS.convert(period, TimeUnit.MICROSECONDS);
            case NANOSECONDS:
                return TimeUnit.MILLISECONDS.convert(period, TimeUnit.NANOSECONDS);
        }
        return period;
    }
    
    private void resume(AtmosphereResource resource) {
        resource.resume();
    }

    private void doAsynchronous() {
        // TODO Auto-generated method stub
    }
}
