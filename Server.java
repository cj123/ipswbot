import java.util.ArrayList;
import java.util.List;

public class Server
{
	// channels, address and port
	private List<String> channels = new ArrayList<String>();
	private String address;
	private int port;

	public Server(String requiredAddress, int requiredPort, String[] requiredChannels)
	{
		for(String channel : requiredChannels)
			channels.add(channel);
			
		address = requiredAddress;
		port = requiredPort;
	} // Server

	public String getAddress()
	{
		return address;
	} // getAddress

	public int getPort()
	{
		return port;
	} // getPort

	public void addChannel(String channelName)
	{
		// add a channel to the channels List
		if(!channels.contains(channelName))
			channels.add(channelName);
	} // addChannel

	public List<String> getChannels()
	{
		return channels;
	} // getChannels

	public String toString()
	{
		// get channels comma separated
		StringBuilder builder = new StringBuilder();
		builder.append(channels.get(0));
		for (int index = 1; index < channels.size(); index++)
		{
			builder.append(", ");
			builder.append(channels.get(index));
		} // for

		return "Server: " + address + ":" + port + ". Channels: " + builder;
	} // toString
} // Server