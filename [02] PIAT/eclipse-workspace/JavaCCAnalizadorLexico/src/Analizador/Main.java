package Analizador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
	private static File file;

	public static void main(String args[]) {
		Main.getFile();
		if(file == null) {
			System.out.println("Invalid File or no file selected");
			System.exit(-1);
		}
		try {
			GramaticaTokenManager lexer = new GramaticaTokenManager(new SimpleCharStream(new FileReader(Main.file)));
			Token t;
			while((t = lexer.getNextToken()).kind != GramaticaTokenManager.EOF) {}
		
		System.out.println("Successfully");
		}catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	private final static void getFile() {
		JFileChooser search = new JFileChooser();
		FileNameExtensionFilter filter1 = new FileNameExtensionFilter("Archivos de código fuente", "cpp");
		FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Archivos de texto", "txt");
		search.setFileFilter(filter1);
		search.setFileFilter(filter2);
		int option = search.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			file = search.getSelectedFile();
		}
	}
}
