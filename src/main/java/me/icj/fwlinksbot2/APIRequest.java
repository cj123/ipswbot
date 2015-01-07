package me.icj.fwlinksbot2;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;

public class APIRequest {

	private final static String APIBASE = "https://api.ipsw.me/v2.1";
	private final static double VERSION = 2.1;
	private final static String OLD_APIBASE = "https://api.ipsw.me/v2"; // i'm really sorry for this

	private final FwlinksBot bot;
	private final String channel;
	private final String sender;

	public APIRequest(FwlinksBot requiredBot, String requiredChannel, String requiredSender) {
		bot = requiredBot;
		channel = requiredChannel;
		sender = requiredSender;
	} // APIRequest

	private static String makeURLRequest(String url) {
		try {
			URL requestURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
			try {

				// connection properties
				connection.setRequestProperty("Connection", "close");
				connection.setRequestProperty("User-Agent", "fwlinksbot v" + VERSION);
				connection.setReadTimeout(5000);

				BufferedReader response = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				try {
					return response.readLine();
				} finally {
					response.close();
				} // finally

			} finally  {
				connection.disconnect();
			} // finally
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} // catch

		return null;
	} // makeURLRequest

	// fill out args when less than two have been supplied
	private String[] argsPrepare(String[] args) {
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

	private void sendMessage(String message) {
		message = sender + ": " + message;
		bot.sendSplitMessage(channel, message);
	} // sendMessage

	private String[] windowsAliases = new String[] {"windows", "win"};
	private String[] macAliases = new String[] {"mac os x", "macosx", "mac", "os x", "osx", "mac_os_x", "os_x"};

	private String platformAlias(String platform) {
		if (Arrays.asList(windowsAliases).contains(platform.toLowerCase())) {
			return "win";
		} else if (Arrays.asList(macAliases).contains(platform.toLowerCase())) {
			return "osx";
		} else {
			return "";
		}
	} // platformAlias

	private String parseRFC3339(String toParse) {
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat target = new SimpleDateFormat("EEE, MMM d yyyy 'at' HH:mm:ss");
		String result = "";

		try {
			Date date = parser.parse(toParse);
			result = target.format(date);
		} catch (Exception e) {
			System.out.println("Date format exception");
			System.err.println(e);
		}

		return result;
	} // parseRFC3339

	public void firmware(String[] args) {
		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		args = argsPrepare(args);

		if(args[3].equals("info") || args[3].equals("info.json")) {
			bot.errorMessage(channel, sender, "this type of request is not supported.");
			return;
		} // if

		String response = makeURLRequest(APIBASE + "/" + args[1] + "/" + args[2] + "/" + args[3]);

		if(args[3].equals("releasedate") || args[3].equals("uploaddate")) {
			// format the response in something slightly nicer than RFC3339
			response = parseRFC3339(response);
		}

		sendMessage((response != null
					? "the " + args[3] + " for " + args[1] + " (" + args[2] + ") is " + response 
					: "this request combo is unknown."));
	} // firmware

	public void redsn0w(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		args = argsPrepare(args);
		args[1] = platformAlias(args[1]);

		String response = makeURLRequest(OLD_APIBASE + "/redsn0w/" + args[1] + "/" + args[2] + "/" + args[3]);

		sendMessage((response != null
					? "the " + args[3] + " for " + args[1] + " (" + args[2] + ") is " + response 
					: "this request combo is unknown."));
	} // redsn0w

	public void iTunes(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		}

		args = argsPrepare(args);
		args[1] = platformAlias(args[1]);

		String response;

		// return both 32 and 64 bit urls for windows
		if(args[1].toLowerCase().equals("win") && args[3].toLowerCase().equals("url"))
			response = "32 bit: " + makeURLRequest(APIBASE + "/iTunes/" + args[1] + "/" + args[2] + "/" + "url")
						+ " 64 bit: " + makeURLRequest(APIBASE + "/iTunes/" + args[1] + "/" + args[2] + "/" + "64biturl");
		else
			response = makeURLRequest(APIBASE + "/iTunes/" + args[1] + "/" + args[2] + "/" + args[3]);

		if(args.length > 2 && (args[3].equals("releasedate") || args[3].equals("uploaddate"))) {
			// format the response in something slightly nicer than RFC3339
			response = parseRFC3339(response);
		}

		sendMessage((response != null
					? "the " + args[3] + " for " + args[1] + " (" + args[2] + ") is " + response
					: "this request combo is unknown."));
	} // iTunes

	public void tss(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		String response = makeURLRequest("http://api.ineal.me/tss/" + args[1] + "/less");

		if(!response.equals("null"))
			sendMessage(response);
	} // tss

	public void shsh(String[] args) {

		if(args.length < 2) {
			bot.errorMessage(channel, sender, "incorrect parameters. !help for details");
			return;
		} // if

		String response = makeURLRequest("http://api.ineal.me/shsh/" + args[1]);

		if(!response.equals("null"))
			sendMessage(response);
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
		} // else if

		String response = makeURLRequest(OLD_APIBASE + "/PwnageTool/" + args[1] + "/" + args[2]);

		sendMessage((response != null
					? "the " + args[2] + " for PwnageTool (" + args[1] + ") is " + response 
					: "request combo is unknown."));
	} // pwnagetool

} // APIRequest
