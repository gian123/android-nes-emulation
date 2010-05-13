
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
		
		return 0;
	}
	
	public short cpuReadWord(short address){
		
		if ((address & 0xFFFF) >= 0x8000 && (address & 0xFFFF) < 0xFFFF){
			byte low = _rom.prgRom[address & 0xFFFF - 0x8000];
			byte high = _rom.prgRom[(int)(address & 0xFFFF - 0x8000) + 1];
			return (short)((high << 8 | low) & 0xFFFF);
		}//else if ((address & 0xFFFF) >= 0x8000 && (address & 0xFFFF) < 0xC000)
		return 0;
	}
	
}
