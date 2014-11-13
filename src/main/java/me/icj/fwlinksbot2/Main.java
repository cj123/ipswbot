package me.icj.fwlinksbot2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.net.URL;
import java.util.Properties;

public class Main
{
	// servers
	private static Server[] servers;

	// bots
	private static FwlinksBot[] bots;

	// the socket server for receiving the messages
	private static ReleaseMessage releaseMessage;

	private static final String CONFIG_LOCATION = "/resources/config.properties";

	public static void main(String[] args) throws Exception 
	{

		readProperties();

		// connect to each server
		for(int botIndex = 0; botIndex < servers.length; botIndex++)
		{
			// create a new instance of FwlinksBot with the server
			FwlinksBot bot = new FwlinksBot(servers[botIndex], logLocation);
			bots[botIndex] = bot;
			try 
			{
				bot.printLog("Connecting to server: " + servers[botIndex].toString());
				bot.setVerbose(verbose);
				bot.connect(servers[botIndex].getAddress(), servers[botIndex].getPort());

				for(String channel : servers[botIndex].getChannels())
					bot.joinChannel(channel);
				
			} // try
			catch (Exception exception) 
			{
				System.out.println(exception);
			} // catch
		} // for

		// start the release message server if enabled in config
		if(startReleaseMessageServer)
		{
			releaseMessage = new ReleaseMessage(bots);
			releaseMessage.start();
		} // if
	} // main

	// do we start a release message server?
	private static boolean startReleaseMessageServer = false;

	private static boolean verbose = false;

	// log location
	private static String logLocation = "";

	public static void readProperties()
	{
		Properties prop = new Properties();

		InputStream input = null;

		try
		{
			input = new FileInputStream("config.properties");

			prop.load(input);
			startReleaseMessageServer = Boolean.parseBoolean(prop.getProperty("startReleaseMessageServer"));
			verbose = Boolean.parseBoolean(prop.getProperty("verbose"));
			logLocation = prop.getProperty("logLocation");

			String[] parsedServers = prop.getProperty("servers").split(",");

			// create the arrays
			servers = new Server[parsedServers.length];
			bots = new FwlinksBot[servers.length];

			for(int serverIndex = 0; serverIndex < servers.length; serverIndex++)
			{
				String address = prop.getProperty(parsedServers[serverIndex] + ".address");
				int port = Integer.parseInt(prop.getProperty(parsedServers[serverIndex] + ".port"));
				String[] channels = prop.getProperty(parsedServers[serverIndex] + ".channels").split(",");
				servers[serverIndex] = new Server(address, port, channels);
			} // for

		} // try
		catch (IOException exception)
		{
			// most likely unable to find config.properties
			// but it should be there
			System.out.println("IOException caught. Most likely config.properties does not exist");
			System.err.println(exception);
		} // catch
		finally
		{
			try
			{
				if(input != null)
					input.close();
			} // try
			catch (IOException exception)
			{
				System.out.println("IOException caught. Message: " + exception.getMessage());
				System.err.println(exception);				
			} // catch
		} // finally
	} // readProperties

} // FwlinksBot
