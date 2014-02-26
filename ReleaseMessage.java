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

	} // run

	public ReleaseMessage(FwlinksBot[] requiredBots)
	{
		bots = requiredBots;
	} // ReleaseMessage

} // ReleaseMessage