
public class memory {
   /*
	 6502 cpu memory:
	  ________________  $10000
	 |				  |
	 |				  |
	 |    PRG-ROM     |
	 |				  |
	 |				  |
	 |________________| $8000
	 |     SRAM       | 
	 |________________| $6000
	 |  Expansion Rom | 
	 |________________| $4020
	 |                | 
	 |				  |
	 |  I/O Registers |
	 |				  |
	 |________________| $2000
	 |                | 
	 |				  |
	 |      RAM       |
	 |				  |
	 |________________| $0000
	 
	 */
	public rom _rom;
	public byte[] _cpuRam = new byte[8 * 1024]; // 8k cpu ram

	/*
	 * read memory by cpu
	 */
	public byte cpuReadByteFromMem(short address){
		//
		// mirrored $0000 - $07FF three times 
		// ($0800 - $0FFF, $1000 - $1FFF )
		//
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
		// zero page $0000 - $00FF
		return _cpuRam[address & 0x00FF]; 
	}
	
	public short cpuReadWordZeroPage(byte address){
		byte low = _cpuRam[address & 0x00FF];
		byte high = _cpuRam[(int)(address & 0x00FF) + 1];
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
	
	// write to zero page
	public void cpuWriteByteToZeroPage(byte address, byte value){
		_cpuRam[address & 0x00FF] = value;
	}
	
	public void cpuWriteByteToMem(short address, byte value){
		if ((address & 0xFFFF) < 0x2000){
			_cpuRam[address & 0x07FF] = value;
		}
		else {
			//....other
		}
	}
}
