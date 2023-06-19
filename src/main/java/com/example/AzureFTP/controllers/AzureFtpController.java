package com.example.AzureFTP.controllers;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.example.AzureFTP.AzureDataloadConfig;
import com.example.AzureFTP.HybrisDataloadConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/azure")
public class AzureFtpController 
{
	
	@Resource(name = "blobServiceClientSAS")
	BlobServiceClient blobServiceClientSAS;
	
	
	@Autowired
	AzureDataloadConfig azureDataloadConfig;
	
	@Autowired
	HybrisDataloadConfig hybrisDataloadConfig;
	
	
	Function<BlobProperties, String> BLOB_LOG = (p) -> "STATUS "+p.getCopyStatus()+" SIZE "+p.getBlobSize();
	
	@GetMapping("/blobUpload")
	public String blobUpload()
	{
		
		
		BlobContainerClient blobContainerClient = blobServiceClientSAS.getBlobContainerClient(azureDataloadConfig.getContainerName());
		
		
		String SOURCE = hybrisDataloadConfig.getSourceLocation()+hybrisDataloadConfig.getSourceFileName();
		String DESTINATION = azureDataloadConfig.getDestinationLocation()+azureDataloadConfig.getDestinationFileName();
		
		
		log.info("UPLOAD SOURCE ==> "+SOURCE);
		log.info("UPLOAD DESTINATION ==> "+DESTINATION);
		
		 BlobClient blobClient = blobContainerClient.getBlobClient(DESTINATION);

		    try 
		    {
		        blobClient.uploadFromFile(SOURCE);
		    } 
		    catch (UncheckedIOException ex) 
		    {
		        log.error("Failed to upload from file:", ex.getMessage());
		    }
		
		
		return "Azure Blob Upload";
	}
	
	@GetMapping("/blobDownload")
	public String blobDownload()
	{
		
		BlobContainerClient blobContainerClient = blobServiceClientSAS.getBlobContainerClient(azureDataloadConfig.getContainerName());
		
		String SOURCE = azureDataloadConfig.getSourceLocation()+azureDataloadConfig.getSourceFileName();
		String DESTINATION = hybrisDataloadConfig.getDestinationLocation()+hybrisDataloadConfig.getDestinationFileName();
		
		
		log.info("DOWNLOAD SOURCE ==> "+SOURCE);
		log.info("DOWNLOAD DESTINATION ==> "+DESTINATION);
		
		 BlobClient blobClient = blobContainerClient.getBlobClient(SOURCE);

		    try 
		    {
		    		blobClient.downloadToFile(DESTINATION);
		    } 
		    catch (UncheckedIOException ex) 
		    {
		        log.error("Failed to download file:", ex.getMessage());
		    }
		
		return "Azure Blob Downloaded";
	}
	
	@GetMapping("/blobMove")
	public String blobMove()
	{
		BlobContainerClient blobContainerClient = blobServiceClientSAS.getBlobContainerClient(azureDataloadConfig.getContainerName());
		
		
		String SOURCE = azureDataloadConfig.getSourceLocation()+azureDataloadConfig.getSourceFileName();
		String DESTINATION = azureDataloadConfig.getSourceLocation()+"backup/"+azureDataloadConfig.getSourceFileName();
		
		log.info("MOVE SOURCE ==> "+SOURCE);
		log.info("MOVE DESTINATION ==> "+DESTINATION);
		
		BlobClient sourceBlob = blobContainerClient.getBlobClient(SOURCE);
		 
		BlobClient destinationBlob = blobContainerClient.getBlobClient(DESTINATION);
		
		try 
	    {
			destinationBlob.beginCopy(sourceBlob.getBlobUrl()+"?"+azureDataloadConfig.getSasToken(), null);
			
			sourceBlob.deleteIfExists();
		} 
	    catch (UncheckedIOException ex) 
	    {
	        log.error("Failed to move file:", ex.getMessage());
	    }
		 
		 return "Azure Blob Moved " + BLOB_LOG.apply(destinationBlob.getProperties());
	}
	
	@GetMapping(value="/blobList",produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> blobList()
	{
		List<String> allFiles = new ArrayList();
		
		BlobContainerClient blobContainerClient = blobServiceClientSAS.getBlobContainerClient(azureDataloadConfig.getContainerName());
		
		
		String SOURCE = azureDataloadConfig.getSourceLocation();
		
		log.info("LIST SOURCE ==> "+SOURCE);
		
		 String delimiter = "/";
		 ListBlobsOptions options = new ListBlobsOptions()
		            .setPrefix(SOURCE);
		
		try 
	    {
			Iterable<BlobItem> blobs = blobContainerClient.listBlobsByHierarchy(SOURCE);
			//Iterable<BlobItem> blobs = blobContainerClient.listBlobsByHierarchy(delimiter, options, null);
			
			blobs.forEach(blob -> {
				
				if(!blob.isPrefix())
				{
					allFiles.add(blob.getName());
				}
				else
				{
					System.out.println("directory found "+blob.getName());
				}
				
			});
			
			System.out.println("all files "+allFiles);
		
		} 
	    catch (UncheckedIOException ex) 
	    {
	        log.error("Failed to list file:", ex.getMessage());
	    }
		 
		 return allFiles;
	}
	
	
	
	
}
