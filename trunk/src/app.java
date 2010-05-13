
public class app {
	public static void main(String[] argv){
		
		nes g_nes = new nes();
		g_nes.readFromFile("mario.nes");
		g_nes.reset();
		//rom g_rom = g_nes.readFromFile("mario.nes");
	}
}
