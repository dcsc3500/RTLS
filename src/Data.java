
public class Data {
	Receiver r;
	byte[]p;
	int packetSize;
	Data(Receiver r, byte[]arr, int n)
	{
		this.r = r;
		p = arr;
		packetSize = n;
	}
}
