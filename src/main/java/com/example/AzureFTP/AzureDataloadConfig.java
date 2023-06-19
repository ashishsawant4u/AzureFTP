package com.example.AzureFTP;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "azure") 
public class AzureDataloadConfig 
{
		
	private String endpointUrl;
	
	private String sasToken;
	
	private String containerName;
	
	private String sourceLocation;
	
	private String destinationLocation;
	
	private String sourceFileName;
	
	private String destinationFileName;
	
}
