package me.icj.fwlinksbot2;

import java.net.*;
import java.io.*;

public class ReleaseMessage extends Thread
{
	private static final int socketPort = 4536;
	private final FwlinksBot[] bots;

	@Override
	public void run()
	{
		bots[0].printLog("starting socket server on port " + socketPort);

		// to avoid try-with-resources which isn't supported
		// before 1.7
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try
		{
			// configure sockets
			serverSocket = new ServerSocket(socketPort, 0, InetAddress.getByName(null));
			clientSocket = serverSocket.accept(); 
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while(true)
			{
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					// new message
					bots[0].printLog("new session message: " + inputLine);

					for (FwlinksBot bot : bots)
					{
						// send to each channel
						for(String channel : bot.getChannels())
						{
								bot.sendSplitMessage(channel, "[Update] " + inputLine);
						} // for
					} // for
				} // while
			} // while
		} // try
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		} // catch
		finally
		{
			try {
				if(serverSocket != null) serverSocket.close();
				if(clientSocket != null) clientSocket.close();
				if(out != null) out.close();
				if(in != null) in.close();				
			}
			catch (IOException e)
			{
				System.out.println(e.getMessage());
			}
		} // finally

	} // run

	public ReleaseMessage(FwlinksBot[] requiredBots)
	{
		bots = requiredBots;
	} // ReleaseMessage

} // ReleaseMessage