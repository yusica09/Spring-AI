package com.example.demo.service;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiService {
	//## 필드 ##
	private ChatClient chatClient;
	private OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;
	private OpenAiAudioSpeechModel openAiAudioSpeechModel;
	
	//## 생성자 ##
	public AiService(ChatClient.Builder chatClientBuilder,
			OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel,
			OpenAiAudioSpeechModel openAiAudioSpeechModel) {
		chatClient = chatClientBuilder.build();
		this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel;
		this.openAiAudioSpeechModel = openAiAudioSpeechModel;
	}
	
	//## 메소드 ##
	public String stt(byte[] bytes) {
		//음성 데이터(byte[])를 ByteArrayResource로 생성 -> 프롬프트 생성시 사용
		Resource audioResource = new ByteArrayResource(bytes);
		//모델 옵션 설정
		OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
				.model("whisper-1")
				//언어를 명시하지 않아도 자동으로 감지되나, 명시하면 처리속도 다소 향상가능
				.language("ko")
				.build();
		//프롬프트 생성
		AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource,options);
		//모델에 요청하고 응답받기
		AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt);
		String text = response.getResult().getOutput();
		
		return text;
	}
	
	public byte[] tts(String text) {
		//모델 옵션 설정
		OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
				.model("gpt-40-mini-tts")
				.voice(SpeechRequest.Voice.ALLOY)
				.responseFormat(SpeechRequest.AudioResponseFormat.MP3)
				.speed(1.0)
				.build();
		//프롬프트 생성
		TextToSpeechPrompt prompt = new TextToSpeechPrompt(text,options);
		//모델을 호출하고 응답받기
		TextToSpeechResponse response = openAiAudioSpeechModel.call(prompt);
		byte[] bytes = response.getResult().getOutput();
		
		return bytes;
	}
	

}
