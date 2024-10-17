package com.example.demo.openapi.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "tbl_api_result")
@EntityListeners(AuditingEntityListener.class)
public class ApiResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int no;
	
	@CreatedDate
	@Column(nullable = true)
	LocalDateTime apiCallTime;
	
	@Column(length = 10, nullable = true)
	String resultCode;
	
	@Column(length = 20, nullable = true)
	String resultMsg;
	
	@Column
	int totalCount;
	
}
