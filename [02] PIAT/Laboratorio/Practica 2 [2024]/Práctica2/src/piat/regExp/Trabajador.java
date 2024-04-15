package piat.regExp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

/**
 * Hilo que realiza el procesamiento de un fichero de LOG y registra
 * los datos estadísticos solicitados.
 * @author José Luis López Presa
 */
public class Trabajador implements Runnable
{
	// Formatos básicos
	private static final String FECHA = "([0-9]{4}-[0-9]{2}-[0-9]{2})";
	private static final String HORA = "[0-9]{2}:[0-9]{2}:[0-9]{2}";
	private static final String TIPO = "([^0-9\\r\\n\\t\\f\\v ]+)";
	private static final String NUM = "([\\d]+)";
	private static final String ID = "\\[[\\w]+\\]";
	private static final String SEC = "(security-in|security-out)";
	private static final String ESP = "\\s+";
	private static final String REMIT = "([A-Za-z0-9_\\.-]+)";
	private static final String DOM = "@[A-Za-z0-9_]+[\\.[A-Za-z0-9_]+]+";
	private static final String INT =  ESP + "message from: <" + REMIT + DOM + ">" + ".+$"; 
	private static final String MENS = ESP + "(message from):" + ESP + ".+$";
	private static final String ENV = ESP + "(relay to):" + ESP + ".+$";
	private static final String INFECTADO = ESP + ".+security-antivirus: (INFECTED)" + ESP + ".+$";
	private static final String SPAMNOBLOQ = ESP + "SEC-PASSED:" + ESP + ".*security-antispam: (SPAM)$";
	private static final String SOBRECARGA = ESP + ".+\\((overload)\\)$";
	private static final String NODEST = ESP + ".+\\((bad) destination mailbox address\\)$";

	// Preámbulo de las trazas
	private static final String PREÁMBULO = FECHA + ESP + HORA + ESP + TIPO + NUM + ESP + ID + ":";

	// Traza genérica
	private static final String VÁLIDA = PREÁMBULO + ESP + ".+$";

	// Trazas a procesar
	private static final String GEN =  FECHA + ESP + HORA + ESP + "(msa)" + NUM + ESP + ID + ":" + INT;
	private static final String IN = FECHA + ESP + HORA + ESP + "(smtp-in)" + NUM + ESP + ID + ":" + MENS;
	private static final String OUT = FECHA + ESP + HORA + ESP + "(smtp-out)" + NUM + ESP + ID + ":" + ENV;
	private static final String INF = FECHA + ESP + HORA + ESP + SEC + NUM + ESP + ID + ":" + INFECTADO;
	private static final String SPAM = FECHA + ESP + HORA + ESP + SEC + NUM + ESP + ID + ":" + SPAMNOBLOQ;
	private static final String OVER = FECHA + ESP + HORA + ESP + TIPO + NUM + ESP + ID + ":" + SOBRECARGA;
	private static final String BAD = FECHA + ESP + HORA + ESP + TIPO + NUM + ESP + ID + ":" + NODEST;

	// Nombres para los estadísticos agregados por tipo de servidor y día
	private static final Map<String,String> nombreEst = new HashMap<String,String>();
	static
	{
		nombreEst.put ( "message from", "msgIn" );
		nombreEst.put ( "relay to", "msgOut" );
		nombreEst.put ( "INFECTED", "msgINFECTED" );
		nombreEst.put ( "SPAM", "msgSPAM" );
		nombreEst.put ( "overload", "code 4.3.2" );
		nombreEst.put ( "bad", "code 5.1.1" );
	}

	// Patrones para obtener los estadísticos agregados por tipo de servidor y día
	private static final List<Pattern> patrones = new ArrayList<Pattern>();
	static
	{
		patrones.add ( Pattern.compile ( IN ) );
		patrones.add ( Pattern.compile ( OUT ) );
		patrones.add ( Pattern.compile ( INF ) );
		patrones.add ( Pattern.compile ( SPAM ) );
		patrones.add ( Pattern.compile ( OVER ) );
		patrones.add ( Pattern.compile ( BAD ) );
	}

	// Patrón para contabilizar los estadísticos agregados por cuenta emisora
	private static final Pattern patrónCuentaEmisora = Pattern.compile ( GEN );

	// Patrón para reconocer las trazas válidas
	private static final Pattern patrónGenérico = Pattern.compile ( VÁLIDA );

	// Atributos
	private final File fichero;
	private final EstGenerales eg;	
	private final EstAgregadasTipoFecha ea;
	private final EstUsuario eu;
	private final String nombre;

	/**
	 * Constructor.
	 * @param f Fichero de LOG a procesar.
	 * @param eg Estadísticos generales a actualizar.
	 * @param ea Estadísticos agregados por tipo de servidor y día a actualizar.
	 * @param eu Estadísticos agregados por cuenta emisora a actualizar.
	 */
	public Trabajador (
		File f,
		EstGenerales eg,
		EstAgregadasTipoFecha ea,
		EstUsuario eu)
	{
		this.fichero = f;
		this.eg = eg;
		this.ea = ea;
		this.eu = eu;
		nombre = "Trabajador que procesa el fichero " + fichero.getName();
	}

	/**
	 * Procesa el fichero recibido y actualiza los estadísticos.
	 * En caso de que se produzca alguna excepción, muestra el mensaje correspondiente
	 * y termina su ejecución.
	 */
	public final void run ()
	{
		Thread.currentThread().setName(nombre);
		try
		{
			final BufferedReader entrada = new BufferedReader ( new FileReader ( fichero ) );
			procesarFichero ( entrada );
			entrada.close();
			eg.registrarFichero();
		}
		catch ( Exception e )
		{
			System.err.println ( e.getMessage() );
		}
	}

	private final void procesarFichero ( BufferedReader entrada )
	throws IOException
	{
		String línea;

		while ( ( línea = entrada.readLine() ) != null )
			procesarLinea ( línea );
	}

	private final void procesarLinea ( String línea )
	{
		final boolean casó = aplicarPatrones ( línea );
		final boolean error = !casó && !esVálida ( línea );
		eg.registrarTraza();
		if ( error )
			eg.registrarError();
	}

	private final boolean esVálida ( String l )
	{
		final Matcher m = patrónGenérico.matcher ( l );
		final boolean match = m.matches();
		if ( match )
		{
			final String tipoServ = m.group(2);
			final String num = m.group(3);
			eg.registrarServidor ( tipoServ, num );
		}
		return match;
	}

	private final boolean aplicarPatrones ( String l )
	{
		boolean éxito;

		éxito = false;
		// Procesar todos los patrones de la constante patrones que se usa
		// para obtener los estadísticos agregados por tipo de servidor y día
		for ( Pattern p : patrones )
		{	final boolean nuevo = aplicarPatrón ( p, l );
			éxito = éxito || nuevo;
		}
		//Procesar el patrón del estadístico agregado por cuenta emisora
		final boolean nuevo = agregarMensajes ( patrónCuentaEmisora, l );
		éxito = éxito || nuevo;
		return éxito;
	}

	private final boolean agregarMensajes ( Pattern p, String l )
	{
		final  Matcher m = p.matcher ( l );
		if ( !m.matches() )
			return false;
		final String usuario = m.group(4);
		eu.registrarMensaje ( usuario );
		return true;
	}

	private final boolean aplicarPatrón ( Pattern p, String l )
	{
		final  Matcher m = p.matcher ( l );

		if ( !m.matches() )
			return false;
		final String fecha = m.group(1);
		final String tipoServ = m.group(2);
		final String num = m.group(3);
		final String tipoEst = nombreEst.get ( m.group(4) );
		ea.registrarTraza ( tipoServ, fecha, tipoEst );
		return true;
	}
}
