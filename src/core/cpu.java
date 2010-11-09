package core;

public class cpu {
	private cpuRegister _register = new cpuRegister();
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
		_register.PC = _cpuMemory.cpuReadWordFromMem(cpuRegister.RES_VECTOR);
		
	}
	
	private void setCpuFlag(boolean condition, byte flag){
		if (condition){
			_register.P |= flag;
		}
	}

	/// read method
	private void readImmdiate(){
		_byte_data = _cpuMemory.readByte(_register.PC++);
		_excutedCycles ++;
	}
	
	// zero page addressing
	private void readZeroPage(){
		_word_effectiveAddress = _cpuMemory.readByte(_register.PC++);
		_byte_data = _cpuMemory.cpuReadByteZeroPage((byte)_word_effectiveAddress);
		_excutedCycles += 2;
	}
	
	private void readZeroPageX(){
  		_word_effectiveAddress = _cpuMemory.readByte(_register.PC++);
  		_word_effectiveAddress += _register.X;
  		_byte_data = _cpuMemory.cpuReadByteZeroPage((byte)_word_effectiveAddress);
  		_excutedCycles += 3;
	}
	
	private void readZeroPageY(){
		_word_effectiveAddress = _cpuMemory.readByte(_register.PC++);
  		_word_effectiveAddress += _register.Y;
  		_byte_data = _cpuMemory.cpuReadByteZeroPage((byte)_word_effectiveAddress);
  		_excutedCycles += 3;
	}
	// end of zero page addressing
	
	//
	// if cross page, cpu cycle add 1
	//
	private void checkEA(){
		if ((_word_effectiveAddressTemp & 0xFF00) != (_word_effectiveAddress & 0xFF00))
			_excutedCycles += 1;
	}
	
	private void readAbsolute(){
		_word_effectiveAddress = _cpuMemory.cpuReadWordFromMem(_register.PC);
		_register.PC += 2;
		_byte_data = _cpuMemory.readByte(_word_effectiveAddress);
		_excutedCycles += 4;		
	}
	
	private void readAbsoluteX(){
		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordFromMem(_register.PC);
		_register.PC += 2;
		_word_effectiveAddress = (short)(_word_effectiveAddressTemp + _register.X);
		_byte_data = _cpuMemory.readByte(_word_effectiveAddress);
		_excutedCycles += 4;
		checkEA();
	}
	
	private void readAbsoluteY(){
		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordFromMem(_register.PC);
		_register.PC += 2;
		_word_effectiveAddress = (short)(_word_effectiveAddressTemp + _register.Y);
		_byte_data = _cpuMemory.readByte(_word_effectiveAddress);
		checkEA();
	}
	
//	private void readIndirect(){
//		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordFromMem(_register.PC);
//		_register.PC += 2;
//		_word_effectiveAddress = _cpuMemory.cpuReadWordFromMem(_word_effectiveAddressTemp);
//		_byte_data = _cpuMemory.readByte(_word_effectiveAddress);
//	}
	
	private void readIndexedIndirectX(){
		_byte_data = _cpuMemory.readByte(_register.PC++);
		_word_effectiveAddress = _cpuMemory.cpuReadWordZeroPage((byte)(_byte_data + _register.X));
		_byte_data = _cpuMemory.readByte(_word_effectiveAddress);
		_excutedCycles += 6;
	}
	
	private void readIndirectIndexedY(){
		_byte_data = _cpuMemory.readByte(_register.PC++);
		_word_effectiveAddressTemp = _cpuMemory.cpuReadWordZeroPage(_byte_data); // read from zero page
		_word_effectiveAddress = (short)(_word_effectiveAddressTemp + _register.Y);
		_byte_data = _cpuMemory.readByte(_word_effectiveAddress);
		_excutedCycles += 5;
		checkEA();
	}
	
	private void writeZeroPage(){
		_cpuMemory.cpuWriteByteToZeroPage((byte)_word_effectiveAddress, _byte_data);
	}
	
	private void writeEffectiveAddress(){
		_cpuMemory.cpuWriteByteToMem(_word_effectiveAddress, _byte_data);
	}
	//
	// set negative zero flag
	//
	private void setNZFlag(byte data){
		if (data == 0x0)
			_register.P |= cpuRegister.Z_FLAG;
		if ((data & 0x80) != 0x0)
			_register.P |= cpuRegister.N_FLAG;
	}

	private void cpuExecADC(){
		_word_temp = (short)(_byte_data + _register.A + (_register.P & cpuRegister.C_FLAG));
		setCpuFlag(_word_temp > (short)0xFF, cpuRegister.C_FLAG);
		// overflow .. set when negative + negtive = positive or postive + postive  = negative
		setCpuFlag(((_register.A ^ _word_temp) & (_byte_data ^ _word_temp) & 0x80) != 0, cpuRegister.V_FLAG);
		_register.A = (byte)_word_temp;
		setNZFlag(_register.A);
	}
	
	//
	// 
	//
	private void cpuExecSBC(){
		 _word_temp = (short) ((short)_register.A - (short)_byte_data - (short)(~(_register.P & cpuRegister.C_FLAG)));
		 
		 setCpuFlag(((_register.A ^ _byte_data) & (_register.A ^ _word_temp) & 0x80) != 0, cpuRegister.V_FLAG);
		 setCpuFlag(_word_temp < 0x100, cpuRegister.C_FLAG);
		 //
		 _register.A = (byte)_word_temp;
		 setNZFlag(_register.A);
	}
	
	private void cpuExecDec(){
		--_byte_data;
		setNZFlag(_byte_data);
	}
	
	private void cpuExecAsl(byte data){
		setCpuFlag((data & 0x80) != 0, cpuRegister.C_FLAG);
		data <<= 1;
		setNZFlag(data);
	}
	
	private void cpuExecBit(){
		setNZFlag((byte)(_byte_data & _register.A));
		setCpuFlag((_byte_data & 0x80) != 0, cpuRegister.N_FLAG);
		setCpuFlag((_byte_data & 0x40) != 0, cpuRegister.V_FLAG);
	}
	
	private void cpuExecEOR(){
		_register.A ^= _byte_data;
		setNZFlag(_register.A);
	}
	
	private void cpuExecLSR(byte data){
		setCpuFlag((data & 0x80) != 0, cpuRegister.C_FLAG);
		data >>= 1;
		setNZFlag(data);
	}
	
	private void cpuExecORA(){
		_register.A |= _byte_data;
		setNZFlag(_register.A);
	}
	
	private void cpuExecROL(byte data){
		if ((_register.P & cpuRegister.C_FLAG) != 0){
			setCpuFlag((data & 0x80) != 0, cpuRegister.C_FLAG);
			data = (byte)((data << 1) | 0x01);
		}
		else{
			setCpuFlag((data & 0x80) != 0, cpuRegister.C_FLAG);
			data <<= 1;
		}		
		setNZFlag(data);
	}
	
	private void cpuExecROR(byte data){
		if ((_register.P & cpuRegister.C_FLAG) != 0){
			setCpuFlag((data & 0x01) != 0, cpuRegister.C_FLAG);
			data = (byte)((data >> 1) | 0x80);
		}
		else{
			setCpuFlag((data & 0x80) != 0, cpuRegister.C_FLAG);
			data >>= 1;
		}		
		setNZFlag(data);	
	}
	
	private void cpuExecLDA(){
		_register.A = _byte_data;
		setNZFlag(_register.A);
	}
	
	private void cpuExecLDX(){
		_register.X = _byte_data;
		setNZFlag(_register.X);
	}
	
	private void cpuExecLDY(){
		_register.Y = _byte_data;
		setNZFlag(_register.Y);
	}
	
	private void cpuExecSTA(){
		_byte_data = _register.A;
	}
	
	private void cpuExecSTX(){
		_byte_data = _register.X;
	}
	
	private void cpuExecSTY(){
		_byte_data = _register.Y;
	}
	
	private void cpuExecTAX(){
		_register.X = _register.A;
		setNZFlag(_register.X);
	}
	private void cpuExecTXA(){
		_register.A = _register.X;
		setNZFlag(_register.A);
	}
	
	private void cpuExecTAY(){
		_register.Y = _register.A;
		setNZFlag(_register.Y);
	}
	
	private void cpuExecTYA(){
		_register.A = _register.Y;
		setNZFlag(_register.A);
	}
	
	private void cpuExecTSX(){
		_register.X = _register.SP;
		setNZFlag(_register.X);
	}
	
	private void cpuExecTXS(){
		_register.SP = _register.X;		
	}
	
	/*
	 * run cpu for certain cycles
	 */
	public void excute(int requestCycles){
		_excutedCycles = 0;
		
		byte opCode = 0;
		opCode = _cpuMemory.readByte(_register.PC++);
		
		switch(opCode){		
		// ADC #$??
		case	(byte)0x69: {
			readImmdiate();
			cpuExecADC();
			//_excutedCycles += 2;
		}
			break;
			// ADC $??
		case	(byte)0x65: {
			readZeroPage();
			cpuExecADC();
			//_excutedCycles += 3;
		}
			break;
			 // ADC $??,X
		case	(byte)0x75:{
			readZeroPageX();
			cpuExecADC();
			//_excutedCycles += 4;
		}
			break;
			// ADC $????
		case	(byte)0x6D: {
			readAbsolute();
			cpuExecADC();
			//_excutedCycles += 4;
		}
			break;
			// ADC $????,X
		case	(byte)0x7D: {
			readAbsoluteX();
			cpuExecADC();
			//checkEA();
			//_excutedCycles += 4;
		}
			break;
			 // ADC $????,Y
		case	(byte)0x79:{
			readAbsoluteY();
			cpuExecADC();
			//checkEA();
			//_excutedCycles += 4;
		}
			break;
			// ADC ($??,X)
		case	(byte)0x61: {
			readIndexedIndirectX();
			cpuExecADC();
			//_excutedCycles += 6;
		}
			break;
			// ADC ($??),Y
		case	(byte)0x71: {
			readIndirectIndexedY();
			cpuExecADC();
		}
			break;
			// SBC #$??
		case	(byte)0xE9: {
			readImmdiate();
			cpuExecSBC();			
		}
			break;
			// SBC $??
		case	(byte)0xE5: {
			readZeroPage();
			cpuExecSBC();			
		}
			break;
			// SBC $??,X
		case	(byte) 0xF5: {
			readZeroPageX();
			cpuExecSBC();
		}
			break;
			// SBC $????
		case	(byte)0xED:{
			readAbsolute();
			cpuExecSBC();
		}
			break;
			// SBC $????,X
		case	(byte)0xFD: {
			readAbsoluteX();
			cpuExecSBC();
		}
			break;
			// SBC $????,Y
		case	(byte)0xF9: {
			readAbsoluteY();
			cpuExecSBC();
		}
			break;
			// SBC ($??,X)
		case	(byte)0xE1: {
			readIndexedIndirectX();
			cpuExecSBC();
		}
			break;
			// SBC ($??),Y
		case	(byte)0xF1: {
			readIndirectIndexedY();
			cpuExecSBC();			
		}
			break;			
			// DEC $??
		case	(byte)0xC6: {
			readZeroPage();
			cpuExecDec();
			writeZeroPage();
			//_excutedCycles += 5;
		}
			break;
			// DEC $??,X
		case	(byte)0xD6: {
			readZeroPageX();
			cpuExecDec();
			writeZeroPage();
			//_excutedCycles += 6;
		}
			break;
			// DEC $????
		case	(byte)0xCE:{
			readAbsolute();
			cpuExecDec();
			writeEffectiveAddress();
			//_excutedCycles += 6;
		}
			break;
			// DEC $????,X
		case	(byte)0xDE:{
			readAbsoluteX();
			cpuExecDec();
			writeEffectiveAddress();
			//_excutedCycles += 7;
		}
			break;
			// DEX
		case	(byte)0xCA:{
			setNZFlag(--_register.X);
			_excutedCycles += 2;
		}
			break;
			// DEY
		case	(byte)0x88: {
			setNZFlag(--_register.Y);
			_excutedCycles += 2;
		}
			break;
			// INC $??
		case	(byte)0xE6: {
			readZeroPage();
			setNZFlag(++_byte_data);
			writeZeroPage();
			//_excutedCycles += 5;
		}
			break;
			// INC $??,X
		case	(byte)0xF6: {
			readZeroPageX();
			setNZFlag(++_byte_data);
			writeZeroPage();
			//_excutedCycles += 6;
		}
			break;
			// INC $????
		case	(byte)0xEE:{
			readAbsolute();
			setNZFlag(++_byte_data);
			writeEffectiveAddress();
			//_excutedCycles += 6;
		}
			break;
			// INC $????,X
		case	(byte)0xFE: {
			readAbsoluteX();
			setNZFlag(++_byte_data);
			writeEffectiveAddress();
			//_excutedCycles += 7;
		}
			break;
			// INX
		case	(byte)0xE8: {
			setNZFlag(++_register.X);
			_excutedCycles += 2;
		}
			break;
			// INY
		case	(byte)0xC8: {
			setNZFlag(++_register.Y);
			_excutedCycles += 2;
		}
			break;
			// AND #$??
		case	(byte)0x29: {
			readImmdiate();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			_excutedCycles += 2;
		}
			break;
			// AND $??
		case	(byte)0x25: {
			readZeroPage();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			_excutedCycles += 3;
		}
			break;
			// AND $??,X
		case	(byte)0x35: {
			readZeroPageX();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			_excutedCycles += 4;
		}
			break;
			// AND $????
		case	(byte)0x2D: {
			readAbsolute();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			_excutedCycles += 4;
		}
			break;
			// AND $????,X
		case	(byte)0x3D: {
			readAbsoluteX();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			checkEA();
			_excutedCycles += 4;
		}
			break;
			// AND $????,Y
		case	(byte)0x39: {
			readAbsoluteY();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			checkEA();
			_excutedCycles += 4;
		}
			break;
			// AND ($??,X)
		case	(byte)0x21:{
			readIndexedIndirectX();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			_excutedCycles += 6;
		}
			break;
			// AND ($??),Y
		case	(byte)0x31: {
			readIndirectIndexedY();
			_register.A &= _byte_data;
			setNZFlag(_register.A);
			checkEA();
			_excutedCycles += 6;
		}
			break;						
		case	(byte)0x0A: // ASL A
			cpuExecAsl(_register.A);
			_excutedCycles += 2;		
			break;
		case	(byte)0x06: // ASL $??
			readZeroPage();
			cpuExecAsl(_byte_data);
			writeZeroPage();
			break;
		case	(byte)0x16: // ASL $??,X
			readZeroPageX();
			cpuExecAsl(_byte_data);
			writeEffectiveAddress();
			break;
		case	(byte)0x0E: // ASL $????
			readAbsolute();
			cpuExecAsl(_byte_data);
			writeEffectiveAddress();
			break;
		case	(byte)0x1E: // ASL $????,X
			readAbsoluteX();
			cpuExecAsl(_byte_data);
			writeEffectiveAddress();
			break;
		case	(byte)0x24: // BIT $??
			readZeroPage();
			cpuExecBit();
			break;
		case	(byte)0x2C: // BIT $????
			readAbsolute();
			cpuExecBit();
			break;
		case	(byte)0x49: // EOR #$??
			readImmdiate();
			cpuExecEOR();
			break;
		case	(byte)0x45: // EOR $??
			readZeroPage();
			cpuExecEOR();
			break;
		case	(byte)0x55: // EOR $??,X
			readZeroPageX();
			cpuExecEOR();
			break;
		case	(byte)0x4D: // EOR $????
			readAbsolute();
			cpuExecEOR();
			break;
		case	(byte)0x5D: // EOR $????,X
			readAbsoluteX();
			cpuExecEOR();
			break;
		case	(byte)0x59: // EOR $????,Y
			readAbsoluteY();
			cpuExecEOR();
			break;
		case	(byte)0x41: // EOR ($??,X)
			readIndexedIndirectX();
			cpuExecEOR();
			break;
		case	(byte)0x51: // EOR ($??),Y
			readIndirectIndexedY();
			cpuExecEOR();
			break;
		case	(byte)0x4A: // LSR A
			cpuExecLSR(_register.A);
			break;
		case	(byte)0x46: // LSR $??
			readZeroPage();
			cpuExecLSR(_byte_data);
			break;
		case	(byte)0x56: // LSR $??,X
			readZeroPageX();
			cpuExecLSR(_byte_data);
			break;
		case	(byte)0x4E: // LSR $????
			readAbsolute();
			cpuExecLSR(_byte_data);
			break;
		case	(byte)0x5E: // LSR $????,X
			readAbsoluteY();
			cpuExecLSR(_byte_data);
			break;
		case	(byte)0x09: // ORA #$??
			readImmdiate();
			cpuExecORA();
			break;
		case	(byte)0x05: // ORA $??
			readZeroPage();
			cpuExecORA();
			break;
		case	(byte)0x15: // ORA $??,X
			readZeroPageX();
			cpuExecORA();
			break;
		case	(byte)0x0D: // ORA $????
			readAbsolute();
			cpuExecORA();
			break;
		case	(byte)0x1D: // ORA $????,X
			readAbsoluteX();
			cpuExecORA();
			break;
		case	(byte)0x19: // ORA $????,Y
			readAbsoluteY();
			cpuExecORA();
			break;
		case	(byte)0x01: // ORA ($??,X)
			readIndexedIndirectX();
			cpuExecORA();
			break;
		case	(byte)0x11: // ORA ($??),Y
			readIndirectIndexedY();
			cpuExecORA();
			break;
		case	(byte)0x2A: // ROL A
			cpuExecROL(_register.A);
			break;
		case	(byte)0x26: // ROL $??
			readZeroPage();
			cpuExecROL(_byte_data);
			break;
		case	(byte)0x36: // ROL $??,X
			readZeroPageX();
			cpuExecROL(_byte_data);
			break;
		case	(byte)0x2E: // ROL $????
			readAbsolute();
			cpuExecROL(_byte_data);	
			break;
		case	(byte)0x3E: // ROL $????,X
			readAbsoluteX();
			cpuExecROL(_byte_data);
			break;
		case	(byte)0x6A: // ROR A
			cpuExecROR(_register.A);
			break;
		case	(byte)0x66: // ROR $??
			readZeroPage();
			cpuExecROR(_byte_data);
			break;
		case	(byte)0x76: // ROR $??,X
			readZeroPageX();
			cpuExecROR(_byte_data);
			break;
		case	(byte)0x6E: // ROR $????
			readAbsolute();
			cpuExecROR(_byte_data);
			break;
		case	(byte)0x7E: // ROR $????,X
			readAbsoluteX();
			cpuExecROR(_byte_data);
			break;
		case	(byte)0xA9: // LDA #$??
			readImmdiate();
			cpuExecLDA();
			break;
		case	(byte)0xA5: // LDA $??
			readZeroPage();
			cpuExecLDA();
			break;
		case	(byte)0xB5: // LDA $??,X
			readZeroPageX();
			cpuExecLDA();
			break;
		case	(byte)0xAD: // LDA $????
			readAbsolute();
			cpuExecLDA();
			break;
		case	(byte)0xBD: // LDA $????,X
			readAbsoluteX();
			cpuExecLDA();
			break;
		case	(byte)0xB9: // LDA $????,Y
			readAbsoluteY();
			cpuExecLDA();
			break;
		case	(byte)0xA1: // LDA ($??,X)
			readIndexedIndirectX();
			cpuExecLDA();
			break;
		case	(byte)0xB1: // LDA ($??),Y
			readIndirectIndexedY();
			cpuExecLDA();
			break;
		case	(byte)0xA2: // LDX #$??
			readImmdiate();
			cpuExecLDX();
			break;
		case	(byte)0xA6: // LDX $??
			readZeroPage();
			cpuExecLDX();
			break;
		case	(byte)0xB6: // LDX $??,Y
			readZeroPageY();		
			cpuExecLDX();
			break;
		case	(byte)0xAE: // LDX $????
			readAbsolute();		
			cpuExecLDX();
			break;
		case	(byte)0xBE: // LDX $????,Y
			readAbsoluteY();
			cpuExecLDX();
			break;
		case	(byte)0xA0: // LDY #$??
			readImmdiate();
			cpuExecLDY();
			break;
		case	(byte)0xA4: // LDY $??
			readZeroPage();
			cpuExecLDY();
			break;
		case	(byte)0xB4: // LDY $??,X
			readZeroPageX();
			cpuExecLDY();
			break;
		case	(byte)0xAC: // LDY $????
			readAbsolute();
			cpuExecLDY();
			break;
		case	(byte)0xBC: // LDY $????,X
			readAbsoluteX();
			cpuExecLDY();
			break;
		case	(byte)0x85: // STA $??
			readZeroPage();
			cpuExecSTA();	
			writeZeroPage();
			break;
		case	(byte)0x95: // STA $??,X
			readZeroPageX();
			cpuExecSTA();	
			writeZeroPage();
			break;
		case	(byte)0x8D: // STA $????
			readAbsolute();
			cpuExecSTA();	
			writeEffectiveAddress();
			break;
		case	(byte)0x9D: // STA $????,X
			readAbsoluteX();
			cpuExecSTA();	
			writeEffectiveAddress();
			break;
		case	(byte)0x99: // STA $????,Y
			readAbsoluteY();
			cpuExecSTA();	
			writeEffectiveAddress();
			break;
		case	(byte)0x81: // STA ($??,X)
			readIndexedIndirectX();
			cpuExecSTA();	
			writeEffectiveAddress();
			break;
		case	(byte)0x91: // STA ($??),Y
			readIndirectIndexedY();
			cpuExecSTA();	
			writeEffectiveAddress();
			break;

		case	(byte)0x86: // STX $??
			readZeroPage();
			cpuExecSTX();	
			writeZeroPage();
			break;
		case	(byte)0x96: // STX $??,Y
			readZeroPageY();
			cpuExecSTX();	
			writeZeroPage();
			break;
		case	(byte)0x8E: // STX $????
			readAbsolute();
			cpuExecSTX();	
			writeEffectiveAddress();
			break;

		case	(byte)0x84: // STY $??
			readZeroPage();
			cpuExecSTY();	
			writeZeroPage();
			break;
		case	(byte)0x94: // STY $??,X
			readZeroPageX();
			cpuExecSTY();	
			writeZeroPage();
			break;
		case	(byte)0x8C: // STY $????
			readAbsolute();
			cpuExecSTY();	
			writeEffectiveAddress();
			break;

		case	(byte)0xAA: // TAX
			cpuExecTAX();
			_excutedCycles += 2;
			break;
		case	(byte)0x8A: // TXA
			cpuExecTXA();
			_excutedCycles += 2;
			break;
		case	(byte)0xA8: // TAY
			cpuExecTAY();
			_excutedCycles += 2;
			break;
		case	(byte)0x98: // TYA
			cpuExecTYA();
			_excutedCycles += 2;
			break;
		case	(byte)0xBA: // TSX
			cpuExecTSX();
			_excutedCycles += 2;
			break;
		case	(byte)0x9A: // TXS
			cpuExecTXS();
			_excutedCycles += 2;
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
			_register.P |= cpuRegister.I_FLAG;
			_excutedCycles += 2;
		}
			break;

// スタック系
		case	(byte)0x48: // PHA
			
			break;
		case	(byte)0x08: // PHP
			
			break;
		case	(byte)0x68: // PLA (N-----Z-)
			
			break;
		case	(byte)0x28: // PLP
			
			break;

// その他
		case	(byte)0x00: // BRK
			
			break;

		case	(byte)0xEA: // NOP
			//ADD_CYCLE(2);
			break;

// 未公開命令群
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
			_excutedCycles += 2;
			break;
		case	(byte)0x80: // DOP (CYCLES 2)
		case	(byte)0x82: // DOP (CYCLES 2)
		case	(byte)0x89: // DOP (CYCLES 2)
		case	(byte)0xC2: // DOP (CYCLES 2)
		case	(byte)0xE2: // DOP (CYCLES 2)
			_register.PC ++;
			_excutedCycles += 2;
			break;
		case	0x04: // DOP (CYCLES 3)
		case	0x44: // DOP (CYCLES 3)
		case	0x64: // DOP (CYCLES 3)
			_register.PC ++;
			_excutedCycles += 3;
			break;
		case	0x14: // DOP (CYCLES 4)
		case	0x34: // DOP (CYCLES 4)
		case	0x54: // DOP (CYCLES 4)
		case	0x74: // DOP (CYCLES 4)
		case	(byte)0xD4: // DOP (CYCLES 4)
		case	(byte)0xF4: // DOP (CYCLES 4)
			_register.PC ++;
			_excutedCycles += 4;
			break;
		case	0x0C: // TOP
		case	0x1C: // TOP
		case	0x3C: // TOP
		case	0x5C: // TOP
		case	0x7C: // TOP
		case	(byte)0xDC: // TOP
		case	(byte)0xFC: // TOP
			_register.PC += 2;
			_excutedCycles += 4;
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
