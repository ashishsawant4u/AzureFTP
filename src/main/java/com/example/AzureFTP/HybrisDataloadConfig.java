package com.example.AzureFTP;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "hybris") 
public class HybrisDataloadConfig 
{
			
	private String sourceLocation;
	
	private String destinationLocation;
	
	private String sourceFileName;
	
	private String destinationFileName;
	
}
