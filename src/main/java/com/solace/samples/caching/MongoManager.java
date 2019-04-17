package com.solace.samples.caching;

import java.time.LocalDateTime;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MongoManager {
		
	@Autowired
    private MongoCallback mongoCallback;

	private MongoCollection<Document> collection = null;
	
	@Value("${db.userid}")
	private String dbUserid;

	@Value("${db.password}")
	private String dbPassword;
	
	@Value("${db.uri}")
	private String dbUri;

	private MongoManager() {
	}
		
	public void init() {
		log.info("init");
		
		String uri = "mongodb+srv://" + dbUserid + ":" + dbPassword + dbUri;

		MongoClient mongoClient = MongoClients.create(uri);
		MongoDatabase database = mongoClient.getDatabase("nse");
		
		collection = database.getCollection("ticker");
		
	}
	
	
	public void deleteAll() {
		if (collection == null)
			init();

		collection.drop();
		System.out.println("all deleted");
	}
	
	public void send() {
		TickerVO ticker = new TickerVO();
		ticker.setSymbol("GOOGL");
		ticker.setPrice(1100.11);
		ticker.setDatetime(LocalDateTime.now());
		ticker.setMessageId(100001);
		save(ticker);

		ticker.setSymbol("AAPL");
		ticker.setPrice(200.22);
		ticker.setDatetime(LocalDateTime.now());
		ticker.setMessageId(100002);
		save(ticker);

		ticker.setSymbol("GOOGL");
		ticker.setPrice(1100.33);
		ticker.setDatetime(LocalDateTime.now());
		ticker.setMessageId(100003);
		save(ticker);

		ticker.setSymbol("GOOGL");
		ticker.setPrice(1100.44);
		ticker.setDatetime(LocalDateTime.now());
		ticker.setMessageId(100004);
		save(ticker);

	}
		
	public void save(TickerVO ticker) {
	
		if (collection == null)
			init();
		
		Document doc = new Document("symbol", ticker.getSymbol())
                .append("price", ticker.getPrice())
                .append("messageid", ticker.getMessageId())
                .append("time", ticker.getDatetime())
				;

		collection.insertOne(doc);
		log.info("Send Message");
		
	}
	
	public void query() {

//		MongoCallback callback = new MongoCallback();

		getRecords(100000, 100010, mongoCallback);
	}
	
	public void getRecords(long startId, long endId, MongoCallback callback) {
		if (collection == null)
			init();
		log.info("getRecords");
		
		collection.find(
				Filters.and(
					Filters.gte("messageid", startId),

					Filters.lte("messageid", endId)
				))
		.forEach(callback);
		
		
		
	}
	
	
	
}
