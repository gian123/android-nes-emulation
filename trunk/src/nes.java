import java.io.DataInputStream;
import java.io.FileInputStream;


public class nes {
	private memory _memory = new memory();
	private cpu _cpu = new cpu();
	
	public void reset(){
		_cpu.setMemory(_memory);
		_cpu.reset();
	}
	
	public void readFromFile(String fileName){
		
		romHeader header = new romHeader();
		try {
			FileInputStream input = new FileInputStream(fileName);
			DataInputStream dataInput = new DataInputStream(input);
			dataInput.read(header.nesId);
			header.prgRomCount = dataInput.readByte();
			header.chrRomCount = dataInput.readByte();
			header.ctrlByte1 = dataInput.readByte();
			header.ctrlByte2 = dataInput.readByte();
			header.ram8kCount = dataInput.readByte();
			dataInput.read(header.reserved);
			
			if (header.nesId[0] != 'N' || header.nesId[1] != 'E' ||
				header.nesId[2] != 'S' || header.nesId[3] != 0x1a){
				// bad file
				return;
			}
			
			rom retRom = new rom();
			retRom.prg_16k_count = header.prgRomCount;
			retRom.chr_8k_count = header.chrRomCount;
			retRom.mapperId = (header.ctrlByte1 & 0xf0) >> 4 | (header.ctrlByte2 & 0xf0);
			
			retRom.prgRom = new byte[header.prgRomCount * 16 * 1024];
			retRom.chrRom = new byte[header.chrRomCount * 8 * 1024];
			
			if ((header.ctrlByte1 & 0x4) != 0){
				// has trainer
				retRom.trainer = new byte[512];
				dataInput.read(retRom.trainer);
			}
			dataInput.read(retRom.prgRom);
			dataInput.read(retRom.chrRom);
			
			_memory._rom = retRom;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void emulationStart(){
		reset();
		while(true){
			_cpu.excute(10);
		}
		
	}
}
