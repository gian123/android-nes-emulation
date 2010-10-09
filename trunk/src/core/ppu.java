package core;

public class ppu {
	private final byte PPU_VBLANK_FLAG = (byte)0x80;
	
	private memory _mem = null;
	
	// $2000 write ppu control register 1
	// $2001 write ppu control register 2
	// $2002 read  ppu status register
	// $2003 write SPR-RAM address register
	// $2004 write SPR_RAM I/O register
	// $2005 write VRAM address register 1
	// $2006 write VRAM address register 2
	// $2007 read/write VRAM I/O register
	private byte[] _ppuReg = new byte[8];
	
	public void setMemory(memory mem){
		_mem = mem;
	}
	
	public byte read(short address){
		byte data = 0x00;

		// ppu register $2000-$2007
		switch (address) {
			// Write only Register
			case 0x2000: // PPU Control Register #1(W)
			case 0x2001: // PPU Control Register #2(W)
			case 0x2003: // SPR-RAM Address Register(W)
			case 0x2005: // PPU Scroll Register(W2)
			case 0x2006: // VRAM Address Register(W2)
				//data = PPU7_Temp;	
				break;
			// Read/Write Register
			case	0x2002: // PPU Status Register(R)
				data = _ppuReg[2]; //| VSSecurityData;
				// PPU56Toggle = 0;
				_ppuReg[2] &= ~PPU_VBLANK_FLAG;
				break;
			case	0x2004: // SPR_RAM I/O Register(RW)
				data = _mem._spriteRam[_ppuReg[3]++];
				break;
			case	0x2007: // VRAM I/O Register(RW), reads or writes a byte from VRAM at current address
				short addr = loopy_v & 0x3FFF;
				data = PPU7_Temp;
				if( _ppuReg[0] & PPU_INC32_BIT ) 
					loopy_v+=32;
				else				
					loopy_v++;
				if( addr >= 0x3000 ) {
					if( addr >= 0x3F00 ) {
						if( !(addr&0x0010) ) {
							return	BGPAL[addr&0x000F];
						} else {
							return	SPPAL[addr&0x000F];
						}
					}
					addr &= 0xEFFF;
				}
				PPU7_Temp = PPU_MEM_BANK[addr>>10][addr&0x03FF];
		}

		return	data;
	}
	
	public void write(short address){
		switch (address){
		case 0x2000:
			break;
		}
	}
	
	public void scanLine(){
			
	}
}
