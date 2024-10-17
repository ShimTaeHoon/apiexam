package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.openapi.entity.ApiResult;
import com.example.demo.openapi.repository.ApiResultRepository;

@SpringBootTest
public class ApiResultRepositoryTest {

	@Autowired
	ApiResultRepository repository;
	
	@Test
	public void 데이터_추가() {
	
		ApiResult api = ApiResult.builder()
						.apiCallTime(LocalDateTime.now())
						.resultCode("01")
						.resultMsg("OK")
						.totalCount(10)
						.build();
		
		repository.save(api);
	}
	
	@Test
	public void 데이터_전체_조회() {
		
		List<ApiResult> list = repository.findAll();
		
		for(ApiResult apiresult : list) {
			System.out.println(apiresult);
		}
		
	}
	
	@Test
	public void 데이터_단건_조회() {
		
		Optional<ApiResult> optional = repository.findById(1);
		System.out.println(optional);
		
	}
	
	@Test
	public void 데이터_수정() {
		
		Optional<ApiResult> optional = repository.findById(1);
		ApiResult api = optional.get();
		api.setResultCode("02로 수정");
		repository.save(api);
		
	}
	
	@Test
	public void 데이터_삭제() {
		
		repository.deleteById(1);
		
	}
	
}
