package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.demo.advisor.AdvisorA;
import com.example.demo.advisor.AdvisorB;
import com.example.demo.advisor.AdvisorC;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiService1 {
	// ##### 필드 #####
    private ChatClient chatClient;

    // ##### 생성자 #####
    public AiService1(ChatClient.Builder chatClientBuilder) {
    				// 기본 Advisor로 추가하는 방법 사용
    	this.chatClient = chatClientBuilder.defaultAdvisors(
    			new AdvisorA(),
                new AdvisorB())
    			.build();
  }

    // ##### 메소드 #####
    // 동기 방식의 Spring Web을 위한 메소드
    /* Advisor가 추가되지 않는다면 동기방식의 Spring Web에서도 비동기 응답을 위한 요청 매핑 메소드 선언 가능
     * Advisor가 추가되면 비동기 응답을 위한 요청 매핑 메소드를 선언 불가능
     */
    
    public String advisorChain1(String question) {
    				// 요청 시 마다 Advisor를 ChatClient에 추가하는 방법 사용
    	String response = chatClient.prompt()
    			.advisors(new AdvisorC())
    			.user(question)
    			.call()
    			.content();
    	return response;
    }
  
    // 비동기 스트림 방식의 Spring Reactive Web을 위한 메소드
    public Flux<String> advisorChain2(String question) {
    				// 요청 시 마다 Advisor를 ChatClient에 추가하는 방법 사용
    	Flux<String> response = chatClient.prompt()
    			.advisors(new AdvisorC())
    			.user(question)
    			.stream()
    			.content();
    	return response;
    }  
}
