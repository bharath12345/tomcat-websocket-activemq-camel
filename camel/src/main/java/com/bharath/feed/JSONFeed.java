package com.bharath.feed;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.Session;

/**
 *
 * @author kmccormack
 */

@Component
public class JSONFeed {

    private static FeedThread feedThread;
    private static StockRouter stockRouter;

    public static void main(String args[]) {
		JSONFeed feed = new JSONFeed();
		feed.start();
	}

    @PostConstruct
    public void init() {
        try {
            System.out.println("bharath: postconstruct");

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost:61616");

            stockRouter = new StockRouter(connectionFactory);

            feedThread = new FeedThread(connectionFactory) {
                @Override
                Message createStockMessage(Session session, Stock updatedStock) throws Exception {
                    String msgText = new StringBuffer()
                            .append("{")
                            .append("\"symbol\": \"" + updatedStock.getSymbol() + "\",\n")
                            .append("\"name\": \"" + updatedStock.getName() + "\",\n")
                            .append("\"low\": " + updatedStock.getLow() + ",\n")
                            .append("\"high\": " + updatedStock.getHigh() + ",\n")
                            .append("\"open\": " + updatedStock.getOpen() + ",\n")
                            .append("\"last\": " + updatedStock.getLast() + ",\n")
                            .append("\"change\": " + updatedStock.getLast() + "")
                            .append("}\n")
                            .toString();
                    System.out.println(msgText);
                    return session.createTextMessage(msgText);
                }

            };

            start();


        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	public void start() {
		feedThread.start();
        stockRouter.start();
	}

	public void stop() {
        feedThread.running = false;
		feedThread = null;
        stockRouter = null;
	}
}
