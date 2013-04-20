
package com.bharath.websocket;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class WebSocketStockPricesRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

           from("activemq:topic:stockQuoteTopic").routeId("fromJMStoWebSocketQuotes")
             .log(LoggingLevel.DEBUG,">> Stock price received : ${body}")
             .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    System.out.println("We just downloaded: " + exchange.getIn().getHeader("CamelFileName"));
                }
             })
             .to("websocket://0.0.0.0:9090/stockQuoteTopic?sendToAll=true&staticResources=classpath:webapp")
             .to("file:/Applications/apache-tomcat-7.0.34/logs/stocks.json");

    }
}





