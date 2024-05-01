package piat.regExp;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author S
 *
 */
public class Trabajador implements Runnable {

	private final File fichero;
	private final Pattern pTraza;
	private final AtomicInteger lineasCorrectas,lineasIncorrectas;
	private final ConcurrentHashMap<String, String>  hmServidores;
	private final ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas;	
	private final ConcurrentHashMap <String,Pattern> hmPatronesEstadisticasAgregadas;
	private final ConcurrentHashMap <String,AtomicInteger> hmUsuarios;	
	private final AtomicInteger numTrabajadoresTerminados;
	
	public Trabajador(	File fichero, Pattern pTraza, AtomicInteger lineasCorrectas, AtomicInteger lineasIncorrectas, 
						ConcurrentHashMap<String, String> hmServidores,ConcurrentHashMap<String, AtomicInteger> hmEstadisticasAgregadas, 
						ConcurrentHashMap <String,Pattern> hmPatronesEstadisticasAgregadas,ConcurrentHashMap <String,AtomicInteger> hmUsuarios, AtomicInteger numTrabajadoresTerminados) {
		this.fichero=fichero;
		this.pTraza=pTraza;
		this.lineasCorrectas=lineasCorrectas;
		this.lineasIncorrectas=lineasIncorrectas;
		this.hmServidores=hmServidores;
		this.hmEstadisticasAgregadas=hmEstadisticasAgregadas;
		this.hmPatronesEstadisticasAgregadas=hmPatronesEstadisticasAgregadas;
		this.hmUsuarios=hmUsuarios;
		this.numTrabajadoresTerminados=numTrabajadoresTerminados;
	}
		
	public void run (){
		Thread.currentThread().setName("Trabajador del fichero " + fichero.getName());
	    //System.out.println("["+Thread.currentThread().getName()+"] Empiezo");

		// Abrir el fichero y si no hay errores, procesar cada lÃ­nea invocando al mÃ©todo procesarLinea()
	    try {
	    	BufferedReader brInput = new BufferedReader(new FileReader(fichero));
			try{
				String linea;
				while ( (linea = brInput.readLine())!= null){
					procesarLinea(linea);
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
		
		//System.out.println("["+Thread.currentThread().getName()+"] Termino");
		System.out.print (".");
		numTrabajadoresTerminados.incrementAndGet();
	}
		
	/**
	 * Procesa una lÃ­nea que contendrÃ¡ una traza de un fichero de log.
	 * El procesamiento consiste en:
	 * 		- Verificar si es una traza con el formato correcto
	 * 		- Invocar a los mÃ©todos correspondientes para cada estadÃ­stico
	 *
	 * @param	sLinea	LÃ­nea a analizar
	 */
	public void procesarLinea(String sLinea){		
		Matcher matcher;
				
		if (sLinea.trim().length()>0){
			matcher = pTraza.matcher(sLinea);	// Realizar la casaciÃ³n de la lÃ­nea con el patrÃ³n genÃ©rico de una traza
			if (matcher.matches()) {	// Verificar que la lÃ­nea es correcta
				lineasCorrectas.incrementAndGet();
				estadisticasServidor(matcher);
				estadisticasAgregadas(matcher);
				estadisticasUsuarios(matcher);
			} else {
				//System.out.println("La lÃ­nea "+sLinea+" no tiene el formato correcto");
				lineasIncorrectas.incrementAndGet();
			}
		}
	}	
	

	/**
	 * Este mÃ©todo sirve para obtener las estadÃ­sticas de servidor.
	 * Guarda en el mapa hmServidores como clave el nombre de servidor y como valor su tipo
	 *
	 * @param	matcherLinea	Objeto Matcher al que ya se le ha realizado una casaciÃ³n y por tanto se puede extraer informaciÃ³n de sus grupos
	 */
	private void estadisticasServidor(Matcher matcherLinea) {
		// Extraer de matcherLinea el nombre del servidor y el tipo
		// TODO
		hmServidores.put (matcherLinea.group(2), matcherLinea.group(3));	// Meter los valores obtenidos en el mapa
	}
	
	/**
	 * Este mÃ©todo sirve para obtener las estadÃ­sticas agregadas cuyos patrones estÃ¡n en el mapa hmPatronesEstadisticasAgregadas
	 * Para ello recorre el mapa hmPatronesEstadisticasAgregadas y en cada iteraciÃ³n extrae un patron que prueba con la lÃ­nea que se estÃ¡ analizando.
	 * Si el patrÃ³n casa con la informaciÃ³n de la lÃ­nea se actualiza el mapa hmEstadisticasAgregadas
	 *
	 * @param	matcherLinea	Objeto Matcher al que ya se le ha realizado una casaciÃ³n y por tanto se puede extraer informaciÃ³n de sus grupos
	 */	
	private void estadisticasAgregadas(Matcher matcherLinea) {
		// Extraer de matcherLinea los valores que se necesitan a partir de los grupos disponibles
		final String traza=matcherLinea.group(0);				// Traza completa
		final String tipoServidor =   matcherLinea.group(3);	// Tipo de servidor
		final String fecha =   matcherLinea.group(1);	// Fecha

		Matcher comparador;
		String clave, estadistico;
		Pattern patron;
		final AtomicInteger contadorAnterior;
		
		// Recorrer el mapa hmPatronesEstadisticasAgregadas, que contiene los patrones a probar, para ver si cada uno de esos patrones casan con la traza a analizar
		for (Map.Entry<String,Pattern> entrada : hmPatronesEstadisticasAgregadas.entrySet()){
			estadistico=entrada.getKey();	// String con el nombre del estadÃ­stico
			patron=entrada.getValue();		// PatrÃ³n asociado al estadÃ­stico
			comparador=patron.matcher(traza);	// Aplicar el patrÃ³n a la traza a analizar
			if (comparador.matches()) {			// Si casa, entonces actualizar el mapa hmEstadisticasAgregadas
				clave = tipoServidor + " " + fecha + " - " + estadistico;	// La clave del mapa hmEstadisticasAgregadas estÃ¡ formada por el tipo del servidor y el nombre del estadÃ­stico
				// TODO En la aplicaciÃ³n de la prÃ¡ctica la clave del mapa hmEstadisticasAgregadas estarÃ¡ formada por el tipo del servidor, la fecha de la traza y el nombre del estadÃ­stico. Por ejemplo "security-in 2020-02-21 - msgBLOCKED" 
				// AÃ±adir el estadÃ­stico al mapa. Si no existe el valor del contador es 1, pero si existe, se recupera el valor y se incrementa
				contadorAnterior=hmEstadisticasAgregadas.putIfAbsent(clave, new AtomicInteger(1));
				if (contadorAnterior!=null) contadorAnterior.incrementAndGet();
				break;	// Este break sale del for, pues al ser los patrones disjuntos, en cuanto una lÃ­nea cumple un patrÃ³n, no hace falta mirar otros patrones
			}
		}
	}	
	
	/**
	 * Este mÃ©todo sirve para obtener las estadÃ­sticas del nÂº de correos que ha enviado cada usuario del sistema
	 * Por cada traza que se reconoce como traza en la que se ha enviado un mensaje, se actualiza el mapa hmUsuarios
	 *
	 * @param	matcherLinea	Objeto Matcher al que ya se le ha realizado una casaciÃ³n y por tanto se puede extraer informaciÃ³n de sus grupos
	 */	
	private void estadisticasUsuarios(Matcher matcherLinea) {
		// TODO:
		//	Ver si la traza se corresponde a una traza que indica que se ha enviado un mensaje
		//	En ese caso, guardar en el mapa hmUsuarios el nombre del usuario como clave y como valor el nÂº 1 si no existÃ­a esa clave, pues en el caso de que existiera hay que incrementar el valor
		final String traza=matcherLinea.group(0);
		String correo = ".*msa[0-9]+\\s+.*\\smessage from:\\s<([\\w-]+(\\.[\\w-]+)*)@[A-Za-z0-9]+(\\.[A-Za-z09]+)*(\\.[A-Za-z]{2,})>.*";
		String usuario;
		Matcher comparador;
		Pattern patron=Pattern.compile(correo);
		final AtomicInteger contadorAnterior;
		
		comparador=patron.matcher(traza);
		if(comparador.matches()) {
			usuario=comparador.group(1);
			contadorAnterior=hmUsuarios.putIfAbsent(usuario, new AtomicInteger(1));
			if(contadorAnterior!=null) {
				contadorAnterior.incrementAndGet();
			}
		}
	}	
	
}
