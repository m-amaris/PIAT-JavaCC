package piat.regExp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author 
 *
 */
public class EstadisticasLog {

	private final static int numMaxMsg = 500;	// NÂº mÃ¡ximo de mensajes enviados por un usuario para mostrarlo en las estadÃ­sticas
	
	// Expresiones regulares para la fecha y la hora
	private final static String FECHA = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
	private final static String HORA = "[0-9]{2}:[0-9]{2}:[0-9]{2}";
	// Expresiones regulares para el nombre del servidor. El nombre del servidor estÃ¡ formado por el tipo + un nÃºmero. Ejemplo smtp-in3
	private final static String TIPO_SERVIDOR = "[^0-9]+";
	private final static String NUMERO_SERVIDOR = "[0-9]+";
	
	// PatrÃ³n de una traza cualquiera correcta de la que podemos extraer, en el grupo 1, el nombre del servidor
	//TODO: Modificar este patrÃ³n para que tenga mÃ¡s grupos y asÃ­ se pueda extraer mÃ¡s informaciÃ³n y no solo el nombre del servidor
	private final static String patronTraza = "^("+FECHA+")\\s+" + HORA + "\\s+((" + TIPO_SERVIDOR+ ")" + NUMERO_SERVIDOR+")+\\s+\\[\\w+\\]:\\s.*";
	
	/* Patrones que se usan en las estadÃ­sticas agregadas */
	private final static String msgIn = "^"+FECHA+"\\s+" + HORA + "\\s+(smtp-in" + NUMERO_SERVIDOR+")+\\s+\\[\\w+\\]:\\s(message\\sfrom:)\\s.*";
	private final static String msgOut = "^"+FECHA+"\\s+" + HORA + "\\s+(" + "smtp-out" + NUMERO_SERVIDOR+")+\\s+\\[\\w+\\]:\\s(message\\sfrom:)\\s.*";	
	private final static String msgINFECTED = ".*INFECTED.*";	
	private final static String msgSPAM = ".*security-in"+NUMERO_SERVIDOR+".*SPAM.*";	
	private final static String code511 = ".*5.1.1\\s*\\(bad\\sdestination\\smailbox\\saddress\\).*";	
	private final static String code432 = ".*4.3.2\\s*\\(overload\\).*";	
	
	public static void main(String[] args) throws InterruptedException {
	    Thread.currentThread().setName("Principal");
	    // Verificar que se pasa como argumento el directorio con los logs y obtenerlo
	    File directorioFuente=validarArgumentos(args);
		
		// En este array bidimensional se almacenan los nombres de los estadÃ­sticos a obtener y el patrÃ³n para que luego sea mas fÃ¡cil recorrerlo 
		// y meter sus valores en el mapa hmPatronesEstadisticasAgregadas 
		final String[][] patronesEstadisticasAgregadas= {
							{"msgIn",msgIn},
							{"msgOut",msgOut},
							{"msgINFECTED",msgINFECTED},
							{"msgSPAM",msgSPAM},
							{"code 5.1.1",code511},
							{"code 4.3.2",code432},				
						};
		
		
		// Mapa donde se guarda como clave el nombre de la estadÃ­stica agregada, y como valor, el patrÃ³n para detectar el String de la traza que contiene ese estadÃ­stico
		final ConcurrentHashMap <String,Pattern> hmPatronesEstadisticasAgregadas = new ConcurrentHashMap <String,Pattern> ();
		
		// Guardar en el mapa hmPatronesEstadisticasAgregadas todos los patrones que se usarÃ¡n para las estadÃ­sticas agregadas.
		// Los patrones y el nombre del estadÃ­stico estÃ¡n en el array bidimensional patronesEstadisticasAgregadas
		Pattern patron;
		String estadistico;		
		for (int i= 0; i < patronesEstadisticasAgregadas.length;i++) {
			estadistico=patronesEstadisticasAgregadas[i][0];
			patron = Pattern.compile(patronesEstadisticasAgregadas[i][1]);
			hmPatronesEstadisticasAgregadas.put(estadistico, patron);
		}		
		
		// Mapa donde recoger los contadores de cada estadÃ­sticas agregada.
		// La clave indica el servidor, la fecha y el estadÃ­stico, por lo que estarÃ¡ formado por una String con esta estructura: "<tipoServidor> <fecha> <estadÃ­stico>"
		// El valor es el contador de las veces que se encuentra el estadÃ­stico anterior
		final ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas = new ConcurrentHashMap <String,AtomicInteger>();	

		// Mapa donde guardar el nombre del servidor y el tipo de servidor
		final ConcurrentHashMap<String, String> hmServidores = new ConcurrentHashMap <String,String>();
		
		// Mapa donde se guarda como clave el nombre de un usuario y como valor un contador, para poder guardar cuantos mensajes envÃ­an los usuarios del sistema
		final ConcurrentHashMap <String,AtomicInteger> hmUsuarios = new ConcurrentHashMap <String,AtomicInteger> ();	
		
		// Crear el patrÃ³n que se usarÃ¡ para analizar una traza, y del que, ademÃ¡s de conocer si es una traza correcta, se pueden extraer ciertos valores mediante los grupos
		Pattern pTraza = Pattern.compile (patronTraza);

		/* Variables usadas para las estadÃ­sticas generales */
		int numeroFicherosProcesados;
		final AtomicInteger lineasCorrectas = new AtomicInteger(0);
		final AtomicInteger lineasIncorrectas = new AtomicInteger(0);
		
		/****** Comienzo del procesamiento ******/
		
		// Obtener los ficheros de log del directorio
		final File[] ficheros=obtenerFicheros(directorioFuente);
		
		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
		System.out.print ("\nLanzando hilos en los " + numDeNucleos + " nÃºcleos disponibles de este ordenador ");
		
		// Crear un pool donde ejecutar los hilos. El pool tendrÃ¡ un tamaÃ±o del nÂº de nÃºcleos del ordenador
		// por lo que nunca podrÃ¡ haber mÃ¡s hilos que ese nÃºmero en ejecuciÃ³n simultÃ¡nea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecuciÃ³n, poner como argumento un 1. IrÃ¡ mucho mÃ¡s lenta
		final ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

	    final long tiempoComienzo = System.currentTimeMillis();
	    final AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
	    int numTrabajadores=0;
	    numeroFicherosProcesados=0;
		for (File fichero: ficheros){
			System.out.print (".");
			ejecutor.execute(new Trabajador(fichero,  pTraza,  lineasCorrectas,  lineasIncorrectas, hmServidores,hmEstadisticasAgregadas, hmPatronesEstadisticasAgregadas,hmUsuarios,numTrabajadoresTerminados));
			numeroFicherosProcesados++;
			numTrabajadores++;
			//break; // Descomentando este break, solo se ejecuta el primer trabajador
		}
		System.out.print ("\nSe van a ejecutar "+numTrabajadores+" trabajadores en el pool. Esperar a que terminen ");
		
		// Esperar a que terminen todos los trabajadores
		ejecutor.shutdown();	// Cerrar el ejecutor cuando termine el Ãºltimo trabajador
		// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado y los que quedan
		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			final int terminados=numTrabajadoresTerminados.get();
			System.out.print("\nYa han terminado "+terminados+". Esperando a los "+(numTrabajadores-terminados)+" que quedan ");
		}
		// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los creados
		System.out.println("\nYa han terminado los "+numTrabajadoresTerminados.get()+" trabajadores");
		
		/****** Mostrar las estadÃ­sticas ******/
		/* EstadÃ­sticas generales */
		System.out.println("\n\n  EstadÃ­sticas generales:");

		System.out.print("\t Servidores: ");
		// Crear un mapa temporal, llamado numServidores, que se usarÃ¡ para almacenar como clave el tipo de servidor y como valor un contador de cuantos hay de ese tipo en el mapa hmServidores
		HashMap<String, Integer> numServidores = new HashMap <String,Integer>();
		// Recorrer todas las entradas de hmServidores
		for (Map.Entry<String, String> entrada : hmServidores.entrySet()) {
			// Se intenta meter la clave de hmServidores en numServidores:
			// 		si no existe se aÃ±ade con el contador 1
			// 		si ya existe se incrementa el contador en 1
			String nombreServidor = entrada.getValue();
			numServidores.put (nombreServidor, numServidores.get(nombreServidor) == null ? 1 : Integer.valueOf(numServidores.get(nombreServidor)+1));
		}
		// Mostrar por la salida estÃ¡ndar todas las entradas del mapa numServidores
		for (Map.Entry<String, Integer> entrada : numServidores.entrySet()) {
			System.out.print(entrada.getKey()+"="+entrada.getValue()+" ");
		}
		
		System.out.println("\n\t Numero de ficheros procesados: "+ numeroFicherosProcesados);
		System.out.println("\t Numero de trazas procesadas: "+ lineasCorrectas.get()+lineasIncorrectas.get());
		System.out.println("\t Numero de trazas incorrectas: "+ lineasIncorrectas.get());

		
		/* EstadÃ­sticas agregadas */
		System.out.println("\n\n  EstadÃ­sticas agregadas:");
		
		// Para mostrar el contenido del mapa hmEstadisticasAgregadas de forma ordenado, se copia a un TreeMap y se muestra el contenido de este, pues un TreeMap almacena la informaciÃ³n ordenada por la clave
		Map <String, AtomicInteger> mapaOrdenado = new TreeMap<String ,AtomicInteger>(hmEstadisticasAgregadas);
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {
			//TODO: La siguiente instrucciÃ³n es correcta, pero se puede cambiar para que salga mejor formateada
			System.out.println("\t"+entrada.getKey()+" = " +entrada.getValue().get());
	        }

		/* EstadÃ­sticas de usuarios */
		System.out.println("\n  EstadÃ­sticas de usuarios que han enviado mÃ¡s de "+numMaxMsg+" mensajes:");
		
		// Se reutiliza el TreeMap anterior para mostrar el mapa hmUsuarios ordenado
		mapaOrdenado.clear();
		mapaOrdenado = new TreeMap<String ,AtomicInteger>(hmUsuarios);
		for (Map.Entry<String, AtomicInteger> entrada : mapaOrdenado.entrySet()) {
			if (entrada.getValue().get()>=numMaxMsg) 
				System.out.printf("\t%-20s %5d %n", entrada.getKey()+":",entrada.getValue().get());
		}

		
		final long tiempoFinal = System.currentTimeMillis();
		System.out.println("\nTiempo de ejecuciÃ³n: "+ ((long)(tiempoFinal-tiempoComienzo)/1000) + " segundos");
		System.out.println("\n\n");
	}
	
	
	/* MÃ©todos auxiliares */
	
	/**
	 * Devuelve un array de elementos File que se corresponden con los ficheros con extensiÃ³n log encontrados en el directorio que se pasa como parÃ¡metro
	 *
	 * @param	directorioFuente	Directorio que contiene los ficheros de log
	 */
	private static File[] obtenerFicheros(File directorioFuente) {
	
		final File[] ficheros = directorioFuente.listFiles( new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".log")){
						return true;
					}
					return false;
				}
			}
		);
		return ficheros;
	}
	
 
	/**
	 * Verifica que se ha pasado un argumento con el nombre del directorio y que este existe y se puede leer. En caso contrario aborta la aplicaciÃ³n.
	 *
	 * @param	args	Argumentos a analizar
	 */
	private static File validarArgumentos(String[] args) {
		// ValidaciÃ³n de argumentos
		if (args.length != 1){
			mostrarUso("ERROR: faltan argumentos");
			System.exit(-1);
		}
	
		// VerificaciÃ³n de que el directorio existe y puede ser leÃ­do
		File directorioFuente = new File(args[0]);
		if (!directorioFuente.isDirectory() || !directorioFuente.canRead()){
			mostrarUso("ERROR: El directorio '" + args[0] + "' no existe o no puede ser leÃ­do");
			System.exit(-1);
		}
		return (directorioFuente);
	}
	
	
	/**
	 * Muestra mensaje de uso del programa.
	 *
	 * @param	mensaje	Mensaje adicional informativo en la lÃ­nea inicial (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje);
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <directorio fuente>\n" +
				"\t\t donde <directorio fuente> es el path al directorio donde estÃ¡n los ficheros de log\n");
	}
}
