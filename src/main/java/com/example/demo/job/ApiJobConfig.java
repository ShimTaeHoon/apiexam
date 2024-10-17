package com.example.demo.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.openapi.entity.ApiResult;
import com.example.demo.openapi.repository.ApiResultRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
public class ApiJobConfig {

	@Autowired
	JobRepository jobRepository;
	
	@Autowired
	PlatformTransactionManager manager;
	
	@Autowired
	ApiResultRepository apiResultRepository;
	
	@Bean
	public Job simpleJob1() throws IOException {
		return new JobBuilder("ApiJob", jobRepository)
				.start(step1())
				.next(step2())
				.next(step3())
				.build();
	}
	
	@Bean
	public Step step1() throws IOException {
		
		TaskletStep a = new StepBuilder("step1..", jobRepository)
				.tasklet(testTasklet(), manager).build();

		return a;
		
	}
	
	@Bean
	public Step step2() throws IOException {
		
		TaskletStep a = new StepBuilder("step2..", jobRepository)
				.tasklet(test2Tasklet(), manager).build();

		return a;
		
	}
	
	@Bean
	public Step step3() throws IOException {
	
		TaskletStep a = new StepBuilder("step3..", jobRepository)
				.tasklet(test3Tasklet(), manager).build();

		return a;
		
	}
	
	@Bean
	public Tasklet testTasklet() throws IOException {
		return ((contribution, chunkContext) -> {
			
			System.out.println("Step1. API 호출하기");
			
			String serviceKey = "BFa7HKNN1IuGlZkxLHbOVV3w3SQRHiR%2BGlUqDwpG86ionFcSpvdECOMfM39KAOpDQh1Bt5QvIrhdJdPhWV03WA%3D%3D";
			String dataType = "JSON";
			String code = "11B20201";
			
			StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstMsgService/getLandFcst");
			urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
			urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("regId", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8"));
			URL url = new URL(urlBuilder.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + conn.getResponseCode());
			BufferedReader rd;
			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				}
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				rd.close();
				conn.disconnect();
				
				StepContext context = chunkContext.getStepContext(); 
				ExecutionContext executionContext = context.getStepExecution().getJobExecution().getExecutionContext();
				executionContext.put("weatherData", sb.toString());
				
				System.out.println(sb.toString());
	
				return RepeatStatus.FINISHED;
		});
	}
	
	@Bean
	public Tasklet test2Tasklet() {
		return ((contribution, chunkContext) -> {

			System.out.println("Step2. 응답 데이터 파싱하기");

			 StepContext context = chunkContext.getStepContext();
		     ExecutionContext executionContext = context.getStepExecution().getJobExecution().getExecutionContext();
		     String weatherContext = (String) executionContext.get("weatherData");

		     ObjectMapper mapper = new ObjectMapper();
		     mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        
		     Root root = mapper.readValue(weatherContext, Root.class);
		      
		     System.out.println("결과코드 : " + root.response.header.resultCode);
		     System.out.println("결과메세지 : " + root.response.header.resultMsg);
		     System.out.println("totalcount : " + root.response.body.totalCount);
		     		     
	         String resultCode = executionContext.get("resultCode").toString();
			 String resultMsg = executionContext.get("resultMsg").toString();
			 String totalCount = executionContext.get("totalCount").toString();
			 	     
		     int totalCountNum = Integer.parseInt(totalCount);
		     
		     executionContext.put("resultCode", resultCode);
		     executionContext.put("resultMsg",resultMsg);
		     executionContext.put("totalCount", totalCountNum);
		     
			return RepeatStatus.FINISHED;
		});
	}
	
	@Bean
	public Tasklet test3Tasklet() {
		return ((contribution, chunkContext) -> {

			System.out.println("Step3. API 호출 결과를 테이블에 저장");

			 StepContext context = chunkContext.getStepContext();
		     ExecutionContext executionContext = context.getStepExecution().getJobExecution().getExecutionContext();
		     String weatherContext = (String) executionContext.get("weatherData");

		     ObjectMapper mapper = new ObjectMapper();
		     mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		        
		     Root root = mapper.readValue(weatherContext, Root.class);
		      
		     System.out.println("결과코드 : " + root.response.header.resultCode);
		     System.out.println("결과메세지 : " + root.response.header.resultMsg);
		     System.out.println("totalcount : " + root.response.body.totalCount);
		     		     
				String resultCode = executionContext.get("resultCode").toString();
				String resultMsg = executionContext.get("resultMsg").toString();
				String totalCount = executionContext.get("totalCount").toString();
				
				int totalCountNum = Integer.parseInt(totalCount);

										
				ApiResult apiResult = ApiResult.builder()
						.apiCallTime(LocalDateTime.now())
						.resultCode(resultCode)
						.resultMsg(resultMsg)
						.totalCount(totalCountNum)
						.build();
				
				apiResultRepository.save(apiResult);
				
			return RepeatStatus.FINISHED;
		});
	}
}
