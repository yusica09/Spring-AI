package com.example.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiServiceStepBackPrompt {
	//## 필드 ##
	private ChatClient chatClient;

	//## 생성자 ##
	public AiServiceStepBackPrompt(ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}
	
	//## 메소드 ##
	public String stepBackPrompt(String question) throws Exception{
		String questions = chatClient.prompt()
				//LLM이 단계별로 질문을 분해하는 과정에서 맨마지막 질문이 사용자의 질문과 다를 수 있기 때문에 내용 추가
				.user("""
						사용자 질문을 처리하기 Step-Back 프롬프트 기법을 사용하려고 합니다.
						사용자 질문을 단계별 질문들로 재구성해 주세요.
						맨 마지막 질문은 사용자 질문과 일치해야 합니다.
						단계별 질문을 항목으로 하는 JSON 배열로 출력해 주세요.
						예시: ["...","...","...", ...]
						사용자 질문: %s
						""".formatted(question))
				.call()
				.content();
		//응답 문자열에서 순수 JSON만 얻기위해
		String json = questions.substring(questions.indexOf("["), questions.indexOf("]")+1);
		log.info(json);
		
		//JSON을 파싱해서 List<String> 타입으로 얻음
		ObjectMapper objectMapper = new ObjectMapper();
		List<String> listQuestion = objectMapper.readValue(json, new TypeReference<List<String>>() {});
		
		String[] answerArray = new String[listQuestion.size()];
		for(int i=0; i<listQuestion.size(); i++) {
			String stepQuestion = listQuestion.get(i);
			String stepAnswer = getStepAnswer(stepQuestion, answerArray);
			answerArray[i] = stepAnswer;
			log.info("단계{} 질문: {}, 답변: {}", i+1, stepQuestion, stepAnswer);
		}
		return answerArray[answerArray.length-1];
	}

	private String getStepAnswer(String question, String... preStepAnswers) {
		String context = "";
		//이전 단계 답변들은 context 변수에 누적시킴. 
		//Objects.requireNonNullElse(prevStepAnswer, "")는 preStepAnswer가 null일 때 ""반환
		for(String prevStepAnswer : preStepAnswers) {
			context += Objects.requireNonNullElse(prevStepAnswer, "");
		}
		//ChatClinet로 LLM에 요청할때 프롬프트 안에는 현단계 질문과 이전단계 답변들이 문맥으로 추가
		String answer = chatClient.prompt()
				.user(""" 
						%s
						문맥: %s
						""".formatted(question,context))
				.call()
				.content();
		return answer;
	}

}
