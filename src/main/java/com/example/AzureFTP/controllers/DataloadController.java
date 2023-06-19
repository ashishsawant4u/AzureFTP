package com.example.AzureFTP.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.AzureFTP.AzureDataloadConfig;
import com.example.AzureFTP.HybrisDataloadConfig;
import com.example.AzureFTP.services.RexelDataloadService;

@RestController
@RequestMapping("/dataload")
public class DataloadController 
{
	
	@Autowired
	AzureDataloadConfig azureDataloadConfig;
	
	@Autowired
	HybrisDataloadConfig hybrisDataloadConfig;
	
	@Resource(name = "azureCommunicationService")
	RexelDataloadService dataloadService;
	
	@GetMapping("/getFile")
	public String getFile()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		String localDestinationDirectory = hybrisDataloadConfig.getDestinationLocation();
		String filename = "ADENHYBPPRI230613";
		
		boolean result = dataloadService.getFile(remoteSourceDirectory,localDestinationDirectory,filename);
		
		return "getFile "+result;
	}
	
	@GetMapping("/getFiles")
	public List<String> getFiles()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		String localDestinationDirectory = hybrisDataloadConfig.getDestinationLocation();
		List<String> filenames = List.of("ADENHYBPPRI230613");
		
		return dataloadService.getFiles(remoteSourceDirectory, localDestinationDirectory, filenames);
	}
	
	@GetMapping("/getFilesRegularExpression")
	public List<String> getFilesRegularExpression()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		String localDestinationDirectory = hybrisDataloadConfig.getDestinationLocation();
		String regularExpression = ".*.HYBPPRI.*.";
		
		return dataloadService.getFilesRegularExpression(remoteSourceDirectory, localDestinationDirectory,regularExpression);
	}
	
	@GetMapping("/getDirectory")
	public List<String> getDirectory()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		String localDestinationDirectory = hybrisDataloadConfig.getDestinationLocation();
		String regularExpression = ".*.HYBPPRI.*.";
		
		return dataloadService.getDirectory(remoteSourceDirectory, localDestinationDirectory,regularExpression);
	}
	
	@GetMapping("/getFilesList")
	public List<String> getFilesList()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		String regularExpression = ".*.HYBPPRI.*.";
		
		return dataloadService.getFilesList(remoteSourceDirectory,regularExpression);
	}
	
	@GetMapping("/getSubDirectoriesList")
	public List<String> getSubDirectoriesList()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		
		return dataloadService.getSubDirectoriesList(remoteSourceDirectory);
	}
	
	@GetMapping("/moveFile")
	public String moveFile()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		String remoteDestinationDirectory = azureDataloadConfig.getDestinationLocation()+"backup/pass/";
		String filename = "ADENHYBPPRI230613";
		String filenameSuffix = "_OTT";
		
		boolean result = dataloadService.moveFile(remoteSourceDirectory, remoteDestinationDirectory, filename, filenameSuffix);
		
		return "moveFile "+result;
	}
	
	@GetMapping("/moveFiles")
	public List<String> moveFiles()
	{
		String remoteSourceDirectory = azureDataloadConfig.getSourceLocation();
		String remoteDestinationDirectory = azureDataloadConfig.getDestinationLocation()+"backup/pass/";
		List<String> filenames = List.of("ADENHYBPPRI230613","DDENHYBPPRI230613");
		String filenameSuffix = "_OTTX";
		
		return dataloadService.moveFiles(remoteSourceDirectory, remoteDestinationDirectory, filenames, filenameSuffix);
	}
	
	
	@GetMapping("/sendFile")
	public String sendFile()
	{
		String localSourceDirectory = hybrisDataloadConfig.getSourceLocation();
		String remoteDestinationDirectory = azureDataloadConfig.getDestinationLocation()+"upload/";
		String filename = "ADENHYBPPRI230613";
		String filenameSuffix = "_IPL";
		
		boolean result = dataloadService.sendFile(localSourceDirectory, remoteDestinationDirectory, filename, filenameSuffix);
		
		return "sendFile "+result;
	}
	
	@GetMapping("/sendFiles")
	public List<String> sendFiles()
	{
		String localSourceDirectory = hybrisDataloadConfig.getSourceLocation();
		String remoteDestinationDirectory = azureDataloadConfig.getDestinationLocation()+"upload/";
		List<String> filenames = List.of("ADENHYBPPRI230613","DDENHYBPPRI230613");
		String filenameSuffix = "_IND";
		
		return dataloadService.sendFiles(localSourceDirectory, remoteDestinationDirectory, filenames, filenameSuffix);
	}
	
	
	@GetMapping("/copyUnresolvedFile")
	public String copyUnresolvedFile()
	{
		String localSourceFile = hybrisDataloadConfig.getSourceLocation()+"failed_lines.csv";
		String remoteDestinationDirectory = azureDataloadConfig.getDestinationLocation()+"backup/fail/";
		String filenameSuffix = "_ERR";
		
		boolean result = dataloadService.copyUnresolvedFile(localSourceFile, remoteDestinationDirectory, filenameSuffix);
		
		return "sendFile "+result;
	}
}
