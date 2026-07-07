package com.example.demo.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

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
				.model("gpt-4o-mini-tts")
				.voice(SpeechRequest.Voice.ALLOY)
				.responseFormat(SpeechRequest.AudioResponseFormat.MP3)
				.speed(1.0f)
				.build();
		//프롬프트 생성 
		//SpeechPrompt는 이후 버전에서 이름이 바뀜
		SpeechPrompt prompt = new SpeechPrompt(text,options);
		//모델을 호출하고 응답받기
		SpeechResponse response = openAiAudioSpeechModel.call(prompt);
		byte[] bytes = response.getResult().getOutput();
		
		return bytes;
	}
	
	public Map<String, String> chatText(String question){
		//LLM으로 요청하고, 텍스트 응답 얻기
		String textAnswer = chatClient.prompt()
				.system("50자 이내로 한국어로 답변해주세요.")
				.user(question)
				.call()
				.content();
		
		//TTS 모델로 요청하고 응답으로 받은 음성 데이터를 JSON형태로 전송하기 위해 base64 문자열로 변환
		byte[] audio = tts(textAnswer);
		String base64Audio = Base64.getEncoder().encodeToString(audio);
		
		//텍스트 답변과 음성 답변을 Map에 저장
		Map<String, String> response = new HashMap<>();
		response.put("text", textAnswer);
		response.put("audio", base64Audio);
		
		return response;
	}
	
	public Flux<byte[]> ttsFlux(String text) {
		// 모델 옵션 설정
	    OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
	    		.model("gpt-4o-mini-tts")
	    		.voice(SpeechRequest.Voice.ALLOY)
	    		.responseFormat(AudioResponseFormat.MP3)
	    		.speed(1.0f)
	    		.build();

	    // 프롬프트 생성
	    SpeechPrompt prompt = new SpeechPrompt(text, options);

	    // 모델로 요청하고 응답받기 -> call()대신 stream()사용
	    Flux<SpeechResponse> response = openAiAudioSpeechModel.stream(prompt);
	    Flux<byte[]> flux = response.map(speechResponse -> speechResponse.getResult().getOutput());
	    
	    return flux;
	}
	
	public Flux<byte[]> chatVoiceSttLlmTts(byte[] audioBytes) {
		// STT를 이용해서 음성 질문을 텍스트 질문으로 변환
	    String textQuestion = stt(audioBytes);

	    // 텍스트 질문으로 LLM에 요청하고, 텍스트 응답 얻기
	    String textAnswer = chatClient.prompt()
	    		.system("50자 이내로 답변해주세요.")
	    		.user(textQuestion)
	        	.call()
	        	.content();

	    // TTS를 이용해서 비동기 음성 데이터 얻기
	    Flux<byte[]> flux = ttsFlux(textAnswer);
	    
	    return flux;
	}
	

	
	
	

}
