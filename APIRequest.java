import java.net.*;
import java.io.*;

public class APIRequest
{

	private final static String APIBASE = "http://api.ios.icj.me/v2";
	private final static double VERSION = 2.0;

	private final FwlinksBot bot;
	private final String channel;
	private final String sender;

	public APIRequest(FwlinksBot requiredBot, String requiredChannel, String requiredSender)
	{
		this.bot = requiredBot;
		this.channel = requiredChannel;
		this.sender = requiredSender;
	} // APIRequest

	private static String makeURLRequest(String url)
	{
		try 
		{
			URL requestURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
			try {

				// connection properties
				connection.setRequestProperty("Connection", "close");
				connection.setRequestProperty("User-Agent", "fwlinksbot v" + VERSION);
				connection.setReadTimeout(5000);

				BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
				try
				{
					return response.readLine();
				} // try
				finally
				{
					response.close();
				} // finally

			} // try
			finally 
			{
				connection.disconnect();
			} // finally
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		} // catch

		return null;
	} // makeURLRequest

	// fill out args when less than two have been supplied
	private String[] argsPrepare(String[] args)
	{
		if (args.length == 2 || args.length == 3) {
			// assume that the request is the URL, expand the array to add 3rd index
			String argsNew[] = new String[4];

			for(int index = 0; index < args.length; index++) {
				argsNew[index] = args[index];
			}

			if(args.length == 2) {
				argsNew[2] = "latest";
				argsNew[3] = "url";
			} // if

			if(args.length == 3)
				argsNew[3] = "url";

			args = argsNew;
		} // if

		return args;
	} // argsPrepare

	private void sendMessage(String message)
	{
		message = sender + ": " + message;
		bot.sendMessage(channel, message);
	} // sendMessage

	public void firmware(String[] args)
	{
		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		args = this.argsPrepare(args);

		if(args[3].equals("info")) {
			bot.errorMessage(channel, sender, "this type of request is not supported.");
			return;
		} // if

		String response = makeURLRequest(APIBASE + "/" + args[1] + "/" + args[2] + "/" + args[3]);

		this.sendMessage((response != null
					? "the " + args[3] + " for " + args[1] + " (" + args[2] + ") is " + response 
					: "this request combo is unknown."));
	} // firmware

	public void redsn0w(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		args = this.argsPrepare(args);

		String response = makeURLRequest(APIBASE + "/redsn0w/" + args[1] + "/" + args[2] + "/" + args[3]);

		this.sendMessage((response != null
					? "the " + args[3] + " for " + args[1] + " (" + args[2] + ") is " + response 
					: "this request combo is unknown."));
	} // redsn0w

	public void iTunes(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		}

		args = this.argsPrepare(args);

		String response;

		// return both 32 and 64 bit urls for windows
		if(args[1].toLowerCase().equals("windows") && args[3].toLowerCase().equals("url"))
			response = "32 bit: " + makeURLRequest(APIBASE + "/iTunes/" + args[1] + "/" + args[2] + "/" + "url")
						+ " 64 bit: " + makeURLRequest(APIBASE + "/iTunes/" + args[1] + "/" + args[2] + "/" + "64biturl");
		else
			response = makeURLRequest(APIBASE + "/iTunes/" + args[1] + "/" + args[2] + "/" + args[3]);

		this.sendMessage((response != null
					? "the " + args[3] + " for " + args[1] + " (" + args[2] + ") is " + response 
					: "this request combo is unknown."));
	} // iTunes

	public void tss(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		String response = makeURLRequest("http://api.ineal.me/tss/" + args[1] + "/less");

		this.sendMessage(response);
	} // tss

	public void shsh(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		String response = makeURLRequest("http://api.ineal.me/shsh/" + args[1]);

		this.sendMessage(response);
	} // shsh

	public void pwnagetool(String[] args) {

		if(args.length < 2) {
			String[] argsNew = new String[3];

			argsNew[0] = args[0];
			argsNew[1] = "latest";
			argsNew[2] = "url";

			args = argsNew;
		} else if(args.length < 3) {
			String[] argsNew = new String[3];

			argsNew[0] = args[0];
			argsNew[1] = args[1];
			argsNew[2] = "url";

			args = argsNew;
		}

		String response = makeURLRequest(APIBASE + "/PwnageTool/" + args[1] + "/" + args[2]);

		this.sendMessage((response != null
					? "the " + args[2] + " for PwnageTool (" + args[1] + ") is " + response 
					: "this request combo is unknown."));
	} // pwnagetool

}
