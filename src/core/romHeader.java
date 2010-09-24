package core;

public class romHeader {
	public byte nesId[] = new byte[4];
	public byte prgRomCount;
	public byte chrRomCount;
	public byte ctrlByte1;
	public byte ctrlByte2;
	public byte ram8kCount;
	public byte[] reserved = new byte[7];
}
