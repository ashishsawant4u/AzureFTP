package com.example.AzureFTP.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataloadUtils 
{
	
	public static final Logger log = LoggerFactory.getLogger(DataloadUtils.class);
	
	public static void createLocalDirectoryIfNotExist(String localDestinationDirectory)
	{
		try 
		{
			final File localDir = new File(localDestinationDirectory);

			if (!localDir.exists())
			{
				localDir.mkdirs();
			}
		} 
		catch (Exception e) 
		{
			log.error("AZURE DATALOAD | " + e.getMessage());
		}
	}
}
