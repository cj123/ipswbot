import org.jibble.pircbot.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FwlinksBot extends PircBot
{
	// servers
	private static Server[] servers = {
		new Server("irc.chronic-dev.org", 6667, new String[] {"#cj-case", "#iH8sn0w", "#fwlinksbot"}),
		new Server("iphun.osx86.hu", 6667, new String[] {"#ios"}),
		new Server("irc.saurik.com", 6667, new String[] {"#teambacon"}),
		new Server("irc.freenode.net", 6667, new String[] {"#jailbreakqa", "#openjailbreak", "#testfwlinks"})
	};

	// bots
	private static FwlinksBot[] bots = new FwlinksBot[servers.length];

	// the socket server for receiving the messages
	private static ReleaseMessage releaseMessage = new ReleaseMessage(bots);

	public FwlinksBot()
	{
		super();
		this.setName("fwlinksbot");
		this.setLogin("fwlinks");
		this.setVersion("fwlinksbot");
	} // FwlinksBot

	public static void printLog(String message)
	{
        Date date = new Date();
        DateFormat dateFormat = 
            new SimpleDateFormat("[HH:mm:ss] ");
        System.out.println(dateFormat.format(date) + message);
	}

	public static void main(String[] args) throws Exception 
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
				printLog("Connecting to server: " + servers[botIndex].getAddress());
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
	} // main

	public void errorMessage(String channel, String sender, String message)
	{
		printLog("error: " + sender + ": " + message);
		sendMessage(channel, sender + ": " + message);
	} // errorMessage

	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)
	{
		printLog("Invite to channel " + channel + " received. Joining...");
		joinChannel(channel);
		sendMessage(channel, sourceNick + ": thanks for the invite!");
	} // onInvite

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		// parse args into an array
		String[] args = message.split(" ");

		APIRequest api = new APIRequest(this, channel, sender);

		switch(args[0].toLowerCase())
		{
			case "!help":
				this.sendMessage(channel, sender + ": view my commands here: http://api.ios.icj.me/docs/fwlinksbot"
				                 + " i'm open source! more info here: https://gitlab.icj.me/cj/fwlinksbot2");
			break;

			// commands
			case "!fw": case "!firmware": api.firmware(args); break;
			case "!redsn0w": case "!rs": api.redsn0w(args); break;
			case "!itunes": case "!it": api.iTunes(args); break;
			case "!tss": api.tss(args); break;
			case "!shsh": api.shsh(args); break;
			case "!pwnagetool": case "!pt": api.pwnagetool(args); break;

		} // switch

		return;
	} // onMessage

	@Override
	protected void onDisconnect()
	{
		int reconnectAttempts = 0;

		System.out.println("Connection to " + getServer() + " dropped, "
		                   + "trying to reconnect...");

		// try to reconnect
		while(!isConnected() && reconnectAttempts < 15)
		{
			try
			{
				// reconnect
				reconnectAttempts++;
				reconnect();
			} // try
			catch (Exception e)
			{
				// failed to reconnect
				System.out.println("Error: could not reconnect to " + getServer() + "\n"
				                   + e.getMessage());
				try
				{
				    Thread.sleep(4000);
				} // try
				catch(InterruptedException exception) 
				{
				    Thread.currentThread().interrupt();
				} // catch
			} // catch
		} // while

		// check if it has reconnected
		if(isConnected())
		{
			System.out.println("Reconnected to server: " + getServer());
		} // if
		else
		{
			System.out.println("Unable to reconnect to server: " + getServer()
			                   + " - disabling.");
		} // else

	} // onDisconnect


} // FwlinksBot
