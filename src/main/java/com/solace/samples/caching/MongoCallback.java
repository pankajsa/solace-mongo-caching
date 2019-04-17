package com.solace.samples.caching;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.mongodb.Block;

import lombok.extern.slf4j.Slf4j;

import com.solacesystems.jcsmp.InvalidPropertiesException;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;


@Slf4j
@Component
public class MongoCallback implements  Block<Document>{

	@Autowired
	private Environment env;
	
	private Topic topic;
	private XMLMessageProducer prod;
	
	
	@Override
	public void apply(final Document document) {
		log.info(document.toJson());
		
        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

        msg.setText(document.toJson());

        try {
			prod.send(msg,topic);
			
		} catch (JCSMPException e) {
			e.printStackTrace();
		}
	}
	
	private MongoCallback() {
		log.info("MongoCallback");
	}
	
	public void setupSolaceClient() throws JCSMPException {
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, env.getProperty("solace.host"));
        properties.setProperty(JCSMPProperties.VPN_NAME, env.getProperty("solace.vpn"));
        properties.setProperty(JCSMPProperties.USERNAME, env.getProperty("solace.username"));
        properties.setProperty(JCSMPProperties.PASSWORD, env.getProperty("solace.password"));
        JCSMPSession session =  JCSMPFactory.onlyInstance().createSession(properties);

        topic = JCSMPFactory.onlyInstance().createTopic(env.getProperty("solace.pt"));
        session.connect();
        
        prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });



        
	}
	
}



