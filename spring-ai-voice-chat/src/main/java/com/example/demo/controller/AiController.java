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

}
