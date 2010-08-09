
public class memory {
	/*
	public byte[] prgRom = null;
	public byte[] chrRom = null;
	*/
	
	public rom _rom;
	public byte[] _cpuRam = new byte[8 * 1024]; // 8k cpu ram

	/*
	 * read memory by cpu
	 */
	public byte cpuReadByteFromMem(short address){
		if ((address & 0xFFFF) < 0x2000 ){
			return _cpuRam[address & 0x07FF];
		}
		else if ((address & 0xFFFF) >= 0x8000 && (address & 0xFFFF) < 0xFFFF){
			byte value = _rom.prgRom[address & 0xFFFF - 0x8000];
			return value;
		}
		return 0;
	}
	
	public byte cpuReadByteZeroPage(byte address){
		return _cpuRam[address & 0x07FF];
	}
	
	public short cpuReadWordZeroPage(byte address){
		byte low = _cpuRam[address & 0x07FF];
		byte high = _cpuRam[(int)(address & 0x07FF) + 1];
		return (short)((high << 8 | low) & 0xFFFF);
	}
	
	public short cpuReadWordFromMem(short address){
		if ((address & 0xFFFF) < 0x2000){
			byte low = _cpuRam[address & 0x07FF];
			byte high = _cpuRam[(int)(address & 0x07FF) + 1];
			return (short)((high << 8 | low) & 0xFFFF);
		}
		if ((address & 0xFFFF) >= 0x8000 && (address & 0xFFFF) < 0xFFFF){
			byte low = _rom.prgRom[address & 0xFFFF - 0x8000];
			byte high = _rom.prgRom[(int)(address & 0xFFFF - 0x8000) + 1];
			return (short)((high << 8 | low) & 0xFFFF);
		}
		return 0;
	}
	
	
}
