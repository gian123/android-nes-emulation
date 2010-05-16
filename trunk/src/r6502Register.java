
public class r6502Register {
	public static short NMI_VECTOR = (short)0xFFFA;
	public static short RES_VECTOR = (short)0xFFFC;
	public static short IRQ_VECTOR = (short)0xFFFE;
	
	public static byte C_FLAG = 0x01;		// 1: Carry
	public static byte Z_FLAG = 0X02;		// 1: Zero
	public static byte I_FLAG = 0X04;		// 1: Irq_disable
	public static byte D_FLAG = 0X08;		// 1: Decimal mode flag (NES unused)
	public static byte B_FLAG = 0X10;		// 1: Break
	public static byte R_FLAG = 0X20;		// 1: Reserved (Always 1)
	public static byte V_FLAG = 0X40;		// 1: Overflow
	public static byte N_FLAG = (byte)0X80; // 1: Negative
	
	public short PC; 		// program counter
	public byte SP = (byte)0xFF; // stack pointer
	
	public byte A = 0; 	// accumulator
	public byte X = 0; 	// index register x
	public byte Y = 0; 	// index register y
	public byte P; 		// process status 
	/*
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
	*/
	public void reset(){
		A = 0;
		SP = (byte)0xFF;
		X = 0;
		Y = 0;
		P = (byte) (Z_FLAG | R_FLAG);
		//PC =     ? how to init PC
	}
}
