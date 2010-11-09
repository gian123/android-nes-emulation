package core;

public class cpuRegister {
	public final static short NMI_VECTOR = (short)0xFFFA;
	public final static short RES_VECTOR = (short)0xFFFC;
	public final static short IRQ_VECTOR = (short)0xFFFE;
	
	public final static byte C_FLAG = 0x01;			// 1: Carry
	public final static byte Z_FLAG = 0X02;			// 1: Zero
	public final static byte I_FLAG = 0X04;			// 1: Irq_disable
	public final static byte D_FLAG = 0X08;			// 1: Decimal mode flag (NES unused)
	public final static byte B_FLAG = 0X10;			// 1: Break
	public final static byte R_FLAG = 0X20;			// 1: Reserved (Always 1)
	public final static byte V_FLAG = 0X40;		  	// 1: Overflow
	public final static byte N_FLAG = (byte)0X80; 	// 1: Negative
	
	public short PC; 		// program counter
	public byte SP = (byte)0xFF; // stack pointer
	
	public byte A = 0; 	// accumulator
	public byte X = 0; 	// index register x
	public byte Y = 0; 	// index register y
	public byte P; 		// process status 
	
	public boolean FC;
	public boolean FZ;
	public boolean FI;
	public boolean FD;
	public boolean FB;
	public boolean FR;
	public boolean FV;
	public boolean FN;

	public void reset(){
		A = 0;
		SP = (byte)0xFF;
		X = 0;
		Y = 0;
		P = (byte) (Z_FLAG | R_FLAG);
		
		FZ = true;
		FR = true;
		
	}
}
