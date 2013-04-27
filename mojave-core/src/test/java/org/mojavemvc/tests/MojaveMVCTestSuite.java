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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 * @author Luis Antunes
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestBaseActionSignature.class,
    TestMappedControllerDatabase.class,
    TestGuiceInitializer.class,
    TestHttpActionInvoker.class,
    TestDefaultErrorHandler.class,
    TestDefaultJSPErrorHandler.class,
    TestDefaultErrorHandlerFactory.class,
    TestDefaultJSPErrorHandlerFactory.class,
    TestViews.class,
    TestHttpRequestRouter.class,
    TestParamPathHelper.class,
    TestRouteHelper.class,
    TestRegexRouteMap.class,
    TestRegexRoute.class,
    TestRoute.class,
    TestHttpParameterMapSource.class,
    TestJSON.class,
    TestXML.class,
    TestPlainText.class,
    TestEntityMarshallers.class,
    TestFrontController.class,
    TestMultiClient.class
})
public class MojaveMVCTestSuite {
    /*
     *  the class remains completely empty,
     *  being used only as a placeholder for 
     *  the above annotations 
     */
}
