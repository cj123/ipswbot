import org.jibble.pircbot.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FwlinksBot extends PircBot
{
	private Server server;

	public FwlinksBot(Server requiredServer)
	{
		super();
		server = requiredServer;

		// configuration
		setName("fwlinksbot");
		setLogin("fwlinks");
		setVersion("fwlinksbot");
		setAutoNickChange(true);
	}

	public void errorMessage(String channel, String sender, String message)
	{
		printLog("error: " + sender + ": " + message);
		sendMessage(channel, sender + ": " + message);
	} // errorMessage

	// the max amount of chars before splitting a message into parts
	private static final int MESSAGE_SPLIT_LENGTH = 300;

	public void sendSplitMessage(String channel, String message)
	{

		printLog("Sending message '" + message + "' to " + channel);

		if(message.length() >= 1000)
		{
			// this request obviously is not valid. Kill it, kill it quick
			printLog("response was too long. not sending to channel...");
			return;
		} // if
		else if(message.length() > MESSAGE_SPLIT_LENGTH)
		{

			String[] splitInput = message.split("(?<=\\G.{" + MESSAGE_SPLIT_LENGTH + "})");

			sendMessage(channel, splitInput[0] + "...");

			for(int index = 1; index < splitInput.length - 1; index ++)
				sendMessage(channel, splitInput[index] + "...");

			sendMessage(channel, splitInput[splitInput.length - 1]);

		} // else if
		else
		{
			sendMessage(channel, message);
		} // else
	} // sendSplitMessage

	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)
	{
		printLog("Invite to channel " + channel + " received. Joining...");
		joinChannel(channel);
		server.addChannel(channel);
		// sendMessage(channel, sourceNick + ": thanks for the invite!");
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
				sendMessage(channel, sender + ": view my commands here: http://api.ios.icj.me/docs/fwlinksbot"
												 + " i'm open source! more info here: https://github.com/JustaPenguin/fwlinksbot2");
			break;

			// commands
			case "!fw": case "!firmware": api.firmware(args); break;
			case "!redsn0w": case "!rs": api.redsn0w(args); break;
			case "!itunes": case "!it": api.iTunes(args); break;
			case "!tss": api.tss(args); break;
			case "!shsh": api.shsh(args); break;
			case "!pwnagetool": case "!pt": api.pwnagetool(args); break;
			case "!fwinfo": sendMessage(channel, server.toString()); break;

		} // switch

		return;
	} // onMessage

	public void printLog(String message)
	{
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss] ");
		System.out.println(dateFormat.format(date) + message);
	}

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
				printLog("Error: could not reconnect to " + getServer() + "\n" + e.getMessage());
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
			printLog("Reconnected to server: " + getServer());

			if(server.getAddress().equals(getServer()))
			{
				// rejoin channels
				for(String channel : server.getChannels())
					joinChannel(channel);
			} // if
			else
			{
				printLog("We somehow connected to a different server?");
			} // else
		} // if
		else
		{
			printLog("Unable to reconnect to server: " + getServer() + " - disabling.");
		} // else

	} // onDisconnect

} // FwlinksBot