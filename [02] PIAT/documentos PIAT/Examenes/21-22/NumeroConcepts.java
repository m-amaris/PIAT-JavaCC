package piat.examen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergio Delgado Cantalapiedra 53748114N
 *
 */

public class NumeroConcepts {


	public static void main(String[] args) {

		final File fCatalog=verificarArgumentos(args);

		// Obtener los concepts de primer nivel que tienen al menos un concept de tercer nivel:
		final Map<String, Integer> cuentasNivelesConcepts = NumeroConcepts.cuentasConcepts(fCatalog);

		// Mostrar por la salida estandar el contenido del mapa
		for (Map.Entry<String, Integer> entrada : cuentasNivelesConcepts.entrySet()) 
			System.out.printf("%-20s %5d %n", entrada.getKey()+":",entrada.getValue());

	}


	public static Map <String,Integer> cuentasConcepts (File catalogo){

		Map<String, Integer> cuentasConcepts = new HashMap <String,Integer>(); 

		try {
			BufferedReader brInput = new BufferedReader(new FileReader(catalogo));
			try{
				String linea;
				while ( (linea = brInput.readLine())!= null){
					procesarLinea(linea,cuentasConcepts);
				}
			}catch(Exception e){
				System.err.println("ERROR (leyendo el fichero): "+ e.getMessage());
			}finally{
				try {
					brInput.close();

				} catch (IOException e) {
					System.err.println("ERROR (cerrando el fichero)" + e.getMessage());
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("ERROR (fichero no encontrado): "+ e.getMessage());
		}

		return cuentasConcepts;
	}

	/* Procesa una línea del fichero. Si esa línea se corresponde con uno de los elementos concept buscados
	 * evaluará mediante una expresión regular si es de tercer nivel. Si es así agregará en el mapa un  elemento
	 * cuya clave es el nombre del primer (que obtendrá de la línea mediante la expresión regular) y valor un 1. Si el 
	 * elemento ya existe en el mapa incrementará el valor
	 */
	public static void procesarLinea(String sLine, Map<String, Integer> cuentasConcepts){
		// TODO:
		Pattern p = Pattern.compile("^<concept id=\\\"https://datos\\.madrid\\.es/egob/kos/\\w+/\\w+/\\w+/{0,1}\\\"\\s/>$");
		Matcher m = p.matcher(sLine);
		
		if (m.matches()) {
			
		}
			
				
		
		
		
		
		
		
		
		
	}

	private static File verificarArgumentos(String[] args) {
		// Verificar nº de argumentos correcto
		if (args.length!=1){
			Class<? extends Object> thisClass = new Object(){}.getClass();
			System.err.println(
					"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <ficheroCatalogo>\n" +
							"donde:\n"+
							"\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n"
					);
			System.exit(1);
		}
		// Verificar que el fichero de entrada existe y es legible
		File fCatalog = new File (args[0]);
		if (!fCatalog.isFile() || !fCatalog.canRead()) {
			System.err.println ("Error: el fichero de catálogo '" + args[0]+"' no existe o no es posible leerlo!");
			System.exit(1);
		}
		return fCatalog;
	}	

}
