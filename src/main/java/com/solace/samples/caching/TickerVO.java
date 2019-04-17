package com.solace.samples.caching;

import java.time.LocalDateTime;

import lombok.Data;

public @Data class TickerVO {
	private String symbol;
	private double price;
	private long messageId;
	private LocalDateTime datetime;
}
