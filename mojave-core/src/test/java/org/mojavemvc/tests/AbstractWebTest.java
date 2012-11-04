package org.mojavemvc.tests;

import static junit.framework.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public abstract class AbstractWebTest {

    private static final int port = 8989;

    private static final String host = "http://localhost:";
    
    private static Server jetty;

    private WebClient client;

    @BeforeClass
    public static void beforeTests() throws Exception {

        jetty = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        jetty.setConnectors(new Connector[] { connector });

        WebAppContext wactx = new WebAppContext();
        wactx.setClassLoader(TestFrontController.class.getClassLoader());
        wactx.setParentLoaderPriority(true);
        wactx.setContextPath("/mvc");
        wactx.setWar("src/test/resources/standard");

        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] { wactx, new DefaultHandler() });
        jetty.setHandler(handlers);

        jetty.start();
    }

    @Before
    public void beforeEachTest() {

        client = new WebClient();
    }

    @AfterClass
    public static void afterTests() throws Exception {

        if (jetty != null) {
            jetty.stop();
            jetty.destroy();
            jetty = null;
        }
    }
    
    /*-------DSL----------*/
    
    protected RequestedPage makeRequest(String uri) throws Exception {
        return new RequestedPage(uri);
    }
    
    protected RequestAssertion assertThatRequestFor(String uri) throws Exception {
        return new RequestAssertion(uri);
    }
    
    protected RequestAssertion assertThatPOSTRequestFor(String uri, 
            RequestParameter...params) throws Exception {
        
        return new RequestAssertion(HttpMethod.POST, uri, params);
    }
    
    protected RequestAssertion assertThatGETRequestFor(String uri, 
            RequestParameter...params) throws Exception {
        
        return new RequestAssertion(HttpMethod.GET, uri, params);
    }
    
    protected RequestAssertion assertThatPUTRequestFor(String uri, 
            RequestParameter...params) throws Exception {
        
        return new RequestAssertion(HttpMethod.PUT, uri, params);
    }
    
    protected RequestAssertion assertThatDELETERequestFor(String uri, 
            RequestParameter...params) throws Exception {
        
        return new RequestAssertion(HttpMethod.DELETE, uri, params);
    }
    
    protected RequestAssertion assertThatOPTIONSRequestFor(String uri, 
            RequestParameter...params) throws Exception {
        
        return new RequestAssertion(HttpMethod.OPTIONS, uri, params);
    }
    
    protected HEADRequestAssertion assertThatHEADRequestFor(String uri) 
                throws Exception {
        return new HEADRequestAssertion(uri);
    }
    
    protected RequestParameter withParam(String name, String value) {
        return new RequestParameter(name, value);
    }
    
    protected PageElementContentAssertion withContent(String expected) {
        return new PageElementContentAssertion(expected);
    }
    
    protected StandardInput withValueFor(String inputName) {
        return new StandardInput(inputName);
    }
    
    protected CheckBoxInput withCheckBox(String name) {
        return new CheckBoxInput(name);
    }
    
    protected ElementAttribute withAttribute(String name) {
        return new ElementAttribute(name);
    }
    
    protected void newWebClient() {
        client = new WebClient();
    }
    
    protected class RequestParameter {
        private final String name;
        private final String value;
        
        public RequestParameter(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return name;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    protected class RequestedPage {
        private Page page;
        
        public RequestedPage(String uri) throws Exception {
            page = client.getPage(host + port + uri);
            assertNotNull(page);
        }
        
        public String getTagContent(String name) {
            DomNodeList<HtmlElement> elements = ((HtmlPage)page).getElementsByTagName(name);
            assertEquals(1, elements.size());
            HtmlElement tag = elements.get(0);
            return tag.getTextContent();
        }
    }
    
    protected class RequestAssertion {
        private Page page;
        
        public RequestAssertion(String uri) throws Exception {
            page = client.getPage(host + port + uri);
        }
        
        public RequestAssertion(HttpMethod method, String uri, 
                RequestParameter...params) throws Exception {
            
            WebRequest wr = new WebRequest(new URL(host + port + uri), method);
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            for (RequestParameter pair : params) {
                pairs.add(new NameValuePair(pair.getName(), pair.getValue()));
            }
            wr.setRequestParameters(pairs);
            page = client.getPage(wr);
        }
        
        public PageAssertion producesPage() {
            assertNotNull(page);
            return new PageAssertion(page);
        }
        
        public void producesErrorPage() {
            producesPage().withTag("h1", withContent("Error"));
        }
        
        public RequestAssertion afterSubmittingForm(String formName, 
                FormInputValue...formVals) throws Exception {
            
            HtmlForm form = ((HtmlPage)page).getFormByName(formName);
            for (FormInputValue formVal : formVals) {
                HtmlInput input = form.getInputByName(formVal.getInputName());
                formVal.handleInput(input);
            }
            HtmlSubmitInput submit = form.getInputByValue("submit");
            page = submit.click();
            return this;
        }
    }
    
    protected class HEADRequestAssertion {
        
        private Page page;
        
        public HEADRequestAssertion(String uri) throws Exception {
            WebRequest wr = new WebRequest(new URL(host + port + uri),
                    HttpMethod.HEAD);
            page = client.getPage(wr);
            assertNotNull(page);
        }
        
        /*
         * HEAD responses are not supposed to contain a message-body as per RFC
         * 2616
         */
        public void succeeds() {
            List<NameValuePair> headers = page.getWebResponse().getResponseHeaders();
            NameValuePair calledHeader = null;
            for (NameValuePair pair : headers) {
                if ("CALLED".equalsIgnoreCase(pair.getName())) {
                    calledHeader = pair;
                }
            }
            assertNotNull(calledHeader);
            assertEquals("called", calledHeader.getValue());
        }
    }
    
    protected abstract class FormInput {
        protected final String inputName;
        
        public FormInput(String inputName) {
            this.inputName = inputName;
        }
    }
    
    protected class StandardInput extends FormInput {
        public StandardInput(String inputName) {
            super(inputName);
        }
        
        public FormInputValue setTo(String value) {
            return new StandardInputValue(inputName, value);
        }
    }
    
    protected class CheckBoxInput extends FormInput {
        public CheckBoxInput(String inputName) {
            super(inputName);
        }
        
        public CheckBoxInputValue checked() {
            return new CheckBoxInputValue(inputName, true);
        }
    }
    
    protected abstract class FormInputValue {

        protected final String inputName;
        
        public FormInputValue(String inputName) {
            this.inputName = inputName;
        }
        
        public String getInputName() {
            return inputName;
        }
        
        public abstract void handleInput(HtmlInput input);
    }
    
    protected class StandardInputValue extends FormInputValue {
        
        private final String inputValue;
        
        public StandardInputValue(String inputName, String inputValue) {
            super(inputName);
            this.inputValue = inputValue;
        }
        
        public void handleInput(HtmlInput input) {
            input.setValueAttribute(inputValue);
        }
    }
    
    protected class CheckBoxInputValue extends FormInputValue {
        
        private final boolean checked;
        
        public CheckBoxInputValue(String inputName, boolean checked) {
            super(inputName);
            this.checked = checked;
        }
        
        public void handleInput(HtmlInput input) {
            ((HtmlCheckBoxInput)input).setChecked(checked);
        }
    }
    
    protected class PageAssertion {
        
        private final Page page;
        
        public PageAssertion(Page page) {
            this.page = page;
        }
        
        public PageAssertion withTag(String element, 
                PageElementContentAssertion pageElementAssertion) {
            
            DomNodeList<HtmlElement> elements = ((HtmlPage)page).getElementsByTagName(element);
            assertEquals(1, elements.size());
            HtmlElement htmlElement = elements.get(0);
            pageElementAssertion.doAssertion(htmlElement);
            return this;
        }
        
        public PageAssertion withElement(String elementId, 
                PageElementContentAssertion pageElementAssertion) {
            
            HtmlElement htmlElement = ((HtmlPage)page).getElementById(elementId);
            pageElementAssertion.doAssertion(htmlElement);
            return this;
        }
        
        public PageAssertion withElement(String elementId, 
                ElementAttributeAssertion elementAttrAssertion) {
            
            HtmlElement htmlElement = ((HtmlPage)page).getElementById(elementId);
            elementAttrAssertion.doAssertion(htmlElement);
            return this;
        }
        
        public PageAssertion withContentType(String contentType) {
            assertEquals(contentType, page.getWebResponse().getContentType());
            return this;
        }
        
        public PageAssertion withContent(String content) {
            assertEquals(content, page.getWebResponse().getContentAsString());
            return this;
        }
    }
    
    protected class PageElementContentAssertion {
        private final String expected;
        
        public PageElementContentAssertion(String expected) {
            this.expected = expected;
        }
        
        public void doAssertion(HtmlElement element) {
            assertEquals(expected, element.getTextContent());
        }
    }
    
    protected class ElementAttribute {
        private final String name;
        
        public ElementAttribute(String name) {
            this.name = name;
        }
        
        public ElementAttributeAssertion setTo(String expected) {
            return new ElementAttributeAssertion(name, expected);
        }
    }
    
    protected class ElementAttributeAssertion {
        private final String attribute;
        private final String expected;
        
        public ElementAttributeAssertion(String attribute, String expected) {
            this.attribute = attribute;
            this.expected = expected;
        }
        
        public void doAssertion(HtmlElement element) {
            assertEquals(expected, element.getAttribute(attribute));
        }   
    }
}
