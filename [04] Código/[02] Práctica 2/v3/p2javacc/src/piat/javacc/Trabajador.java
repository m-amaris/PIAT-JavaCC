package piat.javacc;

import java.io.File; 
import java.io.FileReader;

public class Trabajador implements Runnable{

    private final File fichero;
    private final String nombre;
    private final EstUsuario eu;
    private final EstGenerales eg;
    private final EstAgregadasTipoFecha ea;

    public Trabajador(File fichero, EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) {
        this.fichero = fichero;
        this.eu = eu;
        this.eg = eg;
        this.ea = ea;
        nombre = "Trabajador que procesa el fichero " + fichero.getName();
    }

    @Override
    public void run() {
        Thread.currentThread().setName(nombre);
        
        try {
            Parser t = new Parser(new ParserTokenManager(new SimpleCharStream(new FileReader(fichero))),eu,eg,ea,fichero);
            t.Start();
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }
}
