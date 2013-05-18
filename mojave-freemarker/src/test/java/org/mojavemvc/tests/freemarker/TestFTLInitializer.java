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
package org.mojavemvc.tests.freemarker;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mojavemvc.initialization.AppPropertyCollector;
import org.mojavemvc.initialization.AppResources;
import org.mojavemvc.initialization.InitParams;
import org.mojavemvc.initialization.internal.FTLInitializer;
import org.mojavemvc.views.FTL;

import freemarker.template.Configuration;

/**
 * 
 * @author Luis Antunes
 */
public class TestFTLInitializer {

    @Test
    public void createsConfiguration() throws Exception {
        
        InitParams params = mock(InitParams.class);
        AppResources resources = mock(AppResources.class);
        AppPropertyCollector collector = mock(AppPropertyCollector.class);
        
        FTLInitializer init = new FTLInitializer();
        init.initialize(params, resources, collector);
        
        verify(collector).addProperty(eq(FTL.CONFIG_PROPERTY), any(Configuration.class));
    }
}
