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
package org.mojavemvc.tests.mustache;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Test;
import org.mojavemvc.tests.AbstractWebTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author Luis Antunes
 */
public class TestMultiClient extends AbstractWebTest {

    private static final Logger logger = LoggerFactory.getLogger("org.mojavemvc.tests.mustache");
    
    @Test
    public void manyDifferentConcurrentRequests() throws Exception {
        
        error = false;
        int numClients = 50;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(numClients);

        for (int i = 0; i < numClients; i++) {
            new SynchedThread(new Client(i), startLatch, stopLatch).start();
        }

        startLatch.countDown(); /* let all threads proceed */
        stopLatch.await(); /* wait for all to finish */
        
        if (error) {
            Assert.fail();
        }
    }
    
    /*-----------------Helper methods and classes-------------------------------*/

    private static boolean error = false;
    
    private class Client implements Runnable {

        private final int clientNum;
        private final WebClient client;
        
        public Client(int clientNum) {
            this.clientNum = clientNum;
            this.client = new WebClient();
        }
        
        public void run() {
            
            try {
                
                HtmlPage page = (HtmlPage) client.getPage(
                        "http://localhost:8989/mvc/serv/mustache/basic/client" + clientNum);
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("client" + clientNum, h1.getTextContent());
                
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
        }
    }
    
    private static class SynchedThread extends Thread {

        private CountDownLatch startLatch;
        private CountDownLatch stopLatch;

        public SynchedThread(Runnable runnable, CountDownLatch startLatch, CountDownLatch stopLatch) {
            super(runnable);
            this.startLatch = startLatch;
            this.stopLatch = stopLatch;
        }

        @Override
        public void run() {
            try {
                startLatch.await(); /* wait for other threads */
                super.run();
                stopLatch.countDown(); /* signal that this thread is finished */
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private static void checkEquals(String expected, String actual) {

        if (!expected.equals(actual)) {
            throw new RuntimeException("[" + expected + "]" + " not equal to [" + actual + "]");
        }
    }

    private static void checkEquals(int expected, int actual) {

        if (expected != actual) {
            throw new RuntimeException("[" + expected + "]" + " not equal to [" + actual + "]");
        }
    }
}
