package com.kintegration.esb.spring.activemq;

import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class Replier implements MessageListener
{
	public final String MESSAGE_COUNT = "messageCount";
	private static final Logger logger =
			LoggerFactory.getLogger(Replier.class);
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	private AtomicInteger counter = null;
	
    public static void main( String[] args ) {
    	ClassPathXmlApplicationContext ctx = null;
    	try {
    		ctx = 
    				new ClassPathXmlApplicationContext("classpath:/META-INF/spring-context/esb-spring-context.xml");
    		
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	finally {
    		//ctx.close();
    	}
    	
    	//Replier replier = (Replier) ctx.getBean(Replier.class);
        
    }

	public void onMessage(Message message) {
		
		try {
			int messageCount = message.getIntProperty(MESSAGE_COUNT);
			
			if (message instanceof TextMessage) {
				
				TextMessage tm = (TextMessage)message;
				
				String msg = tm.getText();
				
				Queue q = (Queue)tm.getJMSReplyTo();
				
				//System.out.println("Processed message " + msg + " . " + messageCount);
				//System.out.println("The reply queue name is: " + q.getQueueName());
				
				msg = msg.concat(" Leon you are going to be all right, OK!!!");
				
				logger.info("Processed message '{}' . value='{}'", msg, messageCount);
				logger.info("The reply queue name is: '{}'", q.getQueueName());
				
				counter.incrementAndGet();
				
				//tm.setText(msg);
				//tm.setJMSCorrelationID(tm.getJMSMessageID());
				
				sendMessages(tm);
			}
		} catch (JMSException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void sendMessages(final Message msg) {
		jmsTemplate.send(new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				
				TextMessage tx = session.createTextMessage();
				TextMessage rqMessage = (TextMessage)msg;
				
				tx.setText(rqMessage.getText().concat(" Leon you are going to be all right, OK!!! Don't worry, be happy"));
				
				return tx;
				
				
			}
			
		});
	}
}
