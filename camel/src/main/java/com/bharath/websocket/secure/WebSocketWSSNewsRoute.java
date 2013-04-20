
package com.bharath.websocket.secure;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class WebSocketWSSNewsRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

           from("activemq:topic:newsTopic").routeId("fromJMStoWebSocketSecureNews")
             .log(LoggingLevel.DEBUG, ">> News info received : ${body}")
             .delay(5000)
             .to("websocket://0.0.0.0:8443/newsTopic?sendToAll=true" +
                     "&sslContextParametersRef=#sslContextParameters&staticResources=classpath:webapp");

        // TO BE TESTED
        // crossOriginFilterOn=true&allowedOrigins=*&filterPath=wss&

    }
}
