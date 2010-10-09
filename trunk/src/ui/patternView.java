/*
 * a test class 
 */
package ui;

import core.*;


public class patternView {
	private nes _nes;
	
	private screenBuffer _buffer;
	
	public void setInfo(nes n, screenBuffer sb){
		_nes = n;
		_buffer = sb;
	}
	
	public void setBuffer(){
		memory mem = _nes.getMemory();
		byte[] chrBytes = mem._rom.chrRom;
		int[] scrInts = _buffer.getBufferDataInt();
		
//		for (int i = 0; i < chrBytes.length && i < scrInts.length; ++i){
//			//int index = i * 4;
//			scrInts[i] = chrBytes[i];
//		}
		int pitch = _buffer.getPitch();
		for (int i = 0, j = 0; i < chrBytes.length / 4 /*&& i < scrInts.length*/; ++i, ++j){
			int index = i * 4;
			scrInts[j] = chrBytes[index] | chrBytes[index + 1] << 8 |
						 chrBytes[index + 2] << 16 | chrBytes[index + 3] << 24;
			scrInts[j + pitch] = scrInts[j];
			scrInts[j + pitch * 2] = scrInts[j];
			scrInts[j + pitch * 3] = scrInts[j];
			
			if (i != 0 && i % pitch == 0)
				j += pitch * 3;
		}
	}
}
