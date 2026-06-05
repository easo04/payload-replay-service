package com.example.payload_replay_service;

import com.example.payload_replay_service.config.CaptureProperties;
import com.example.payload_replay_service.config.DynamoDbProperties;
import com.example.payload_replay_service.config.ReplayProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({CaptureProperties.class, ReplayProperties.class, DynamoDbProperties.class})
public class PayloadReplayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayloadReplayServiceApplication.class, args);
	}

}
