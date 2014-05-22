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
package org.mojavemvc.tests;

import static org.junit.Assert.*;

import org.bigtesting.routd.Router;
import org.junit.Before;
import org.junit.Test;
import org.mojavemvc.core.MojaveRoute;

/**
 * 
 * @author Luis Antunes
 */
public abstract class RouterContractTest {

    private Router router;
    
    @Before
    public void beforeEachTest() {
        router = newRouter();
    }
    
    protected abstract Router newRouter();
    
    @Test
    public void getRoute_Root() {
        MojaveRoute r1 = new MojaveRoute(null, null, null);
        router.add(r1);
        assertEquals(r1, router.route("/"));
    }
    
    @Test
    public void getRoute_SimilarMatchesConstant() {
        MojaveRoute r1 = new MojaveRoute(null, null, "clients/all");
        MojaveRoute r2 = new MojaveRoute(null, null, "clients/:id");
        router.add(r1);
        router.add(r2);
        assertEquals(r1, router.route("/clients/all"));
    }
    
    @Test
    public void getRoute_SimilarMatchesParam() {
        MojaveRoute r1 = new MojaveRoute(null, null, "clients/all");
        MojaveRoute r2 = new MojaveRoute(null, null, "clients/:id");
        router.add(r1);
        router.add(r2);
        assertEquals(r2, router.route("/clients/123"));
    }
    
    @Test
    public void getRoute_IgnoresParamRegion() {
        MojaveRoute r1 = new MojaveRoute("cntrl", null, null);
        MojaveRoute r2 = new MojaveRoute("cntrl", null, "clients/:id");
        router.add(r1);
        router.add(r2);
        assertEquals(r1, router.route("/cntrl"));
    }
    
    @Test
    public void getRoute_FindsParamRegion() {
        MojaveRoute r1 = new MojaveRoute("cntrl", null, null);
        MojaveRoute r2 = new MojaveRoute("cntrl", null, "clients/:id");
        router.add(r1);
        router.add(r2);
        assertEquals(r2, router.route("/cntrl/clients/23455"));
    }
    
    @Test
    public void getRoute_DistinguishesBetweenControllerAndAction() {
        MojaveRoute r1 = new MojaveRoute("cntrl", null, null);
        MojaveRoute r2 = new MojaveRoute(null, "actn", null);
        router.add(r1);
        router.add(r2);
        assertEquals(r2, router.route("/actn"));
    }
    
    @Test
    public void getRoute_NotFound() {
        MojaveRoute r1 = new MojaveRoute("cntrl", null, null);
        MojaveRoute r2 = new MojaveRoute(null, "actn", null);
        router.add(r1);
        router.add(r2);
        assertNull(router.route("/test"));
    }
    
    @Test
    public void getRoute_MultiParamRegions_Multiple() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", ":id");
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn", ":id/:name");
        router.add(r1);
        router.add(r2);
        assertEquals(r2, router.route("/cntrl/actn/123/bob"));
    }
    
    @Test
    public void getRoute_MultiParamRegions_Single() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", ":id");
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn", ":id/:name");
        router.add(r1);
        router.add(r2);
        assertEquals(r1, router.route("/cntrl/actn/123"));
    }
    
    @Test
    public void getRoute_CustomRegexAlpha() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", ":id<[0-9]+>");
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn", ":id<[a-z]+>");
        router.add(r1);
        router.add(r2);
        assertEquals(r2, router.route("/cntrl/actn/bob"));
    }
    
    @Test
    public void getRoute_CustomRegexNumeric() {
        MojaveRoute r1 = new MojaveRoute("cntrl", "actn", ":id<[0-9]+>");
        MojaveRoute r2 = new MojaveRoute("cntrl", "actn", ":id<[a-z]+>");
        router.add(r1);
        router.add(r2);
        assertEquals(r1, router.route("/cntrl/actn/123"));
    }
}
