package ui;
import java.util.Timer;

import core.nes;

public class app {
	public static void main(String[] argv){
		//Timer timer = new Timer();
		nes g_nes = new nes();
		g_nes.readFromFile("mario.nes");
		
		window wnd = new window();
		wnd.setNes(g_nes);
		wnd.initGL();
		
		wnd.showWindow();
		
//		nes g_nes = new nes();
//		g_nes.readFromFile("mario.nes");
//		//g_nes.reset();
//		g_nes.emulationStart();
//		//rom g_rom = g_nes.readFromFile("mario.nes");
	}
}
