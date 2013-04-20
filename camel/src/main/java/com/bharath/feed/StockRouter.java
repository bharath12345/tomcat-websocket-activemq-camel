package com.bharath.feed;

import com.bharath.websocket.WebSocketStockPricesRoute;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * User: bharadwaj
 * Date: 21/04/13
 * Time: 12:55 AM
 */
public class StockRouter extends Thread {
    CamelContext context = new DefaultCamelContext();

    public StockRouter(ActiveMQConnectionFactory connectionFactory) {
        try {
            context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
            context.addRoutes(new WebSocketStockPricesRoute());

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void run() {
        try {
            context.start();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
