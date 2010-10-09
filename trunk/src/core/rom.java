package core;

public class rom {
	// program ROM, store program instruction for execution
	public byte[] prgRom = null;
	// character ROM, store the character pattern
	public byte[] chrRom = null;
	public byte[] trainer = null;
	
	public int mapperId = 0;
	public int prg_16k_count = 0;
	public int chr_8k_count = 0;
}
