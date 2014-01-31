public class FwlinksBotTest
{
	// servers
	private static Server[] servers = {
		new Server("irc.chronic-dev.org", 6667, new String[] {"#cj-case", "#fwlinksbot"}),
	};

	// bots
	private static FwlinksBot[] bots = new FwlinksBot[servers.length];

	// the socket server for receiving the messages
	private static ReleaseMessage releaseMessage = new ReleaseMessage(bots);

	public static void main(String[] args)
	{
		// connect to each server
		for(int botIndex = 0; botIndex < servers.length; botIndex++)
		{
			FwlinksBot bot = new FwlinksBot();
			bots[botIndex] = bot;
			try 
			{
				// configuration
				bot.setVerbose(false);
				bot.setAutoNickChange(true);
				bot.printLog("Connecting to server: " + servers[botIndex].getAddress());
				bot.connect(servers[botIndex].getAddress(), servers[botIndex].getPort());

				for(String channel : servers[botIndex].getChannels())
					bot.joinChannel(channel);
				
			} // try
			catch (Exception exception) 
			{
				System.out.println(exception);
			} // catch
		} // for

		releaseMessage.start();
	}
}