package me.icj.fwlinksbot2;

import org.jibble.pircbot.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FwlinksBot extends PircBot {
	private Server server;
	private String logLocation = "";

	public FwlinksBot(Server requiredServer, String requiredLogLocation) {
		super();

		server = requiredServer;
		logLocation = requiredLogLocation;

		// configuration
		setName("ipswbot");
		setLogin("ipswbot");
		setVersion("ipswbot");
		setAutoNickChange(true);
	}

	public void errorMessage(String channel, String sender, String message) {
		printLog("error: " + sender + ": " + message);
		sendMessage(channel, sender + ": " + message);
	} // errorMessage

	// the max amount of chars before splitting a message into parts
	private static final int MESSAGE_SPLIT_LENGTH = 300;

	public void sendSplitMessage(String channel, String message) {

		printLog("Sending message '" + message + "' to " + channel);

		if(message.length() >= 1000) {
			// this request obviously is not valid. Kill it, kill it quick
			printLog("response was too long. not sending to channel...");
			return;
		} else if(message.length() > MESSAGE_SPLIT_LENGTH) {

			String[] splitInput = message.split("(?<=\\G.{" + MESSAGE_SPLIT_LENGTH + "})");

			sendMessage(channel, splitInput[0] + "...");

			for(int index = 1; index < splitInput.length - 1; index ++)
				sendMessage(channel, splitInput[index] + "...");

			sendMessage(channel, splitInput[splitInput.length - 1]);

		} else {
			sendMessage(channel, message);
		}
	} // sendSplitMessage

	@Override
	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
		printLog("Invite to channel " + channel + " received. Joining...");
		joinChannel(channel);
		server.addChannel(channel);
		// sendMessage(channel, sourceNick + ": thanks for the invite!");
	} // onInvite

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		// parse args into an array
		String[] args = message.split(" ");

		APIRequest api = new APIRequest(this, channel, sender);

		String request = args[0].toLowerCase();

		if(request.equals("!help"))
			sendMessage(channel, sender + ": view my commands here: https://api.ipsw.me/docs/misc/fwlinksbot"
											 + " i'm open source! more info here: https://gitlab.icj.me/fwlinks/bot");
		else if(request.equals("!fw") || request.equals("!firmware"))
			api.firmware(args);
		else if(request.equals("!redsn0w") || request.equals("!rs"))
			api.redsn0w(args);
		else if(request.equals("!itunes") || request.equals("!it"))
			api.iTunes(args);
		else if(request.equals("!tss"))
			api.tss(args);
		else if(request.equals("!shsh"))
			api.shsh(args);
		else if(request.equals("!pwnagetool") || request.equals("!pt"))
			api.pwnagetool(args);
		else if(request.equals("!fwinfo"))
			sendMessage(channel, server.toString());

	} // onMessage

	public void printLog(String message) {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss] ");
		System.out.println(dateFormat.format(date) + message);

		// write to the log file too
		writeLog(dateFormat.format(date) + message);
	}

	private void writeLog(String message) {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		PrintWriter output = null;

		try {
			File logFile = new File(logLocation, dateFormat.format(date) + ".log");

			if(!logFile.exists()) {
				logFile.createNewFile();
			}

			output = new PrintWriter(new FileWriter(logFile, true));
			output.println(message);
		} catch (IOException exception) {
			System.err.println(exception);
		} finally {
			if(output != null) {
				output.close();

				if(output.checkError())
					System.err.println("Unable to close log location");
			} // if
		}
	}

	@Override
	protected void onDisconnect() {
		int reconnectAttempts = 0;

		printLog("Connection to " + getServer() + " dropped, "
											 + "trying to reconnect...");

		// try to reconnect
		while(!isConnected() && reconnectAttempts < 15) {
			try {
				// reconnect
				reconnectAttempts++;
				reconnect();
			} catch (Exception e) {
				// failed to reconnect
				printLog("Error: could not reconnect to " + getServer() + "\n" + e.getMessage());
				try {
						Thread.sleep(4000);
				} catch(InterruptedException exception) {
						Thread.currentThread().interrupt();
				} // catch
			} // catch
		} // while

		// check if it has reconnected
		if(isConnected()) {
			printLog("Reconnected to server: " + getServer());

			if(server.getAddress().equals(getServer())) {
				// rejoin channels
				for(String channel : server.getChannels())
					joinChannel(channel);
			} else {
				printLog("We somehow connected to a different server?");
			} // else
		} else {
			printLog("Unable to reconnect to server: " + getServer() + " - disabling.");
		} // else

	} // onDisconnect

} // FwlinksBot