package info.reinput.reinput_notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ReinputNotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReinputNotificationServiceApplication.class, args);
	}

}
