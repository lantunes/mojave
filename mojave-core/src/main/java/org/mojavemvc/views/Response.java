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
package org.mojavemvc.views;

import javax.servlet.http.HttpServletResponse;


/**
 * @author Luis Antunes
 */
public class Response {

    public static class OK extends AbstractResponse<OK> {

        public OK() {
            super(HttpServletResponse.SC_OK);
        }
        
        @Override
        protected OK self() {
            return this;
        }
    }
    
    public static class Accepted extends AbstractResponse<Accepted> {

        public Accepted() {
            super(HttpServletResponse.SC_ACCEPTED);
        }
        
        @Override
        protected Accepted self() {
            return this;
        }
    }
    
    public static class Created extends AbstractResponse<Created> {

        public Created() {
            super(HttpServletResponse.SC_CREATED);
        }
        
        @Override
        protected Created self() {
            return this;
        }
    }
    
    public static class Found extends AbstractResponse<Found> {

        public Found() {
            super(HttpServletResponse.SC_FOUND);
        }
        
        @Override
        protected Found self() {
            return this;
        }
    }
    
    public static class MovedPermanently extends 
        AbstractResponse<MovedPermanently> {

        public MovedPermanently() {
            super(HttpServletResponse.SC_MOVED_PERMANENTLY);
        }
        
        @Override
        protected MovedPermanently self() {
            return this;
        }
    }
    
    public static class NoContent extends AbstractResponse<NoContent> {

        public NoContent() {
            super(HttpServletResponse.SC_NO_CONTENT);
        }
        
        @Override
        protected NoContent self() {
            return this;
        }
    }
    
    public static class NotModified extends AbstractResponse<NotModified> {

        public NotModified() {
            super(HttpServletResponse.SC_NOT_MODIFIED);
        }
        
        @Override
        protected NotModified self() {
            return this;
        }
    }
    
    public static class PartialContent extends 
        AbstractResponse<PartialContent> {

        public PartialContent() {
            super(HttpServletResponse.SC_PARTIAL_CONTENT);
        }
        
        @Override
        protected PartialContent self() {
            return this;
        }
    }
    
    public static class ResetContent extends AbstractResponse<ResetContent> {

        public ResetContent() {
            super(HttpServletResponse.SC_RESET_CONTENT);
        }
        
        @Override
        protected ResetContent self() {
            return this;
        }
    }
    
    public static class SeeOther extends AbstractResponse<SeeOther> {

        public SeeOther() {
            super(HttpServletResponse.SC_SEE_OTHER);
        }
        
        @Override
        protected SeeOther self() {
            return this;
        }
    }
    
    public static class UseProxy extends AbstractResponse<UseProxy> {

        public UseProxy() {
            super(HttpServletResponse.SC_USE_PROXY);
        }
        
        @Override
        protected UseProxy self() {
            return this;
        }
    }
    
    public static class TemporaryRedirect extends 
        AbstractResponse<TemporaryRedirect> {

        public TemporaryRedirect() {
            super(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        }
        
        @Override
        protected TemporaryRedirect self() {
            return this;
        }
    }
    
    public static class BadRequest extends AbstractResponse<BadRequest> {

        public BadRequest() {
            super(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        @Override
        protected BadRequest self() {
            return this;
        }
    }
    
    public static class Unauthorized extends AbstractResponse<Unauthorized> {

        public Unauthorized() {
            super(HttpServletResponse.SC_UNAUTHORIZED);
        }
        
        @Override
        protected Unauthorized self() {
            return this;
        }
    }
    
    public static class PaymentRequired extends AbstractResponse<PaymentRequired> {

        public PaymentRequired() {
            super(HttpServletResponse.SC_PAYMENT_REQUIRED);
        }
        
        @Override
        protected PaymentRequired self() {
            return this;
        }
    }
    
    public static class Forbidden extends AbstractResponse<Forbidden> {

        public Forbidden() {
            super(HttpServletResponse.SC_FORBIDDEN);
        }
        
        @Override
        protected Forbidden self() {
            return this;
        }
    }
    
    public static class NotFound extends AbstractResponse<NotFound> {

        public NotFound() {
            super(HttpServletResponse.SC_NOT_FOUND);
        }
        
        @Override
        protected NotFound self() {
            return this;
        }
    }
    
    public static class MethodNotAllowed extends AbstractResponse<MethodNotAllowed> {

        public MethodNotAllowed() {
            super(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        
        @Override
        protected MethodNotAllowed self() {
            return this;
        }
    }
    
    public static class NotAcceptable extends AbstractResponse<NotAcceptable> {

        public NotAcceptable() {
            super(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
        
        @Override
        protected NotAcceptable self() {
            return this;
        }
    }
    
    public static class ProxyAuthenticationRequired extends 
        AbstractResponse<ProxyAuthenticationRequired> {

        public ProxyAuthenticationRequired() {
            super(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
        }
        
        @Override
        protected ProxyAuthenticationRequired self() {
            return this;
        }
    }
    
    public static class RequestTimeout extends AbstractResponse<RequestTimeout> {

        public RequestTimeout() {
            super(HttpServletResponse.SC_REQUEST_TIMEOUT);
        }
        
        @Override
        protected RequestTimeout self() {
            return this;
        }
    }
    
    public static class Conflict extends AbstractResponse<Conflict> {

        public Conflict() {
            super(HttpServletResponse.SC_CONFLICT);
        }
        
        @Override
        protected Conflict self() {
            return this;
        }
    }
    
    public static class Gone extends AbstractResponse<Gone> {

        public Gone() {
            super(HttpServletResponse.SC_GONE);
        }
        
        @Override
        protected Gone self() {
            return this;
        }
    }
    
    public static class LengthRequired extends AbstractResponse<LengthRequired> {

        public LengthRequired() {
            super(HttpServletResponse.SC_LENGTH_REQUIRED);
        }
        
        @Override
        protected LengthRequired self() {
            return this;
        }
    }
    
    public static class PreconditionFailed extends 
        AbstractResponse<PreconditionFailed> {

        public PreconditionFailed() {
            super(HttpServletResponse.SC_PRECONDITION_FAILED);
        }
        
        @Override
        protected PreconditionFailed self() {
            return this;
        }
    }
    
    public static class RequestEntityTooLarge extends 
        AbstractResponse<RequestEntityTooLarge> {

        public RequestEntityTooLarge() {
            super(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        }
        
        @Override
        protected RequestEntityTooLarge self() {
            return this;
        }
    }
    
    public static class RequestURITooLong extends 
        AbstractResponse<RequestURITooLong> {

        public RequestURITooLong() {
            super(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
        }
        
        @Override
        protected RequestURITooLong self() {
            return this;
        }
    }
    
    public static class UnsupportedMediaType extends 
        AbstractResponse<UnsupportedMediaType> {

        public UnsupportedMediaType() {
            super(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        
        @Override
        protected UnsupportedMediaType self() {
            return this;
        }
    }
    
    public static class RequestedRangeNotSatisfiable extends 
        AbstractResponse<RequestedRangeNotSatisfiable> {

        public RequestedRangeNotSatisfiable() {
            super(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
        }
        
        @Override
        protected RequestedRangeNotSatisfiable self() {
            return this;
        }
    }
    
    public static class ExpectationFailed extends 
        AbstractResponse<ExpectationFailed> {

        public ExpectationFailed() {
            super(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
        
        @Override
        protected ExpectationFailed self() {
            return this;
        }
    }
    
    public static class InternalServerError extends 
        AbstractResponse<InternalServerError> {

        public InternalServerError() {
            super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        @Override
        protected InternalServerError self() {
            return this;
        }
    }
    
    public static class NotImplemented extends AbstractResponse<NotImplemented> {

        public NotImplemented() {
            super(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
        
        @Override
        protected NotImplemented self() {
            return this;
        }
    }
    
    public static class BadGateway extends AbstractResponse<BadGateway> {

        public BadGateway() {
            super(HttpServletResponse.SC_BAD_GATEWAY);
        }
        
        @Override
        protected BadGateway self() {
            return this;
        }
    }
    
    public static class ServiceUnavailable extends 
        AbstractResponse<ServiceUnavailable> {

        public ServiceUnavailable() {
            super(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        
        @Override
        protected ServiceUnavailable self() {
            return this;
        }
    }
    
    public static class GatewayTimeout extends AbstractResponse<GatewayTimeout> {

        public GatewayTimeout() {
            super(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        }
        
        @Override
        protected GatewayTimeout self() {
            return this;
        }
    }
    
    public static class HTTPVersionNotSupported extends 
        AbstractResponse<HTTPVersionNotSupported> {

        public HTTPVersionNotSupported() {
            super(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED);
        }
        
        @Override
        protected HTTPVersionNotSupported self() {
            return this;
        }
    }
}
