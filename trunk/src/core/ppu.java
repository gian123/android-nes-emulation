package core;

public class ppu {
	private final byte PPU_VBLANK_FLAG = (byte)0x80;
	
	private memory _mem = null;
	private int _curScanLineIndex = 0;
	private int _displayX = 0;
	private int _displayY = 0;
	
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
				_ppuReg[2] &= ~PPU_VBLANK_FLAG;
				break;
			case	0x2004: // SPR_RAM I/O Register(RW)
				data = _mem._spriteRam[_ppuReg[3]++];
				break;
			// VRAM I/O Register(RW), reads or writes a byte from VRAM at current address
			case	0x2007: { 
				return read2007();				
			}
		}

		return	data;
	}
	
	private byte read2007(){
		return 0;
	}
	
	
	public void write(short address, byte value){
		switch (address){
		// ppu¿ØÖÆ¼Ä´æÆ÷ #1 (w)
		case 0x2000:
			write2000(value);
			break;
		// ppu¿ØÖÆ¼Ä´æÆ÷ #2 (w)
		case 0x2001:
			write2001(value);
			break;
		// ppu×´Ì¬¼Ä´æÆ÷
		case 0x2002:
			write2002(value);
			break;
		case 0x2003:
			write2003(value);
			break;
		case 0x2004:
			write2004(value);
			break;
		case 0x2005:
			write2005(value);
			break;
		case 0x2006:
			write2006(value);
			break;
		case 0x2007:
			write2007(value);
			break;
		}
	}
	
	private void write2000(byte value){
		
	}
	
	private void write2001(byte value){
		
	}
	
	private void write2002(byte value){
		
	}
	
	private void write2003(byte value){
			
	}
	private void write2004(byte value){
		
	}
	private void write2005(byte value){
		
	}
	private void write2006(byte value){
		
	}
	private void write2007(byte value){
		
	}
	
	
	public void scanLine(){
		rendBackground();
		rendSprites();
	}
	
	private void rendBackground(){
		
	}
	
	private void rendSprites(){
		
	}
}
