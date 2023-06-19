package com.example.AzureFTP.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.CopyStatusType;


public interface AzureTransfer 
{
	public String AZURE_DATALOAD_LOG = "AZURE DATALOAD | ";
	
	public static final Logger log = LoggerFactory.getLogger(AzureTransfer.class);
	
	public default boolean downloadFileFromAzure(BlobContainerClient blobContainerClient,String remoteSourceDirectory, String localDestinationDirectory, String filename)
	{
		try 
	    {
			BlobClient blobClient = blobContainerClient.getBlobClient(remoteSourceDirectory + filename);
	    	blobClient.downloadToFile(localDestinationDirectory + filename);
	    	
	    	log.info(AZURE_DATALOAD_LOG + "file downloaded OK");
	    	
	    	return true;
	    } 
	    catch (Exception ex) 
	    {
	        log.error(AZURE_DATALOAD_LOG+ ex.getMessage());
	        
	        return false;
	    }
	}
	
	public default List<BlobItem> getListOfBlobFromAzure(BlobContainerClient blobContainerClient,String remoteSourceDirectory)
	{
		List<BlobItem> blobList = new ArrayList<BlobItem>();
		
		try 
	    {
			Iterable<BlobItem> blobs = blobContainerClient.listBlobsByHierarchy(remoteSourceDirectory);
			blobs.forEach(blobList::add);
			
	    	log.info(AZURE_DATALOAD_LOG + "blobs found at "+remoteSourceDirectory+" => "+blobList.size());
	    } 
	    catch (Exception ex) 
	    {
	        log.error(AZURE_DATALOAD_LOG+ ex.getMessage());
	    }
		
		return blobList;
	}
	
	public default boolean moveFileOnAzure(BlobContainerClient blobContainerClient,String remoteSource, String remoteDestination,String sasToken)
	{
		try 
	    {
			BlobClient sourceBlob = blobContainerClient.getBlobClient(remoteSource);
			 
			BlobClient destinationBlob = blobContainerClient.getBlobClient(remoteDestination);
			
			destinationBlob.beginCopy(sourceBlob.getBlobUrl()+"?"+sasToken, null);
			
			sourceBlob.deleteIfExists();
			
	    	log.info(AZURE_DATALOAD_LOG + "file move STATUS "+destinationBlob.getProperties().getCopyStatus()+" SIZE "+destinationBlob.getProperties().getBlobSize());
	    	
	    	return destinationBlob.getProperties().getCopyStatus().equals(CopyStatusType.SUCCESS);
	    } 
	    catch (Exception ex) 
	    {
	        log.error(AZURE_DATALOAD_LOG+ ex.getMessage());
	        
	        return false;
	    }
	}
	
	public default boolean uploadFileOnAzure(BlobContainerClient blobContainerClient,String localSourceFile, String remoteDestinationFile)
	{
		try 
	    {
			BlobClient blobClient = blobContainerClient.getBlobClient(remoteDestinationFile);
			
			 blobClient.uploadFromFile(localSourceFile);
			
	    	log.info(AZURE_DATALOAD_LOG + "file uploaded OK");
	    	
	    	return true;
	    } 
	    catch (Exception ex) 
	    {
	        log.error(AZURE_DATALOAD_LOG+ ex.getMessage());
	        
	        return false;
	    }
	}
}
