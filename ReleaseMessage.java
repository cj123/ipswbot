import java.net.*;
import java.io.*;

public class ReleaseMessage extends Thread
{
	private static final int socketPort = 4536;
	private final FwlinksBot[] bots;

	private static final int MESSAGE_SPLIT_LENGTH = 300;

	@Override
	public void run()
	{
		bots[0].printLog("starting socket server on port " + socketPort);

		try
		(
			// configure sockets
			ServerSocket serverSocket = new ServerSocket(socketPort, 0, InetAddress.getByName(null));
			Socket clientSocket = serverSocket.accept(); 
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		) {
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
							if(inputLine.length() > MESSAGE_SPLIT_LENGTH)
							{

								String[] splitInput = inputLine.split("(?<=\\G.{" + MESSAGE_SPLIT_LENGTH + "})");

								bot.sendMessage(channel, "[Update] " + splitInput[0] + "...");

								for(int index = 1; index < splitInput.length - 1; index ++)
									bot.sendMessage(channel, splitInput[index] + "...");

								bot.sendMessage(channel, splitInput[splitInput.length - 1]);

							} // if
							else
							{
								bot.sendMessage(channel, "[Update] " + inputLine);
							} // else
						} // for
					} // for
				} // while
			} // while
		} // try
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		} // catch

	} // run

	public ReleaseMessage(FwlinksBot[] requiredBots)
	{
		bots = requiredBots;
	} // ReleaseMessage

} // ReleaseMessage