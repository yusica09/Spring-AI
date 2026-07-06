package com.example.demo.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.AiService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@Slf4j
public class AiController {
	//## 필드 ##
	@Autowired
	private AiService aiService;
	
	//## 메소드 ##
	@PostMapping(
			value = "/stt",
			/*stt() 메소드는 클라이언트가 전송한 multipart/form-data요청에서 파일파트를 매개변수로 받기때문에,
			consumes 속성에 MediaType.MULTIPART_FORM_DATA_VALUE를 지정*/
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.TEXT_PLAIN_VALUE
			)
	public String stt(@RequestParam("speech") MultipartFile speech) throws IOException{
		String text = aiService.stt(speech.getBytes());
		return text;
	}
	
	@PostMapping(
			value = "/tts",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
			)
	public byte[] tts(@RequestParam("text") String text) {
		//MultipartFile에서 음성데이터인 byte[]를 추출한 후, AiService의 stt() 메소드를 호출하여 변환된 텍스트 얻음
		byte[] bytes = aiService.tts(text);
		return bytes;
	}
	
	@PostMapping(
			value = "/chat-text",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
			)
	public Map<String, String> chatText(@RequestParam("question") String question) {
		Map<String, String> response = aiService.chatText(question);
		return response;
	}
	
	@PostMapping(
		    value = "/chat-voice-stt-llm-tts", 
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
		    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
		    )
	public void chatVoiceSttLlmTts(@RequestParam("question") MultipartFile question, 
		HttpServletResponse response) throws Exception {
		
		// 비동기 음성 데이터를 Flux<byte[]>을 얻기
		Flux<byte[]> flux = aiService.chatVoiceSttLlmTts(question.getBytes());

		// 음성 데이터를 응답 본문으로 스트림 출력
		OutputStream outputStream = response.getOutputStream();
		for (byte[] chunk : flux.toIterable()) {
			outputStream.write(chunk);
		    outputStream.flush();
		}
	}
	
}
