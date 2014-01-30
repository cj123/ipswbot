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
		FwlinksBot bot = new FwlinksBot();
		try 
		{
			// configuration
			bot.setVerbose(false);
			bot.connect("irc.freenode.net");

			bot.joinChannel("#JailbreakQA");
			bot.joinChannel("#testfwlinks");
		} // try
		catch (Exception exception) 
		{
			System.out.println(exception);
		} // catch
	} // main

	public void errorMessage(String channel, String sender, String message) {
		sendMessage(channel, sender + ": " + message);
	} // errorMessage

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
