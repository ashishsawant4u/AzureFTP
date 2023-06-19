package com.example.AzureFTP.services;

import java.util.List;

public interface RexelDataloadService extends AzureTransfer
{

	/**
	 * Returns a boolean result in order to indicate that the defined file has been correctly retrieved from ftp server
	 * or not.
	 */
	boolean getFile(String remoteSourceDirectory, String localDestinationDirectory, String filename);

	/**
	 * Returns the list of the names of files that have been correctly retrieved from FTP.
	 */
	List<String> getFiles(String remoteSourceDirectory, String localDestinationDirectory,
			List<String> filenames);

	/**
	 * Returns the list of the names of files that have been correctly retrieved from FTP.
	 */
	List<String> getFilesRegularExpression(String remoteSourceDirectory,
			String localDestinationDirectory, String regularExpression);

	/**
	 * Returns the list of the names of files that have been correctly retrieved from FTP.
	 */
	List<String> getDirectory(String remoteSourceDirectory, String localDestinationDirectory,
			String excludeRegularExpression);

	/**
	 * Returns the list of files that are existing in the remoteSourceDirectory.
	 */
	List<String> getFilesList(String remoteSourceDirectory, String regularExpression);

	/**
	 * Returns the list of subdirectories that are existing in the remoteSourceDirectory.
	 */
	List<String> getSubDirectoriesList(String remoteSourceDirectory);

	/**
	 * Returns a boolean result in order to indicate that the defined file has been correctly moved or not.
	 */
	boolean moveFile(String remoteSourceDirectory, String remoteDestinationDirectory, String filename,
			String filenameSuffix);

	/**
	 * Returns the list of the names of files that have been correctly moved.
	 */
	List<String> moveFiles(String remoteSourceDirectory, String remoteDestinationDirectory,
			List<String> filenames, String filenameSuffix);

	/**
	 * Returns a boolean result in order to indicate that the defined file has been correctly sent or not.
	 */
	boolean sendFile(String localSourceDirectory, String remoteDestinationDirectory, String filename,
			String filenameSuffix);

	/**
	 * Returns the list of the names of files that have been correctly sent to FTP server.
	 */
	List<String> sendFiles(String localSourceDirectory, String remoteDestinationDirectory,
			List<String> filenames, String filenameSuffix);
	

	boolean copyUnresolvedFile(String remoteSourceDirectory, String remoteDestinationDirectory, String filenameSuffix);
}
