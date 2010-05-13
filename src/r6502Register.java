
public class r6502Register {
	public static short NMI_VECTOR = (short)0xFFFA;
	public static short RES_VECTOR = (short)0xFFFC;
	public static short IRQ_VECTOR = (short)0xFFFE;
	
	public short PC; 		// program counter
	public byte SP = (byte)0xFF; // stack pointer
	
	public byte A = 0; 	// accumulator
	public byte X = 0; 	// index register x
	public byte Y = 0; 	// index register y
	public byte P; 		// process status 
	
	public enum PROCESS_FLAG{
		C_FLAG   	((byte)0x01),		// 1: Carry
		Z_FLAG		((byte)0x02),		// 1: Zero
		I_FLAG		((byte)0x04),		// 1: Irq disabled
		D_FLAG		((byte)0x08),		// 1: Decimal mode flag (NES unused)
		B_FLAG		((byte)0x10),		// 1: Break
		R_FLAG		((byte)0x20),		// 1: Reserved (Always 1)
		V_FLAG		((byte)0x40),		// 1: Overflow
		N_FLAG		((byte)0x80);		// 1: Negative
		
		private byte _value;
		PROCESS_FLAG(byte a){
			_value = a;
		}
	}
	
	public void reset(){
		A = 0;
		SP = (byte)0xFF;
		X = 0;
		Y = 0;
		P = (byte) (PROCESS_FLAG.Z_FLAG._value | PROCESS_FLAG.R_FLAG._value);
		//PC =     ? how to init PC
	}
}
