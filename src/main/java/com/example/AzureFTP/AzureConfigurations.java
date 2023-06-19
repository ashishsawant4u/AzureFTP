package com.example.AzureFTP;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Configuration
public class AzureConfigurations 
{
	@Autowired
	AzureDataloadConfig azureDataloadConfig;
	
	
	@Bean
	public BlobServiceClient blobServiceClientSAS() 
	{
		
		System.out.println("COONECTING ==> "+azureDataloadConfig.getEndpointUrl());
		
	    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
	            .endpoint(azureDataloadConfig.getEndpointUrl())
	            .sasToken(azureDataloadConfig.getSasToken())
	            .buildClient();

	    return blobServiceClient;
	}
	
	@Bean
	public BlobContainerClient blobContainerClient()
	{
		return blobServiceClientSAS().getBlobContainerClient(azureDataloadConfig.getContainerName());
	}
}

