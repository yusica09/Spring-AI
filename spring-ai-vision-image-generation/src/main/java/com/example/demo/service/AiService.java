package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class AiService {
	// ## 필드 ##
	private ChatClient chatClient;
	
	@Autowired
	//이미지 생성형 모델을 사용하기 위한 인터페이스
	private ImageModel imagemodel;

	// ## 생성자 ##
	public AiService(ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}
	
	// ## 메소드 ##
	public Flux<String> imageAnalysis(String question, String contentType, byte[] bytes){
		//시스템 메시지 생성
		SystemMessage systemMessage = SystemMessage.builder()
				.text("""
						당신은 이미지 분석 전문가입니다.
						사용자 질문에 맞게 이미지를 분석하고 답변을 한국어로 하세요.
						""")
				.build();
		//미디어 생성
		Media media = Media.builder()
				.mimeType(MimeType.valueOf(contentType))
				.data(new ByteArrayResource(bytes))
				.build();
		//사용자 메시지 생성- 텍스트질문과 Media 포함시킴 -> 멀티모달리티
		UserMessage userMessage = UserMessage.builder()
				.text(question)
				.media(media)
				.build();
		//프롬프트 생성
		Prompt prompt = Prompt.builder()
				.messages(systemMessage, userMessage)
				.build();
		
		//LLM에 요청하고 응답받기
		Flux<String> flux = chatClient.prompt(prompt)
				.stream()
				.content();
		
		return flux;
	}
	
	
	

}
