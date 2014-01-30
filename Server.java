public class Server
{
	private String[] channels;
	private String address;
	private int port;

	public Server(String requiredAddress, int requiredPort, String[] requiredChannels)
	{
		this.channels = requiredChannels;
		this.address = requiredAddress;
		this.port = requiredPort;
	} // Server

	public String getAddress()
	{
		return address;
	} // getAddress

	public int getPort()
	{
		return port;
	} // getPort

	public String[] getChannels() {
		return channels;
	} // getChannels
}