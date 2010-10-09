package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class screenBuffer {
	private final int WIDTH = 256;
	private final int HEIGHT = 240;
	
	private BufferedImage _buffer;
	
	public screenBuffer(){
		_buffer = new BufferedImage(WIDTH, HEIGHT, 1);
		
	}
	
	public int getPitch(){
		return WIDTH;
	}
	
	public void clear(int rgb){
		for (int w = 0; w < WIDTH; ++w)
			for (int h = 0; h < HEIGHT; ++h)
				_buffer.setRGB(w, h, rgb);
	}
	
	public int[] getBufferDataInt(){
		DataBufferInt bufferInt = (DataBufferInt)_buffer.getRaster().getDataBuffer();
		int[] ppuBuffer = bufferInt.getData();
		return ppuBuffer;
	}
	
	public byte[] getBufferDataByte(){
		DataBufferByte bufferbyte = (DataBufferByte)_buffer.getRaster().getDataBuffer();
		byte[] ppuBuffer = bufferbyte.getData();
		return ppuBuffer;
	}
	
	public void paint(Graphics gfx){
		gfx.drawImage(_buffer, 0, 0, null);
	}
}
