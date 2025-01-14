/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.aws2.translate;

import org.apache.camel.BindToRegistry;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Translate2ProducerTest extends CamelTestSupport {

    @BindToRegistry("amazonTranslateClient")
    AmazonAWSTranslateMock clientMock = new AmazonAWSTranslateMock();

    @EndpointInject("mock:result")
    private MockEndpoint mock;

    @Test
    public void translateTextTest() throws Exception {

        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:translateText", new Processor() {
            @Override
            public void process(Exchange exchange) {
                exchange.getIn().setHeader(Translate2Constants.SOURCE_LANGUAGE, Translate2LanguageEnum.ITALIAN);
                exchange.getIn().setHeader(Translate2Constants.TARGET_LANGUAGE, Translate2LanguageEnum.ENGLISH);
                exchange.getIn().setBody("ciao");
            }
        });

        assertMockEndpointsSatisfied();

        String resultGet = exchange.getIn().getBody(String.class);
        assertEquals("Hello", resultGet);

    }

    @Test
    public void translateTextPojoTest() throws Exception {

        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:translatePojoText", new Processor() {
            @Override
            public void process(Exchange exchange) {
                exchange.getIn()
                        .setBody(TranslateTextRequest.builder().sourceLanguageCode(Translate2LanguageEnum.ITALIAN.toString())
                                .targetLanguageCode(Translate2LanguageEnum.ENGLISH.toString()).text("ciao").build());
            }
        });

        assertMockEndpointsSatisfied();

        String resultGet = exchange.getIn().getBody(String.class);
        assertEquals("Hello", resultGet);

    }

    @Test
    public void translateTextTestOptions() throws Exception {

        mock.expectedMessageCount(1);
        Exchange exchange = template.request("direct:translateTextOptions", new Processor() {
            @Override
            public void process(Exchange exchange) {
                exchange.getIn().setBody("ciao");
            }
        });

        assertMockEndpointsSatisfied();

        String resultGet = exchange.getIn().getBody(String.class);
        assertEquals("Hello", resultGet);

    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:translateText")
                        .to("aws2-translate://test?translateClient=#amazonTranslateClient&operation=translateText")
                        .to("mock:result");
                from("direct:translatePojoText").to(
                        "aws2-translate://test?translateClient=#amazonTranslateClient&operation=translateText&pojoRequest=true")
                        .to("mock:result");
                from("direct:translateTextOptions").to(
                        "aws2-translate://test?translateClient=#amazonTranslateClient&operation=translateText&sourceLanguage=it&targetLanguage=en")
                        .to("mock:result");
            }
        };
    }
}
