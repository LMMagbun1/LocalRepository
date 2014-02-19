package com.kintegration.esb.spring.activemq;

import java.util.Date;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

/**
 * 
 *
 */
@Component
public class Requestor 
{
	
	private static final Logger LOG =
			LoggerFactory.getLogger(Requestor.class);
	
	@Autowired
	protected JmsTemplate jmsTemplate;
	
	@Autowired
	Destination replyTo;
	
	@Autowired
	JmsTemplate jmsTemplateReplyTo;
	
	protected int numberOfMessages = 100;
	
	
	
	public void sendMessages() throws JMSException {
		
		final StringBuilder payload = new StringBuilder();
		
		for (int i = 0; i < numberOfMessages; i++) {
			
			if (payload.length() > 0)
				payload.delete(0, payload.length()-1);
			
			final int counter = i;
			
			payload.append("Message [").append(i).append("] sent at: ").append(new Date());
			
			jmsTemplate.send(new MessageCreator(){

				public Message createMessage(Session session) throws JMSException {
					
					TextMessage message =
							session.createTextMessage(payload.toString());
					message.setIntProperty("messageCount", counter);
					
					message.setJMSReplyTo(replyTo);
					
					System.out.println("Sending message number[" + counter + "]");
					//LOG.info("Sending message number[" + counter + "]");
					return message;
				}
				
			});
			
			receiveMessages();
		}				
		
	}
	
	public void receiveMessages() throws JMSException {
		Message msg = jmsTemplateReplyTo.receive();
		System.out.println(((TextMessage)msg).getText());
	}
	
    public static void main( String[] args ) {
    	
    	ClassPathXmlApplicationContext ctxApplicationContext =
    			new ClassPathXmlApplicationContext("classpath:/META-INF/spring-context/esb-spring-context.xml");
    	
    	Requestor rq = (Requestor) ctxApplicationContext.getBean(Requestor.class);
    	
    	try {
    		rq.sendMessages();
    	}
    	catch(JMSException ex) {
    		ex.printStackTrace();
    	}
    	
    	finally {
    		ctxApplicationContext.close();
    	}
    	
    }
}
