package com.example.demo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.AiService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {
	//## 필드 ##
	@Autowired
	private AiService aiService;
	
	//## 요청 매핑 메소드 ##
	@PostMapping(
			value = "/image-analysis",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_NDJSON_VALUE
			)
	public Flux<String> imageAnalysis(@RequestParam("question") String question,
									@RequestParam("attach") MultipartFile attach) throws IOException{
		//이미지가 업로드 되지 않았을 경우
		if(attach == null || !attach.getContentType().contains("image/")) {
			Flux<String> response = Flux.just("이미지를 올려주세요.");
			
			return response;
		}
		Flux<String> flux = aiService.imageAnalysis(question, attach.getContentType(), attach.getBytes());
		
		return flux;
	}

}
