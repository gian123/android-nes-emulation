package core;

public class r6502Register {
	public final short NMI_VECTOR = (short)0xFFFA;
	public final short RES_VECTOR = (short)0xFFFC;
	public final short IRQ_VECTOR = (short)0xFFFE;
	
	public final byte C_FLAG = 0x01;		// 1: Carry
	public final byte Z_FLAG = 0X02;		// 1: Zero
	public final byte I_FLAG = 0X04;		// 1: Irq_disable
	public final byte D_FLAG = 0X08;		// 1: Decimal mode flag (NES unused)
	public final byte B_FLAG = 0X10;		// 1: Break
	public final byte R_FLAG = 0X20;		// 1: Reserved (Always 1)
	public final byte V_FLAG = 0X40;		// 1: Overflow
	public final byte N_FLAG = (byte)0X80; // 1: Negative
	
	public short PC; 		// program counter
	public byte SP = (byte)0xFF; // stack pointer
	
	public byte A = 0; 	// accumulator
	public byte X = 0; 	// index register x
	public byte Y = 0; 	// index register y
	public byte P; 		// process status 

	public void reset(){
		A = 0;
		SP = (byte)0xFF;
		X = 0;
		Y = 0;
		P = (byte) (Z_FLAG | R_FLAG);
		//PC =     ? how to init PC
	}
}
