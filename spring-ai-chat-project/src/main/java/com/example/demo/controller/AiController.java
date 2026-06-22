package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AiService;
import com.example.demo.service.AiServiceByChatClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {
	
	@Autowired
	private AiService aiService;
	
	/*
	@Autowired
	private AiServiceByChatClient aiService;
	*/
	
	//클라이언트에서 받아야할 요청 본문타입(consumes), 응답 본문으로 제공할 타입(produces) 명시
	@PostMapping(
			value = "/chat-model",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.TEXT_PLAIN_VALUE
	)
	public String chatModel(@RequestParam("question") String question){
		String answerStreamText = aiService.generateText(question);
		return answerStreamText;
	}
	
	@PostMapping(
			value = "/chat-model-stream",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_NDJSON_VALUE //라인으로 구분된 청크 텍스트
			)
	public Flux<String> chatModelStream(@RequestParam("question") String question){
		Flux<String> answerStreamText = aiService.generateStreamText(question);
		return answerStreamText;
	}

}
