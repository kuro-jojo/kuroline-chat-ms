package com.kuro.kuroline_chat_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class KurolineChatMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KurolineChatMsApplication.class, args);
	}

}
