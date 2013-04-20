package com.bharath.feed;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author kmccormack
 */
public abstract class FeedThread extends Thread {

    public boolean running = true;
    private Random random;
    private ActiveMQConnectionFactory connectionFactory;

    abstract Message createStockMessage(Session session, Stock updatedStock) throws Exception;

    public FeedThread(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void run() {
        try {

            // create the connection factory
            Connection connection = connectionFactory.createConnection();
            //Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create the session and topic
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic quoteTopic = session.createTopic("stockQuoteTopic");
            MessageProducer quotesProducer = session.createProducer(quoteTopic);
            quotesProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // load in the portfolio of stocks
            Portfolio portfolio = new Portfolio();
            List<Stock> stocksList = portfolio.getStocks();

            random = new Random();

            while (running) {

                Iterator<Stock> i = stocksList.iterator();
                while (i.hasNext()) {
                    Stock stock = i.next();
                    simulateChange(stock);
                    Message msg = createStockMessage(session, stock);
                    msg.setStringProperty("symbol", stock.getSymbol());
                    quotesProducer.send(msg);
                    //System.out.println("msg sent = " + msg.toString());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }


            }
        } catch (Exception ex) {
            System.out.println("Problem creating feed");
            ex.printStackTrace();
        }
    }

    private void simulateChange(Stock stock) {

        double maxChange = stock.getOpen() * 0.005;
        double change = maxChange - random.nextDouble() * maxChange * 2;
        stock.setChange(change);
        double last = stock.getLast() + change;

        if (last < stock.getOpen() + stock.getOpen() * 0.15 && last > stock.getOpen() - stock.getOpen() * 0.15) {
            stock.setLast(last);
        } else {
            stock.setLast(stock.getLast() - change);
        }

        if (stock.getLast() > stock.getHigh()) {
            stock.setHigh(stock.getLast());
        } else if (stock.getLast() < stock.getLow()) {
            stock.setLow(stock.getLast());
        }
        stock.setDate(new Date());

    }
}
