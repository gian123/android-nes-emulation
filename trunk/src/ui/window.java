package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class window {
	private JFrame _mainFrame =  new JFrame();
	private JPanel _panel = new JPanel();
	private screenBuffer _screenBuffer = new screenBuffer();
	
	public window(){
		_screenBuffer.clear((int)0x00ff00ff);
	}
	
	public void showWindow(){
		_mainFrame.setBounds(100, 100, 500, 500);
		_mainFrame.setBackground(Color.BLACK);
		_mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_mainFrame.add(_panel);
		_mainFrame.setVisible(true);
		_mainFrame.show();
	}
	
	public void paint(){
		_screenBuffer.paint(_panel.getGraphics());
	}
	
	public void drawLines(){
		try{
			Graphics gfx = _panel.getGraphics();
			gfx.setColor(Color.black);
			gfx.clearRect(0, 0, _panel.getWidth(), _panel.getHeight());
			gfx.setColor(Color.white);
			gfx.drawLine(0, 0, 100, 100);
		}
		catch(Exception ex){
			String str = ex.toString();
			str.charAt(0);
		}
	}
}
