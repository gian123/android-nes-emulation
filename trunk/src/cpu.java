
public class cpu {
	private r6502Register _register = new r6502Register();
	private memory _cpuMemory = null;
	
	private byte  _byte_data;
	private short _word_effectiveAddress;
	private short _word_temp;
	private short _word_effectiveAddressTemp;
	private int _excutedCycles;
	
	//public cpu(){
	//	reset();
	//}
	
	public void setMemory(memory mem){
		_cpuMemory = mem;
	}
	
	public void reset(){
		_register.reset();
		_register.PC = _cpuMemory.cpuReadWordFromMem(r6502Register.RES_VECTOR);
		
	}
	
	private void setCpuFlag(boolean condition, byte flag){
		if (condition){
			_register.P |= flag;
		}
	}

	private void memReadImmdiate(){
		_byte_data = _cpuMemory.cpuReadByteFromMem(_register.PC++);
	}
	
	private void memReadZeroPage(){
		_word_effectiveAddress = _cpuMemory.cpuReadByteFromMem(_register.PC++);
		_byte_data = _cpuMemory.cpuReadByteZeroPage((byte)_word_effectiveAddress);
	}
	
	private void memReadZeroPageX(){
  		_word_effectiveAddress = _cpuMemory.cpuReadByteFromMem(_register.PC++);
  		_word_effectiveAddress += _register.X;
  		_byte_data = _cpuMemory.cpuReadByteZeroPage((byte)_word_effectiveAddress);
	}
	
	private void memReadZeroPageY(){
		_word_effectiveAddress = _cpuMemory.cpuReadByteFromMem(_register.PC++);
  		_word_effectiveAddress += _register.Y;
  		_byte_data = _cpuMemory.cpuReadByteZeroPage((byte)_word_effectiveAddress);
	}
	
	private void memReadAbsolute(){
		_word_effectiveAddress = _cpuMemory.cpuReadWordFromMem(_register.PC);
		_register.PC += 2;
		_byte_data = _cpuMemory.cpuReadByteFromMem(_word_effectiveAddress);
	}
	
	private void memReadAbsoluteX(){
		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordFromMem(_register.PC);
		_register.PC += 2;
		_word_effectiveAddress = (short)(_word_effectiveAddressTemp + _register.X);
		_byte_data = _cpuMemory.cpuReadByteFromMem(_word_effectiveAddress);
	}
	
	private void memReadAbsoluteY(){
		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordFromMem(_register.PC);
		_register.PC += 2;
		_word_effectiveAddress = (short)(_word_effectiveAddressTemp + _register.Y);
		_byte_data = _cpuMemory.cpuReadByteFromMem(_word_effectiveAddress);
	}
	
	private void memReadIndirect(){
		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordFromMem(_register.PC);
		_register.PC += 2;
		_word_effectiveAddress = _cpuMemory.cpuReadWordFromMem(_word_effectiveAddressTemp);
		_byte_data = _cpuMemory.cpuReadByteFromMem(_word_effectiveAddress);
	}
	
	private void memReadIndexedIndirectX(){
		_byte_data = _cpuMemory.cpuReadByteFromMem(_register.PC++);
		_word_effectiveAddress = _cpuMemory.cpuReadWordZeroPage((byte)(_byte_data + _register.X));
		_byte_data = _cpuMemory.cpuReadByteFromMem(_word_effectiveAddress);
	}
	
	private void memReadIndirectIndexedY(){
		_byte_data = _cpuMemory.cpuReadByteFromMem(_register.PC++);
		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordZeroPage(_byte_data);
		_word_effectiveAddress = (short)(_word_effectiveAddressTemp + _register.Y);
		_byte_data = _cpuMemory.cpuReadByteFromMem(_word_effectiveAddress);
	}
	
	//
	// set negative zero flag
	//
	private void setNZFlag(byte data){
		if (data == 0x0)
			_register.P |= r6502Register.Z_FLAG;
		if ((data & 0x80) != 0x0)
			_register.P |= r6502Register.N_FLAG;
	}

	private void cpuExecADC(){
		_word_temp = (short)(_byte_data + _register.A + (_register.P & r6502Register.C_FLAG));
		setCpuFlag(_word_temp > (short)0xFF, r6502Register.C_FLAG);
		// overflow .. set when negative + negtive = positive or postive + postive  = negative
		setCpuFlag(((_register.A ^ _word_temp) & (_byte_data ^ _word_temp) & 0x80) != 0, r6502Register.V_FLAG);
		_register.A = (byte)_word_temp;
		setNZFlag(_register.A);
	}
	
	//
	// 
	//
	private void cpuExecSBC(){
		 _word_temp = (short) ((short)_register.A - (short)_byte_data - (short)(~(_register.P & r6502Register.C_FLAG)));
		 //此处 flag 的设置？？
		 setCpuFlag(((_register.A ^ _byte_data) & (_register.A ^ _word_temp) & 0x80) != 0, r6502Register.V_FLAG);
		 setCpuFlag(_word_temp < 0x100, r6502Register.C_FLAG);
		 //
		 _register.A = (byte)_word_temp;
		 setNZFlag(_register.A);
	}
	
	private void cpuExecDec(){
		--_byte_data;
		setNZFlag(_byte_data);
	}
	
	private void checkEA(){
		if ((_word_effectiveAddressTemp & 0xFF00) != (_word_effectiveAddress & 0xFF00))
			_excutedCycles += 1;
	}
	/*
	 * run cpu for certain cycles
	 */
	public void excute(int requestCycles){
		_excutedCycles = 0;
		
		byte opCode = 0;
		opCode = _cpuMemory.cpuReadByteFromMem(_register.PC++);
		
		byte byteData;
		short wordTemp;
		short effetiveAddress;
		switch(opCode){
		
		
		// ADC #$??
		case	(byte)0x69: {
			memReadImmdiate();
			cpuExecADC();
			_excutedCycles += 2;
		}
			break;
			// ADC $??
		case	(byte)0x65: {
			memReadZeroPage();
			cpuExecADC();
			_excutedCycles += 3;
		}
			break;
			 // ADC $??,X
		case	(byte)0x75:{
			memReadZeroPageX();
			cpuExecADC();
			_excutedCycles += 4;
		}
			break;
			// ADC $????
		case	(byte)0x6D: {
			memReadAbsolute();
			cpuExecADC();
			_excutedCycles += 4;
		}
			break;
			// ADC $????,X
		case	(byte)0x7D: {
			memReadAbsoluteX();
			cpuExecADC();
			checkEA();
			_excutedCycles += 4;
		}
			break;
			 // ADC $????,Y
		case	(byte)0x79:{
			memReadAbsoluteY();
			cpuExecADC();
			checkEA();
			_excutedCycles += 4;
		}
			break;
			// ADC ($??,X)
		case	(byte)0x61: {
			memReadIndexedIndirectX();
			cpuExecADC();
			_excutedCycles += 6;
		}
			break;
			// ADC ($??),Y
		case	(byte)0x71: {
			memReadIndirectIndexedY();
			cpuExecADC();
			checkEA();
			_excutedCycles += 6;
		}
			break;
			// SBC #$??
		case	(byte)0xE9: {
			memReadImmdiate();
			cpuExecSBC();
			_excutedCycles += 2;
		}
			break;
			// SBC $??
		case	(byte)0xE5: {
			memReadZeroPage();
			cpuExecSBC();
			_excutedCycles += 3;
		}
			break;
			// SBC $??,X
		case	(byte) 0xF5: {
			memReadZeroPageX();
			cpuExecSBC();
			_excutedCycles += 4;
		}
			
			break;
			// SBC $????
		case	(byte)0xED:{
			memReadAbsolute();
			cpuExecSBC();
			_excutedCycles += 4;
		}
			break;
			// SBC $????,X
		case	(byte)0xFD: {
			memReadAbsoluteX();
			cpuExecSBC();
			checkEA();
			_excutedCycles += 4;
		}
			break;
			// SBC $????,Y
		case	(byte)0xF9: {
			memReadAbsoluteY();
			cpuExecSBC();
			checkEA();
			_excutedCycles += 4;
		}
			break;
			// SBC ($??,X)
		case	(byte)0xE1: {
			memReadIndexedIndirectX();
			cpuExecSBC();
			_excutedCycles += 6;
		}
			break;
			// SBC ($??),Y
		case	(byte)0xF1: {
			memReadIndirectIndexedY();
			cpuExecSBC();
			checkEA();
			_excutedCycles += 5;
		}
			break;
			
			// DEC $??
		case	(byte)0xC6: {
			memReadImmdiate();
			cpuExecDec();
			
		}
			break;
		case	(byte)0xD6: // DEC $??,X
			
			break;
		case	(byte)0xCE: // DEC $????
			
			break;
		case	(byte)0xDE: // DEC $????,X
			
			break;

		case	(byte)0xCA: // DEX
			
			break;
		case	(byte)0x88: // DEY
			
			break;

		case	(byte)0xE6: // INC $??
			
			break;
		case	(byte)0xF6: // INC $??,X
			
			break;
		case	(byte)0xEE: // INC $????
			
			break;
		case	(byte)0xFE: // INC $????,X
			
			break;

		case	(byte)0xE8: // INX
			
			break;
		case	(byte)0xC8: // INY
			
			break;

		case	(byte)0x29: // AND #$??
			
			break;
		case	(byte)0x25: // AND $??
			
			break;
		case	(byte)0x35: // AND $??,X
			
			break;
		case	(byte)0x2D: // AND $????
			
			break;
		case	(byte)0x3D: // AND $????,X
			
			break;
		case	(byte)0x39: // AND $????,Y
			
			break;
		case	(byte)0x21: // AND ($??,X)
			
			break;
		case	(byte)0x31: // AND ($??),Y
			
			break;

		case	(byte)0x0A: // ASL A
			
			break;
		case	(byte)0x06: // ASL $??
			
			break;
		case	(byte)0x16: // ASL $??,X
			
			break;
		case	(byte)0x0E: // ASL $????
			
			break;
		case	(byte)0x1E: // ASL $????,X
			
			break;

		case	(byte)0x24: // BIT $??
			
			break;
		case	(byte)0x2C: // BIT $????
			
			break;

		case	(byte)0x49: // EOR #$??
			
			break;
		case	(byte)0x45: // EOR $??
			
			break;
		case	(byte)0x55: // EOR $??,X
			
			break;
		case	(byte)0x4D: // EOR $????
			
			break;
		case	(byte)0x5D: // EOR $????,X
			
			break;
		case	(byte)0x59: // EOR $????,Y
			
			break;
		case	(byte)0x41: // EOR ($??,X)
			
			break;
		case	(byte)0x51: // EOR ($??),Y
			
			break;

		case	(byte)0x4A: // LSR A
			
			break;
		case	(byte)0x46: // LSR $??
			
			break;
		case	(byte)0x56: // LSR $??,X
			
			break;
		case	(byte)0x4E: // LSR $????
			
			break;
		case	(byte)0x5E: // LSR $????,X
			
			break;

		case	(byte)0x09: // ORA #$??
			
			break;
		case	(byte)0x05: // ORA $??
			
			break;
		case	(byte)0x15: // ORA $??,X
			
			break;
		case	(byte)0x0D: // ORA $????
			
			break;
		case	(byte)0x1D: // ORA $????,X
			
			break;
		case	(byte)0x19: // ORA $????,Y
			
			break;
		case	(byte)0x01: // ORA ($??,X)
			
			break;
		case	(byte)0x11: // ORA ($??),Y
			
			break;

		case	(byte)0x2A: // ROL A
			
			break;
		case	(byte)0x26: // ROL $??
			
			break;
		case	(byte)0x36: // ROL $??,X
			
			break;
		case	(byte)0x2E: // ROL $????
			
			break;
		case	(byte)0x3E: // ROL $????,X
			
			break;

		case	(byte)0x6A: // ROR A
			
			break;
		case	(byte)0x66: // ROR $??
			
			break;
		case	(byte)0x76: // ROR $??,X
			
			break;
		case	(byte)0x6E: // ROR $????
			
			break;
		case	(byte)0x7E: // ROR $????,X
			
			break;

		case	(byte)0xA9: // LDA #$??
			
			break;
		case	(byte)0xA5: // LDA $??
			
			break;
		case	(byte)0xB5: // LDA $??,X
			
			break;
		case	(byte)0xAD: // LDA $????
			
			break;
		case	(byte)0xBD: // LDA $????,X
			
			break;
		case	(byte)0xB9: // LDA $????,Y
			
			break;
		case	(byte)0xA1: // LDA ($??,X)
			
			break;
		case	(byte)0xB1: // LDA ($??),Y
			
			break;

		case	(byte)0xA2: // LDX #$??
			
			break;
		case	(byte)0xA6: // LDX $??
			
			break;
		case	(byte)0xB6: // LDX $??,Y
			
			break;
		case	(byte)0xAE: // LDX $????
			
			break;
		case	(byte)0xBE: // LDX $????,Y
			
			break;

		case	(byte)0xA0: // LDY #$??
			
			break;
		case	(byte)0xA4: // LDY $??
			
			break;
		case	(byte)0xB4: // LDY $??,X
			
			break;
		case	(byte)0xAC: // LDY $????
			
			break;
		case	(byte)0xBC: // LDY $????,X
			
			break;

		case	(byte)0x85: // STA $??
			
			break;
		case	(byte)0x95: // STA $??,X
			
			break;
		case	(byte)0x8D: // STA $????
			
			break;
		case	(byte)0x9D: // STA $????,X
			
			break;
		case	(byte)0x99: // STA $????,Y
			
			break;
		case	(byte)0x81: // STA ($??,X)
			
			break;
		case	(byte)0x91: // STA ($??),Y
			
			break;

		case	(byte)0x86: // STX $??
			
			break;
		case	(byte)0x96: // STX $??,Y
			
			break;
		case	(byte)0x8E: // STX $????
			
			break;

		case	(byte)0x84: // STY $??
			
			break;
		case	(byte)0x94: // STY $??,X
			
			break;
		case	(byte)0x8C: // STY $????
			
			break;

		case	(byte)0xAA: // TAX
			
			break;
		case	(byte)0x8A: // TXA
			
			break;
		case	(byte)0xA8: // TAY
			
			break;
		case	(byte)0x98: // TYA
			
			break;
		case	(byte)0xBA: // TSX
			
			break;
		case	(byte)0x9A: // TXS
		
			break;

		case	(byte)0xC9: // CMP #$??
			
			break;
		case	(byte)0xC5: // CMP $??
			
			break;
		case	(byte)0xD5: // CMP $??,X
			
			break;
		case	(byte)0xCD: // CMP $????
			
			break;
		case	(byte)0xDD: // CMP $????,X
			
			break;
		case	(byte)0xD9: // CMP $????,Y
			
			break;
		case	(byte)0xC1: // CMP ($??,X)
			
			break;
		case	(byte)0xD1: // CMP ($??),Y
			
			break;

		case	(byte)0xE0: // CPX #$??
			
			break;
		case	(byte)0xE4: // CPX $??
			
			break;
		case	(byte)0xEC: // CPX $????
			
			break;

		case	(byte)0xC0: // CPY #$??
			
			break;
		case	(byte)0xC4: // CPY $??
			
			break;
		case	(byte)0xCC: // CPY $????
			
			break;

		case	(byte)0x90: // BCC
			
			break;
		case	(byte)0xB0: // BCS
			
			break;
		case	(byte)0xF0: // BEQ
			
			break;
		case	(byte)0x30: // BMI
			
			break;
		case	(byte)0xD0: // BNE
			
			break;
		case	(byte)0x10: // BPL
			
			break;
		case	(byte)0x50: // BVC
			
			break;
		case	(byte)0x70: // BVS
			
			break;

		case	(byte)0x4C: // JMP $????
			
			break;
		case	(byte)0x6C: // JMP ($????)
			
			break;

		case	(byte)0x20: // JSR
			
			break;

		case	(byte)0x40: // RTI
			
			break;
		case	(byte)0x60: // RTS
			
			break;


		case	(byte)0x18: // CLC
			
			break;
		case	(byte)0xD8: // CLD
			
			break;
		case	(byte)0x58: // CLI
			
			break;
		case	(byte)0xB8: // CLV
			
			break;

		case	(byte)0x38: // SEC
			
			break;
		case	(byte)0xF8: // SED
			
			break;
		case	(byte)0x78: { // SEI
			_register.P |= r6502Register.I_FLAG;
			_excutedCycles += 2;
		}
			break;

// 僗僞僢僋宯
		case	(byte)0x48: // PHA
			
			break;
		case	(byte)0x08: // PHP
			
			break;
		case	(byte)0x68: // PLA (N-----Z-)
			
			break;
		case	(byte)0x28: // PLP
			
			break;

// 偦偺懠
		case	(byte)0x00: // BRK
			
			break;

		case	(byte)0xEA: // NOP
			//ADD_CYCLE(2);
			break;

// 枹岞奐柦椷孮
		case	0x0B: // ANC #$??
		case	0x2B: // ANC #$??
			//MR_IM(); ANC();
			//ADD_CYCLE(2);
			break;

		case	(byte)0x8B: // ANE #$??
			//MR_IM(); ANE();
			//ADD_CYCLE(2);
			break;

		case	(byte)0x6B: // ARR #$??
			//MR_IM(); ARR();
			//ADD_CYCLE(2);
			break;

		case	(byte)0x4B: // ASR #$??
			//MR_IM(); ASR();
			//ADD_CYCLE(2);
			break;

		case	(byte)0xC7: // DCP $??
			//MR_ZP(); DCP(); MW_ZP();
			//ADD_CYCLE(5);
			break;
		case	(byte)0xD7: // DCP $??,X
			//MR_ZX(); DCP(); MW_ZP();
			//ADD_CYCLE(6);
			break;
		case	(byte)0xCF: // DCP $????
			//MR_AB(); DCP(); MW_EA();
			//ADD_CYCLE(6);
			break;
		case	(byte)0xDF: // DCP $????,X
			//MR_AX(); DCP(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0xDB: // DCP $????,Y
			//MR_AY(); DCP(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0xC3: // DCP ($??,X)
			//MR_IX(); DCP(); MW_EA();
			//ADD_CYCLE(8);
			break;
		case	(byte)0xD3: // DCP ($??),Y
			//MR_IY(); DCP(); MW_EA();
			//ADD_CYCLE(8);
			break;

		case	(byte)0xE7: // ISB $??
			//MR_ZP(); ISB(); MW_ZP();
			//ADD_CYCLE(5);
			break;
		case	(byte)0xF7: // ISB $??,X
			//MR_ZX(); ISB(); MW_ZP();
			//ADD_CYCLE(5);
			break;
		case	(byte)0xEF: // ISB $????
			//MR_AB(); ISB(); MW_EA();
			//ADD_CYCLE(5);
			break;
		case	(byte)0xFF: // ISB $????,X
			//MR_AX(); ISB(); MW_EA();
			//ADD_CYCLE(5);
			break;
		case	(byte)0xFB: // ISB $????,Y
			//MR_AY(); ISB(); MW_EA();
			//ADD_CYCLE(5);
			break;
		case	(byte)0xE3: // ISB ($??,X)
			//MR_IX(); ISB(); MW_EA();
			//ADD_CYCLE(5);
			break;
		case	(byte)0xF3: // ISB ($??),Y
			//MR_IY(); ISB(); MW_EA();
			//ADD_CYCLE(5);
			break;

		case	(byte)0xBB: // LAS $????,Y
			//MR_AY(); LAS(); CHECK_EA();
			//ADD_CYCLE(4);
			break;


		case	(byte)0xA7: // LAX $??
			//MR_ZP(); LAX();
			//ADD_CYCLE(3);
			break;
		case	(byte)0xB7: // LAX $??,Y
			//MR_ZY(); LAX();
			//ADD_CYCLE(4);
			break;
		case	(byte)0xAF: // LAX $????
			//MR_AB(); LAX();
			//ADD_CYCLE(4);
			break;
		case	(byte)0xBF: // LAX $????,Y
			//MR_AY(); LAX(); CHECK_EA();
			//ADD_CYCLE(4);
			break;
		case	(byte)0xA3: // LAX ($??,X)
			//MR_IX(); LAX();
			//ADD_CYCLE(6);
			break;
		case	(byte)0xB3: // LAX ($??),Y
			//MR_IY(); LAX(); CHECK_EA();
			//ADD_CYCLE(5);
			break;

		case	(byte)0xAB: // LXA #$??
			//MR_IM(); LXA();
			//ADD_CYCLE(2);
			break;

		case	(byte)0x27: // RLA $??
			//MR_ZP(); RLA(); MW_ZP();
			//ADD_CYCLE(5);
			break;
		case	(byte)0x37: // RLA $??,X
			//MR_ZX(); RLA(); MW_ZP();
			//ADD_CYCLE(6);
			break;
		case	(byte)0x2F: // RLA $????
			//MR_AB(); RLA(); MW_EA();
			//ADD_CYCLE(6);
			break;
		case	(byte)0x3F: // RLA $????,X
			//MR_AX(); RLA(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0x3B: // RLA $????,Y
			//MR_AY(); RLA(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0x23: // RLA ($??,X)
			//MR_IX(); RLA(); MW_EA();
			//ADD_CYCLE(8);
			break;
		case	(byte)0x33: // RLA ($??),Y
			//MR_IY(); RLA(); MW_EA();
			//ADD_CYCLE(8);
			break;

		case	(byte)0x67: // RRA $??
			//MR_ZP(); RRA(); MW_ZP();
			//ADD_CYCLE(5);
			break;
		case	(byte)0x77: // RRA $??,X
			//MR_ZX(); RRA(); MW_ZP();
			//ADD_CYCLE(6);
			break;
		case	(byte)0x6F: // RRA $????
			//MR_AB(); RRA(); MW_EA();
			//ADD_CYCLE(6);
			break;
		case	(byte)0x7F: // RRA $????,X
			//MR_AX(); RRA(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0x7B: // RRA $????,Y
			//MR_AY(); RRA(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0x63: // RRA ($??,X)
			//MR_IX(); RRA(); MW_EA();
			//ADD_CYCLE(8);
			break;
		case	(byte)0x73: // RRA ($??),Y
			//MR_IY(); RRA(); MW_EA();
			//ADD_CYCLE(8);
			break;

		case	(byte)0x87: // SAX $??
			//MR_ZP(); SAX(); MW_ZP();
			//ADD_CYCLE(3);
			break;
		case	(byte)0x97: // SAX $??,Y
			//MR_ZY(); SAX(); MW_ZP();
			//ADD_CYCLE(4);
			break;
		case	(byte)0x8F: // SAX $????
			//MR_AB(); SAX(); MW_EA();
			//ADD_CYCLE(4);
			break;
		case	(byte)0x83: // SAX ($??,X)
			//MR_IX(); SAX(); MW_EA();
			//ADD_CYCLE(6);
			break;

		case	(byte)0xCB: // SBX #$??
			//MR_IM(); SBX();
			//ADD_CYCLE(2);
			break;

		case	(byte)0x9F: // SHA $????,Y
			//MR_AY(); SHA(); MW_EA();
			//ADD_CYCLE(5);
			break;
		case	(byte)0x93: // SHA ($??),Y
			//MR_IY(); SHA(); MW_EA();
			//ADD_CYCLE(6);
			break;

		case	(byte)0x9B: // SHS $????,Y
			//MR_AY(); SHS(); MW_EA();
			//ADD_CYCLE(5);
			break;

		case	(byte)0x9E: // SHX $????,Y
			//MR_AY(); SHX(); MW_EA();
			//ADD_CYCLE(5);
			break;

		case	(byte)0x9C: // SHY $????,X
			//MR_AX(); SHY(); MW_EA();
			//ADD_CYCLE(5);
			break;

		case	(byte)0x07: // SLO $??
			//MR_ZP(); SLO(); MW_ZP();
			//ADD_CYCLE(5);
			break;
		case	(byte)0x17: // SLO $??,X
			//MR_ZX(); SLO(); MW_ZP();
			//ADD_CYCLE(6);
			break;
		case	(byte)0x0F: // SLO $????
			//MR_AB(); SLO(); MW_EA();
			//ADD_CYCLE(6);
			break;
		case	(byte)0x1F: // SLO $????,X
			//MR_AX(); SLO(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0x1B: // SLO $????,Y
			//MR_AY(); SLO(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	(byte)0x03: // SLO ($??,X)
			//MR_IX(); SLO(); MW_EA();
			//ADD_CYCLE(8);
			break;
		case	(byte)0x13: // SLO ($??),Y
			//MR_IY(); SLO(); MW_EA();
			//ADD_CYCLE(8);
			break;

		case	(byte)0x47: // SRE $??
			//MR_ZP(); SRE(); MW_ZP();
			//ADD_CYCLE(5);
			break;
		case	(byte)0x57: // SRE $??,X
			//MR_ZX(); SRE(); MW_ZP();
			//ADD_CYCLE(6);
			break;
		case	0x4F: // SRE $????
			//MR_AB(); SRE(); MW_EA();
			//ADD_CYCLE(6);
			break;
		case	0x5F: // SRE $????,X
			//MR_AX(); SRE(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	0x5B: // SRE $????,Y
			//MR_AY(); SRE(); MW_EA();
			//ADD_CYCLE(7);
			break;
		case	0x43: // SRE ($??,X)
			//MR_IX(); SRE(); MW_EA();
			//ADD_CYCLE(8);
			break;
		case	0x53: // SRE ($??),Y
			//MR_IY(); SRE(); MW_EA();
			//ADD_CYCLE(8);
			break;

		case	(byte)0xEB: // SBC #$?? (Unofficial)
			//MR_IM(); SBC();
			//ADD_CYCLE(2);
			break;

		case	0x1A: // NOP (Unofficial)
		case	0x3A: // NOP (Unofficial)
		case	0x5A: // NOP (Unofficial)
		case	0x7A: // NOP (Unofficial)
		case	(byte)0xDA: // NOP (Unofficial)
		case	(byte)0xFA: // NOP (Unofficial)
			//ADD_CYCLE(2);
			break;
		case	(byte)0x80: // DOP (CYCLES 2)
		case	(byte)0x82: // DOP (CYCLES 2)
		case	(byte)0x89: // DOP (CYCLES 2)
		case	(byte)0xC2: // DOP (CYCLES 2)
		case	(byte)0xE2: // DOP (CYCLES 2)
			//R.PC++;
			//ADD_CYCLE(2);
			break;
		case	0x04: // DOP (CYCLES 3)
		case	0x44: // DOP (CYCLES 3)
		case	0x64: // DOP (CYCLES 3)
			//R.PC++;
			//ADD_CYCLE(3);
			break;
		case	0x14: // DOP (CYCLES 4)
		case	0x34: // DOP (CYCLES 4)
		case	0x54: // DOP (CYCLES 4)
		case	0x74: // DOP (CYCLES 4)
		case	(byte)0xD4: // DOP (CYCLES 4)
		case	(byte)0xF4: // DOP (CYCLES 4)
			//R.PC++;
			//ADD_CYCLE(4);
			break;
		case	0x0C: // TOP
		case	0x1C: // TOP
		case	0x3C: // TOP
		case	0x5C: // TOP
		case	0x7C: // TOP
		case	(byte)0xDC: // TOP
		case	(byte)0xFC: // TOP
			//R.PC+=2;
			//ADD_CYCLE(4);
			break;

		case	0x02:  /* JAM */
		case	0x12:  /* JAM */
		case	0x22:  /* JAM */
		case	0x32:  /* JAM */
		case	0x42:  /* JAM */
		case	0x52:  /* JAM */
		case	0x62:  /* JAM */
		case	0x72:  /* JAM */
		case	(byte)0x92:  /* JAM */
		case	(byte)0xB2:  /* JAM */
		case	(byte)0xD2:  /* JAM */
		case	(byte)0xF2:  /* JAM */
		default:
			break;
		}
	}
}
