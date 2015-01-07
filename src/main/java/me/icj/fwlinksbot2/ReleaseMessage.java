package me.icj.fwlinksbot2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;

public class ReleaseMessage extends Thread {
	private static final int socketPort = 4536;
	private final FwlinksBot[] bots;

	@Override
	public void run() {
		bots[0].printLog("starting socket server on port " + socketPort);

		// to avoid try-with-resources which isn't supported
		// before 1.7
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		BufferedReader in = null;

		try {
			// configure sockets
			serverSocket = new ServerSocket(socketPort, 0, InetAddress.getByName(null));

			while(true) {
				clientSocket = serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					// new message
					bots[0].printLog("new session message: " + inputLine);

					for (FwlinksBot bot : bots) {
						// send to each channel
						for(String channel : bot.getChannels()) {
							bot.sendSplitMessage(channel, "[Update] " + inputLine);
						} // for
					} // for
				} // while
			} // while
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if(serverSocket != null) serverSocket.close();
				if(clientSocket != null) clientSocket.close();
				if(in != null) in.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} // finally

	} // run

	public ReleaseMessage(FwlinksBot[] requiredBots) {
		bots = requiredBots;
	} // ReleaseMessage

} // ReleaseMessage