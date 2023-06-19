package com.example.AzureFTP.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.example.AzureFTP.AzureDataloadConfig;
import com.example.AzureFTP.utils.DataloadUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("azureCommunicationService")
public class DefaultAzureCommunicationService implements RexelDataloadService 
{

	@Resource(name = "blobServiceClientSAS")
	BlobServiceClient blobServiceClientSAS;

	@Resource(name = "blobContainerClient")
	BlobContainerClient blobContainerClient;
	
	@Autowired
	AzureDataloadConfig azureDataloadConfig;

	/**
	 * Download specific file at remote location
	 */
	@Override
	public boolean getFile(String remoteSourceDirectory, String localDestinationDirectory, String filename) 
	{
		try
		{
			DataloadUtils.createLocalDirectoryIfNotExist(localDestinationDirectory);
				
			return downloadFileFromAzure(blobContainerClient, remoteSourceDirectory, localDestinationDirectory,filename);
		}
		catch (Exception e) 
		{
			log.error(AZURE_DATALOAD_LOG + e.getMessage());
			
			return false;
		}
		
	}

	/**
	 * Download specific list of files at remote location
	 */
	@Override
	public List<String> getFiles(String remoteSourceDirectory, String localDestinationDirectory,List<String> filenames) 
	{
		List<String> downloadedFileNames = new ArrayList();
		
		try
		{
			DataloadUtils.createLocalDirectoryIfNotExist(localDestinationDirectory);
			
			List<BlobItem> blobList = getListOfBlobFromAzure(blobContainerClient, remoteSourceDirectory);
				
			for (final BlobItem blob : blobList)
			{
				for (final String filename : filenames)
				{
					
					String blobName = blob.getName().replace(remoteSourceDirectory, "");
					
					if (!blob.isPrefix() && blobName.equalsIgnoreCase(filename))
					{
						boolean isDownloaded = downloadFileFromAzure(blobContainerClient, remoteSourceDirectory, localDestinationDirectory,filename);
						
						if(isDownloaded)	
								downloadedFileNames.add(blobName);
							
					}
				}
			}
			
		}
		catch (Exception e) 
		{
			log.error(AZURE_DATALOAD_LOG + e.getMessage());
		}
		return downloadedFileNames;
	}

	/**
	 * Download specific list of files at remote location matching given regularExpression
	 */
	@Override
	public List<String> getFilesRegularExpression(String remoteSourceDirectory, String localDestinationDirectory,String regularExpression) 
	{
		List<String> downloadedFileNames = new ArrayList();
		
		final String newRegularExpression = regularExpression == null ? "*" : regularExpression;
		
		try
		{
			DataloadUtils.createLocalDirectoryIfNotExist(localDestinationDirectory);
			
			List<BlobItem> blobList = getListOfBlobFromAzure(blobContainerClient, remoteSourceDirectory);
				
			for (final BlobItem blob : blobList)
			{
				String blobName = blob.getName().replace(remoteSourceDirectory, "");
				
				if (!blob.isPrefix() && blobName.matches(newRegularExpression))
				{
					boolean isDownloaded = downloadFileFromAzure(blobContainerClient, remoteSourceDirectory, localDestinationDirectory,blobName);
					
					if(isDownloaded)	
							downloadedFileNames.add(blobName);
						
				}
			}
			
		}
		catch (Exception e) 
		{
			log.error(AZURE_DATALOAD_LOG + e.getMessage());
		}
		return downloadedFileNames;
	}

	/**
	 * Download specific list of files at remote location which NOT matching given regularExpression
	 */
	@Override
	public List<String> getDirectory(String remoteSourceDirectory, String localDestinationDirectory,
			String excludeRegularExpression) 
	{
		List<String> downloadedFileNames = new ArrayList();
		
		try
		{
			DataloadUtils.createLocalDirectoryIfNotExist(localDestinationDirectory);
			
			List<BlobItem> blobList = getListOfBlobFromAzure(blobContainerClient, remoteSourceDirectory);
				
			for (final BlobItem blob : blobList)
			{
				String blobName = blob.getName().replace(remoteSourceDirectory, "");
				
				if (!blob.isPrefix() && !blobName.matches(excludeRegularExpression))
				{
					boolean isDownloaded = downloadFileFromAzure(blobContainerClient, remoteSourceDirectory, localDestinationDirectory,blobName);
					
					if(isDownloaded)	
							downloadedFileNames.add(blobName);
						
				}
			}
			
		}
		catch (Exception e) 
		{
			log.error(AZURE_DATALOAD_LOG + e.getMessage());
		}
		return downloadedFileNames;
	}

	/**
	 * get list of file names at remote location which matching given regularExpression
	 */
	@Override
	public List<String> getFilesList(String remoteSourceDirectory, String regularExpression) 
	{
		List<String> fileNamesAtRemoteLocation = new ArrayList();
		
		final String newRegularExpression = regularExpression == null ? "*" : regularExpression;
		
		try
		{
			List<BlobItem> blobList = getListOfBlobFromAzure(blobContainerClient, remoteSourceDirectory);
				
			for (final BlobItem blob : blobList)
			{
				String blobName = blob.getName().replace(remoteSourceDirectory, "");
				
				System.out.println("blobName ----> "+blobName);
				
				if (!blob.isPrefix() && blobName.matches(newRegularExpression))
				{
					fileNamesAtRemoteLocation.add(blobName);
				}
			}
			
		}
		catch (Exception e) 
		{
			log.error(AZURE_DATALOAD_LOG + e.getMessage());
		}
		return fileNamesAtRemoteLocation;
	}

	/**
	 * get all list of file names at remote location 
	 */
	@Override
	public List<String> getSubDirectoriesList(String remoteSourceDirectory) 
	{
		List<String> fileNamesAtRemoteLocation = new ArrayList();
		
		try
		{
			List<BlobItem> blobList = getListOfBlobFromAzure(blobContainerClient, remoteSourceDirectory);
				
			for (final BlobItem blob : blobList)
			{
				String blobName = blob.getName().replace(remoteSourceDirectory, "");
				
				System.out.println("blobName ----> "+blobName);
				
				if (!blob.isPrefix())
				{
					fileNamesAtRemoteLocation.add(blobName);
				}
			}
			
		}
		catch (Exception e) 
		{
			log.error(AZURE_DATALOAD_LOG + e.getMessage());
		}
		return fileNamesAtRemoteLocation;
	}

	/**
	 * move specific file from one location to another on azure itself
	 */
	@Override
	public boolean moveFile(String remoteSourceDirectory, String remoteDestinationDirectory, String filename,
			String filenameSuffix) 
	{
		try
		{
			String newFilename = filename;
			if (filenameSuffix != null && !filenameSuffix.isEmpty())
			{
				final String extension = FilenameUtils.getExtension(filename);
				newFilename = FilenameUtils.removeExtension(filename) + filenameSuffix + (extension.isEmpty() ? "" : "." + extension);
			}
			
			return moveFileOnAzure(blobContainerClient, remoteSourceDirectory+filename, remoteDestinationDirectory+newFilename, azureDataloadConfig.getSasToken());
		}
		catch (Exception e) 
		{
			log.error("AZURE DATALOAD | "+e.getMessage());
		}
		return false;
	}

	/**
	 * move specific list of files from one location to another on azure itself
	 */
	@Override
	public List<String> moveFiles(String remoteSourceDirectory, String remoteDestinationDirectory,
			List<String> filenames, String filenameSuffix) 
	{
		List<String> movedFilesName = new ArrayList();
		
		try
		{
			for (final String filename : filenames)
			{
				boolean isMoved = moveFile(remoteSourceDirectory, remoteDestinationDirectory, filename, filenameSuffix);
				
				if(isMoved)
					movedFilesName.add(filename);
			}
		}
		catch (Exception e) 
		{
			log.error("AZURE DATALOAD | "+e.getMessage());
		}
		return movedFilesName;
	}

	/**
	 * upload specific file from local to azure
	 */
	@Override
	public boolean sendFile(String localSourceDirectory, String remoteDestinationDirectory, String filename,
			String filenameSuffix) 
	{
		try
		{
			String newFilename = filename;
			if (filenameSuffix != null && !filenameSuffix.isEmpty())
			{
				final String extension = FilenameUtils.getExtension(filename);
				newFilename = FilenameUtils.removeExtension(filename) + filenameSuffix + (extension.isEmpty() ? "" : "." + extension);
			}
			
			return uploadFileOnAzure(blobContainerClient, localSourceDirectory+filename, remoteDestinationDirectory+newFilename);
		}
		catch (Exception e) 
		{
			log.error("AZURE DATALOAD | "+e.getMessage());
		}
		
		return false;
	}

	/**
	 * upload specific list of files from local to azure
	 */
	@Override
	public List<String> sendFiles(String localSourceDirectory, String remoteDestinationDirectory,
			List<String> filenames, String filenameSuffix) 
	{
		List<String> uploadedFilesName = new ArrayList();
		
		try
		{
			for (final String filename : filenames)
			{
				boolean isUploaded = sendFile(localSourceDirectory, remoteDestinationDirectory, filename, filenameSuffix);
				
				if(isUploaded)
					uploadedFilesName.add(filename);
			}
		}
		catch (Exception e) 
		{
			log.error("AZURE DATALOAD | "+e.getMessage());
		}
		
		return uploadedFilesName;
	}

	/**
	 * upload specific file from local to azure
	 * this is different to sendFile as in this case we get absolute path of the source file
	 * where as in case of sendFile we get source file location and source file name separately
	 */
	@Override
	public boolean copyUnresolvedFile(String localSourceFile, String remoteDestinationDirectory,
			String filenameSuffix) 
	{
		try
		{
			String destFileName =  "unresolved_lines_" + filenameSuffix + ".csv";
			
			return uploadFileOnAzure(blobContainerClient, localSourceFile, remoteDestinationDirectory+destFileName);
		}
		catch (Exception e) 
		{
			log.error("AZURE DATALOAD | "+e.getMessage());
		}
		return false;
	}
}
