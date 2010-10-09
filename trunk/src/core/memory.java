package core;

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
	
	public ppu _ppu;
	
	public byte[] _cpuRam = new byte[8 * 1024]; // 8k cpu ram

	// 2 pattern table, ($0000-$0FFF $1000-$1FFF)
	public byte[] _patternTable = new byte[8 * 1024];
	// 32 * 20 tiles
	public byte[] _nameTable = new byte[2 * 96];
	public byte[] _attributeTable = new byte[2 * 64];
	
	public byte[] _spriteRam = new byte[256];
	/*
	 * read memory by cpu
	 */
	public byte cpuReadByteFromMem(short address){
		//
		// mirrored $0000 - $07FF three times 
		// ($0800 - $0FFF, $1000 - $1FFF )
		//
		
		switch (address >> 13){
			case	0x00:	// $0000-$1FFF 
				return	_cpuRam[address & 0x07FF];
			case	0x01:	// $2000-$3FFF
				return	_ppu.read( (short)(address & 0xE007) );
//			case	0x02:	// $4000-$5FFF
//				if( address < 0x4100 ) {
//					return	ReadReg( address );
//				} else {
//					return	mapper->ReadLow( addr );
//				}
//				break; 
//			case	0x03:	// $6000-$7FFF
//				return	mapper->ReadLow( addr );
			case	0x04:	// $8000-$9FFF
			case	0x05:	// $A000-$BFFF
			case	0x06:	 // $C000-$DFFF
			case	0x07:	// $E000-$FFFF
				return _rom.prgRom[address & 0xFFFF - 0x8000];
		}
		
		
//		if ((address & 0xFFFF) < 0x2000 ){
//			return _cpuRam[address & 0x07FF];
//		}
//		else if ((address & 0xFFFF) >= 0x8000 && (address & 0xFFFF) < 0xFFFF){
//			byte value = _rom.prgRom[address & 0xFFFF - 0x8000];
//			return value;
//		}
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
