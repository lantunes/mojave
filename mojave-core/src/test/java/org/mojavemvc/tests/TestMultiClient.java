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

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Test;
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

    private static final Logger logger = LoggerFactory.getLogger(TestMultiClient.class);

    @Test
    public void sameRequest_10clients() throws Exception {

        error = false;

        final int numClients = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(numClients);

        for (int i = 0; i < numClients; i++) {
            new SynchedThread(new Client2(), startLatch, stopLatch).start();
            // System.out.println(Thread.currentThread().getName() + " start: "
            // + System.nanoTime());
        }

        startLatch.countDown(); /* let all threads proceed */
        stopLatch.await(); /* wait for all to finish */

        if (error) {
            Assert.fail();
        }
    }

    @Test
    public void clientWithParam_20clients() throws Exception {

        error = false;

        final int numClients = 20;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(numClients);

        for (int i = 0; i < numClients; i++) {
            new SynchedThread(new ClientWithParam("param" + i), startLatch, stopLatch).start();
        }

        startLatch.countDown(); /* let all threads proceed */
        stopLatch.await(); /* wait for all to finish */

        if (error) {
            Assert.fail();
        }
    }

    @Test
    public void differentRequests_14clients() throws Exception {

        error = false;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(14);

        new SynchedThread(new Client1(), startLatch, stopLatch).start();
        new SynchedThread(new Client2(), startLatch, stopLatch).start();
        new SynchedThread(new Client3(), startLatch, stopLatch).start();
        new SynchedThread(new Client4(), startLatch, stopLatch).start();
        new SynchedThread(new Client5(), startLatch, stopLatch).start();
        new SynchedThread(new Client6(), startLatch, stopLatch).start();
        new SynchedThread(new Client7(), startLatch, stopLatch).start();
        new SynchedThread(new Client8(), startLatch, stopLatch).start();
        new SynchedThread(new Client9(), startLatch, stopLatch).start();
        new SynchedThread(new Client10(), startLatch, stopLatch).start();
        new SynchedThread(new Client11(), startLatch, stopLatch).start();
        new SynchedThread(new Client12(), startLatch, stopLatch).start();
        new SynchedThread(new Client13(), startLatch, stopLatch).start();
        new SynchedThread(new Client14(), startLatch, stopLatch).start();

        startLatch.countDown(); /* let all threads proceed */
        stopLatch.await(); /* wait for all to finish */

        if (error) {
            Assert.fail();
        }
    }

    private static boolean error = false;

    /*
     * for manual testing of concurrency
     */
    public static void main(String[] args) throws Exception {

        /*
         * comment out beforeTests() and afterTests() and change port in url if
         * testing with an external container
         */
        beforeTests();
        String url = "http://localhost:8989/mvc/serv";

        int i = 0;
        while (true) {
            i++;
            System.out.println("running iteration " + i);

            /*
             * use ExecutorService approach or SynchedThread approach but not
             * both
             */

            /*
             * ExecutorService executor = Executors.newFixedThreadPool(14);
             * executor.submit(new Client1(url)); executor.submit(new
             * Client2(url)); executor.submit(new Client3(url));
             * executor.submit(new Client4(url)); executor.submit(new
             * Client5(url)); executor.submit(new Client6(url));
             * executor.submit(new Client7(url)); executor.submit(new
             * Client8(url)); executor.submit(new Client9(url));
             * executor.submit(new Client10(url)); executor.submit(new
             * Client11(url)); executor.submit(new Client12(url));
             * executor.submit(new Client13(url)); executor.submit(new
             * Client14(url)); executor.shutdown(); //wait long enough for all
             * the threads to finish Thread.sleep( 500 );
             */

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch stopLatch = new CountDownLatch(14);
            new SynchedThread(new Client1(url), startLatch, stopLatch).start();
            new SynchedThread(new Client2(url), startLatch, stopLatch).start();
            new SynchedThread(new Client3(url), startLatch, stopLatch).start();
            new SynchedThread(new Client4(url), startLatch, stopLatch).start();
            new SynchedThread(new Client5(url), startLatch, stopLatch).start();
            new SynchedThread(new Client6(url), startLatch, stopLatch).start();
            new SynchedThread(new Client7(url), startLatch, stopLatch).start();
            new SynchedThread(new Client8(url), startLatch, stopLatch).start();
            new SynchedThread(new Client9(url), startLatch, stopLatch).start();
            new SynchedThread(new Client10(url), startLatch, stopLatch).start();
            new SynchedThread(new Client11(url), startLatch, stopLatch).start();
            new SynchedThread(new Client12(url), startLatch, stopLatch).start();
            new SynchedThread(new Client13(url), startLatch, stopLatch).start();
            new SynchedThread(new Client14(url), startLatch, stopLatch).start();
            startLatch.countDown(); // let all threads proceed
            stopLatch.await(); // wait for all to finish

            if (error) {
                System.out.println("!!!error during iteration " + i);
                break;
            }
        }

        afterTests();
    }

    /*-----------------Helper methods and classes-------------------------------*/

    private static class Client1 extends BaseClient implements Runnable {

        public Client1() {
            super();
        }

        public Client1(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/afterctx2");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from default", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client1 request complete!");
        }
    }

    private static class Client2 extends BaseClient implements Runnable {

        public Client2() {
            super();
        }

        public Client2(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/index");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client2 request complete!");
        }
    }

    private static class Client3 extends BaseClient implements Runnable {

        public Client3() {
            super();
        }

        public Client3(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client3 request complete!");
        }
    }

    private static class Client4 extends BaseClient implements Runnable {

        public Client4() {
            super();
        }

        public Client4(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/index/test");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the test.jsp file of the IndexController testAction!", h1.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client4 request complete!");
        }
    }

    private static class Client5 extends BaseClient implements Runnable {

        public Client5() {
            super();
        }

        public Client5(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/index/with-param?var=hello");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from hello", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client5 request complete!");
        }
    }

    private static class Client6 extends BaseClient implements Runnable {

        public Client6() {
            super();
        }

        public Client6(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/index/another-param?var=hello");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from hello", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client6 request complete!");
        }
    }

    private static class Client7 extends BaseClient implements Runnable {

        public Client7() {
            super();
        }

        public Client7(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/index/some-service?var=hello");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the some-service.jsp file of the IndexController someServiceAction!",
                        h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("answered hello", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client7 request complete!");
        }
    }

    private static class Client8 extends BaseClient implements Runnable {

        public Client8() {
            super();
        }

        public Client8(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL
                        + "/index/test-annotation?var=annotationTest");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the param.jsp file of the IndexController withParamAction!", h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from annotationTest", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client8 request complete!");
        }
    }

    private static class Client9 extends BaseClient implements Runnable {

        public Client9() {
            super();
        }

        public Client9(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL
                        + "/annot/some-action?var=contollerAnnotationTest");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the some-controller.jsp file of the SomeControllerClass doSomething action!",
                        h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("contollerAnnotationTest", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client9 request complete!");
        }
    }

    private static class Client10 extends BaseClient implements Runnable {

        public Client10() {
            super();
        }

        public Client10(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL
                        + "/index/param-annotation-string?p1=param1");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the params.jsp file of the IndexController @Param test action!",
                        h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from param1", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client10 request complete!");
        }
    }

    private static class Client11 extends BaseClient implements Runnable {

        public Client11() {
            super();
        }

        public Client11(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL
                        + "/index/param-annotation-string?p1=param2");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the params.jsp file of the IndexController @Param test action!",
                        h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from param2", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client11 request complete!");
        }
    }

    private static class Client12 extends BaseClient implements Runnable {

        public Client12() {
            super();
        }

        public Client12(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL
                        + "/index/param-annotation-string?p1=param3");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the params.jsp file of the IndexController @Param test action!",
                        h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from param3", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client12 request complete!");
        }
    }

    private static class Client13 extends BaseClient implements Runnable {

        public Client13() {
            super();
        }

        public Client13(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client
                        .getPage(baseURL + "/some-stateful/set-var?var=statetest1");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the stateful.jsp file of the SomeStatefulController someAction!",
                        h1.getTextContent());

                /* make a second request */
                page = client.getPage(baseURL + "/some-stateful/get-var");
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from statetest1", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client13 request complete!");
        }
    }

    private static class Client14 extends BaseClient implements Runnable {

        public Client14() {
            super();
        }

        public Client14(String baseURL) {
            super(baseURL);
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client
                        .getPage(baseURL + "/some-stateful/set-var?var=statetest2");
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the stateful.jsp file of the SomeStatefulController someAction!",
                        h1.getTextContent());

                /* make a second request */
                page = client.getPage(baseURL + "/some-stateful/get-var");
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from statetest2", h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client14 request complete!");
        }
    }

    private static class ClientWithParam extends BaseClient implements Runnable {

        private String param;

        public ClientWithParam(String param) {
            super();
            this.param = param;
        }

        public void run() {
            try {
                HtmlPage page = (HtmlPage) client.getPage(baseURL + "/index/param-annotation-string?p1="
                        + param);
                DomNodeList<HtmlElement> elements = page.getElementsByTagName("h1");
                checkEquals(1, elements.size());
                HtmlElement h1 = elements.get(0);
                checkEquals("This is the params.jsp file of the IndexController @Param test action!",
                        h1.getTextContent());
                elements = page.getElementsByTagName("h2");
                checkEquals(1, elements.size());
                HtmlElement h2 = elements.get(0);
                checkEquals("Hello from " + param, h2.getTextContent());
            } catch (Exception e) {
                logger.error("error", e);
                error = true;
            }
            logger.debug("Client10 request complete!");
        }
    }

    private static abstract class BaseClient {

        protected String baseURL = "http://localhost:8989/mvc/serv";
        protected WebClient client;

        public BaseClient() {
            client = new WebClient();
        }

        public BaseClient(String baseURL) {
            this();
            this.baseURL = baseURL;
        }
    }

    public static class SynchedThread extends Thread {

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
                // System.out.println(Thread.currentThread().getName() +
                // " start: " + System.nanoTime());
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
