import org.jibble.pircbot.*;

public class FwlinksBot extends PircBot
{
	public FwlinksBot()
	{
		this.setName("fwlinksbot");
		this.setLogin("fwlinks");
		this.setVersion("fwlinksbot");
		//this.setIdent("fwlinks");
	} // FwlinksBot

	public static void main(String[] args) throws Exception {

		Server[] servers = { 
			new Server("irc.chronic-dev.org", 6667, new String[] {"#cj-case", "#iH8sn0w"}),
			new Server("iphun.osx86.hu", 6667, new String[] {"#ios"}),
			new Server("irc.saurik.com", 6667, new String[] {"#teambacon"}),
			new Server("irc.freenode.net", 6667, new String[] {"#jailbreakqa", "#openjailbreak", "#testfwlinks"})
		};

		// connect to each server
		for(Server server : servers) {

			FwlinksBot bot = new FwlinksBot();
			try 
			{
				// configuration
				bot.setVerbose(false);
				bot.setAutoNickChange(true);
				bot.connect(server.getAddress(), server.getPort());

				for(String channel : server.getChannels())
					bot.joinChannel(channel);
				
			} // try
			catch (Exception exception) 
			{
				System.out.println(exception);
			} // catch
		} // for
	} // main

	public void errorMessage(String channel, String sender, String message) {
		sendMessage(channel, sender + ": " + message);
	} // errorMessage

	protected void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)
	{
		joinChannel(channel);
		sendMessage(channel, sourceNick + ": thanks for the invite!");
	} // onInvite

	protected void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		// parse args into an array
		String[] args = message.split(" ");

		APIRequest api = new APIRequest(this, channel, sender);

		switch(args[0].toLowerCase()) {

			case "!help":
				this.sendMessage(channel, sender + ": view my commands here: http://api.ios.icj.me/docs/fwlinksbot");
			break;

			case "!fw":
			case "!firmware":
				api.firmware(args);
			break;

			case "!redsn0w":
			case "!rs":
				api.redsn0w(args);
			break;

			case "!itunes":
			case "!it":
				api.iTunes(args);
			break;

			case "!tss":
				api.tss(args);
			break;

			case "!shsh":
				api.shsh(args);
			break;

			case "!pwnagetool":
			case "!pt":
				api.pwnagetool(args);
			break;

			default:
			break;

		} // switch

		return;

	} // onMessage

}
