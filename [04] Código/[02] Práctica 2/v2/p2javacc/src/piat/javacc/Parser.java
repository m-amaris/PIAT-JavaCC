/* Parser.java */
/* Generated By:JavaCC: Do not edit this line. Parser.java */
package piat.javacc;

@SuppressWarnings("unused") // quitar warnings en Parser.java
public class Parser implements ParserConstants {
  final boolean VERBOSE = true;
  // private EstUsuario eu = new EstUsuario();
  // private EstGenerales eg = new EstGenerales();
  // private EstAgregadasTipoFecha ea = new EstAgregadasTipoFecha();
  // private static int linea = 0;

  void skipto(EstGenerales eg,int kind, Exception e){
      if(e!=null){
        // System.out.println("Traza mal formada detectada..."+e.getMessage());
        eg.registrarError();
      }
                Token t;
        do {
                t = getNextToken();
                } while (t.kind != kind);
        }

  private void Registar(EstGenerales eg, EstAgregadasTipoFecha ea,String estadistico, Token tipoServidor, String numeroServidor,String fecha){
    eg.registrarServidor(tipoServidor.image,numeroServidor);
                ea.registrarTraza ( tipoServidor.image, fecha, estadistico );
  }

/** Producción principal. */
// Se pone (<EOL>)? ya que en algunos no-terminales una vez se ha detectado lo que interesa, consume todos los tokens de esa traza hasta llegar al final de línea, consumiendo tambien ese token. Es por eso que en esos casos puede no estar presente el <EOL>
  final public void Start(EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {
eg.registrarFichero();
    try {
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case FECHA:
        case NUMERO:{
          ;
          break;
          }
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
        Traza(eu,eg,ea);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case EOL:{
          jj_consume_token(EOL);
          break;
          }
        default:
          jj_la1[1] = jj_gen;
          ;
        }
      }
      jj_consume_token(0);
    } catch (Exception e) {
// e.printStackTrace();
    skipto(eg,EOL,e);
    }
}

/** Una Traza. */
// Se pone (<NUMERO>)? debido a que si hay una traza mal formada al principio del elemento, por ejemplo 2020-19-2 , el tokenmanager detecta que es un numero y genera un TokenManagerException. Esto hace que termine la ejecuccion del fichero. Para controlar ese error, colocamos esa sentencia y en caso de que se cumple consumimos todos los tokens hasta EOL
  final public void Traza(EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {String f;
eg.registrarTraza();
    try {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case NUMERO:{
        jj_consume_token(NUMERO);
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        ;
      }
      f = Fecha();
      Hora();
      TipoTraza(f, eu, eg,  ea);
    } catch (Exception e) {
// e.printStackTrace();
    skipto(eg,EOL,e);
    }
}

/** Un tipo de traza. */
/** El LOOKAHEAD sirve para poder comprobar todas los tipos de trazas que se quieren analizar y ver de todas ellas cual es la que encaja. Concretamente, el LOOKAHEAD tiene como funcion mirar un numero n de tokens hacia delante, para poder determinar que no-terminal es el correcto.*/
  final public void TipoTraza(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {
    if (jj_2_1(2147483647)) {
      TrazaGEN(f,eu,eg,ea);
    } else if (jj_2_2(2147483647)) {
      TrazaIN(f,eu,eg,ea);
    } else if (jj_2_3(2147483647)) {
      TrazaOUT(f,eu,eg,ea);
    } else if (jj_2_4(2147483647)) {
      TrazaINF(f,eu,eg,ea);
    } else if (jj_2_5(2147483647)) {
      TrazaSPAM(f,eu,eg,ea);
    } else if (jj_2_6(2147483647)) {
      TrazaOVER(f,eu,eg,ea);
    } else if (jj_2_7(2147483647)) {
      TrazaBAD(f,eu,eg,ea);
    } else if (jj_2_8(2147483647)) {
      TrazaValida(f,eu,eg,ea);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
}

  final public void TrazaGEN(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t; String u,n;
    t = jj_consume_token(MSA);
    n = Numero();
    Identificador();
    u = Intento(t,eg);
Registar(eg,ea,"message from",t, n, f); eu.registrarMensaje(u);
}

  final public void TrazaIN(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t; String n;
    t = jj_consume_token(SMTP_IN);
    n = Numero();
    Identificador();
    Intento(t,eg);
Registar(eg,ea,"message from",t, n, f);
}

  final public void TrazaOUT(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t; String n;
    t = jj_consume_token(SMTP_OUT);
    n = Numero();
    Identificador();
    RelayTo(t,eg);
Registar(eg,ea,"msgOut",t, n, f);
}

  final public void TrazaSPAM(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t; String n;
    t = jj_consume_token(SEC);
    n = Numero();
    Identificador();
    SpamNoBloq(t);
Registar(eg,ea,"msgSPAM",t, n, f);
}

  final public void TrazaINF(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t; String n;
    t = jj_consume_token(SEC);
    n = Numero();
    Identificador();
    Infectado(t,eg);
Registar(eg,ea,"msgINFECTED",t, n, f);
}

  final public void TrazaOVER(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t; String n;
    t = TipoServidor();
    n = Numero();
    Identificador();
    Sobrecarga(t);
Registar(eg,ea,"code 4.3.2",t, n, f);
}

  final public void TrazaBAD(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t;String n;
    t = TipoServidor();
    n = Numero();
    Identificador();
    Nodest(t);
Registar(eg,ea,"code 5.1.1",t, n, f);
}

  final public void TrazaValida(String f,EstUsuario eu, EstGenerales eg, EstAgregadasTipoFecha ea) throws ParseException {Token t ;String n;
    t = TipoServidor();
    n = Numero();
    Identificador();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case BOUNCED:{
      jj_consume_token(BOUNCED);
skipto(eg,EOL,null);
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    CualquierCosa();
eg.registrarServidor(t.image,n);
}

/* Otras producciones */
  final public  Token TipoServidor() throws ParseException {Token t;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case MSA:{
      t = jj_consume_token(MSA);
      break;
      }
    case SMTP_IN:{
      t = jj_consume_token(SMTP_IN);
      break;
      }
    case SMTP_OUT:{
      t = jj_consume_token(SMTP_OUT);
      break;
      }
    case SEC:{
      t = jj_consume_token(SEC);
      break;
      }
    case USER_MAILBOX:{
      t = jj_consume_token(USER_MAILBOX);
      break;
      }
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return t;}
    throw new Error("Missing return statement in function");
}

  final public String Fecha() throws ParseException {Token t;
    t = jj_consume_token(FECHA);
{if ("" != null) return t.image;}
    throw new Error("Missing return statement in function");
}

  final public void Hora() throws ParseException {
    jj_consume_token(HORA);
}

  final public String Numero() throws ParseException {Token t;
    t = jj_consume_token(NUMERO);
{if ("" != null) return t.image;}
    throw new Error("Missing return statement in function");
}

  final public void Identificador() throws ParseException {
    jj_consume_token(IDENTIFICADOR_TRAZA);
}

  final public String Intento(Token t,EstGenerales eg) throws ParseException {Token u,v;
    jj_consume_token(FROM);
    u = jj_consume_token(REMIT);
    v = jj_consume_token(DOM);
// linea++;
    // if(VERBOSE) System.out.println("-"+linea+"- ["+t.image+"] message from: "+u.image.substring(1)+"@"+v.image.substring(1, v.image.length() - 1)+" --> msgIn");
    skipto(eg,EOL,null);
    {if ("" != null) return u.image.substring(1);}
    throw new Error("Missing return statement in function");
}

  final public void RelayTo(Token t,EstGenerales eg) throws ParseException {String s;
    jj_consume_token(RELAY_TO);
    s = Relay();
// linea++;
    // if(VERBOSE)System.out.println("-"+linea+"- ["+t.image+"] relay to: "+s+" --> msgOut");
    skipto(eg,EOL,null);
}

  final public void SpamNoBloq(Token t) throws ParseException {Token u;
    jj_consume_token(SEC_PASSED);
    CualquierCosa();
    u = jj_consume_token(SPAM);

}

  final public void Infectado(Token t,EstGenerales eg) throws ParseException {Token u;
    CualquierCosa();
    u = jj_consume_token(INFECTED);
// linea++;
    // if(VERBOSE)System.out.println("-"+linea+"- ["+t.image+"] "+u.image+" --> msgINFECTED");
    skipto(eg,EOL,null);
}

  final public void Sobrecarga(Token t) throws ParseException {
    CualquierCosa();
    jj_consume_token(OVERLOAD);

}

  final public void Nodest(Token t) throws ParseException {
    CualquierCosa();
    jj_consume_token(BAD);

}

/** Se define el no-terminal CualquierCosa y no un token que recoga cualquier cosa (~[]) , debido a que este ultimo no permitiría reconocer ningun otro token que quisiesemos, aunque estos tuviesen prioridad. Es mas conveniente definir un no-terminal con los cualquier token que puede haber hasta encontrar el token que queramos reconocer. */
  final public void CualquierCosa() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case SEC_PASSED:{
      jj_consume_token(SEC_PASSED);
      break;
      }
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case NUMERO:
      case LETRA:
      case PUNTO:
      case DOS_PUNTOS:
      case GUION:
      case GUION_BAJO:
      case ARROBA:
      case IGUAL:
      case ABRE_PARENTESIS:
      case CIERRA_PARENTESIS:
      case COMA:
      case MENOR_QUE:
      case MAYOR_QUE:{
        ;
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case LETRA:{
        jj_consume_token(LETRA);
        break;
        }
      case NUMERO:{
        jj_consume_token(NUMERO);
        break;
        }
      case GUION_BAJO:{
        jj_consume_token(GUION_BAJO);
        break;
        }
      case PUNTO:{
        jj_consume_token(PUNTO);
        break;
        }
      case GUION:{
        jj_consume_token(GUION);
        break;
        }
      case DOS_PUNTOS:{
        jj_consume_token(DOS_PUNTOS);
        break;
        }
      case MENOR_QUE:{
        jj_consume_token(MENOR_QUE);
        break;
        }
      case MAYOR_QUE:{
        jj_consume_token(MAYOR_QUE);
        break;
        }
      case IGUAL:{
        jj_consume_token(IGUAL);
        break;
        }
      case ABRE_PARENTESIS:{
        jj_consume_token(ABRE_PARENTESIS);
        break;
        }
      case CIERRA_PARENTESIS:{
        jj_consume_token(CIERRA_PARENTESIS);
        break;
        }
      case COMA:{
        jj_consume_token(COMA);
        break;
        }
      case ARROBA:{
        jj_consume_token(ARROBA);
        break;
        }
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
}

  final public String Relay() throws ParseException {Token t; String s = "";
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case NUMERO:
      case LETRA:
      case PUNTO:
      case GUION:
      case GUION_BAJO:{
        ;
        break;
        }
      default:
        jj_la1[8] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case LETRA:{
        t = jj_consume_token(LETRA);
s += t.image;
        break;
        }
      case NUMERO:{
        t = jj_consume_token(NUMERO);
s += t.image;
        break;
        }
      case GUION_BAJO:{
        t = jj_consume_token(GUION_BAJO);
s += t.image;
        break;
        }
      case PUNTO:{
        t = jj_consume_token(PUNTO);
s += t.image;
        break;
        }
      case GUION:{
        t = jj_consume_token(GUION);
s += t.image;
        break;
        }
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
{if ("" != null) return s;}
    throw new Error("Missing return statement in function");
}

  private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_1()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_2()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_3()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_4()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_5()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_6()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_7()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_8()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_3R_Relay_323_5_25()
 {
    if (jj_scan_token(LETRA)) return true;
    return false;
  }

  private boolean jj_3R_Relay_323_5_24()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_Relay_323_5_25()) {
    jj_scanpos = xsp;
    if (jj_3R_Relay_323_32_26()) {
    jj_scanpos = xsp;
    if (jj_3R_Relay_323_60_27()) {
    jj_scanpos = xsp;
    if (jj_3R_Relay_323_83_28()) {
    jj_scanpos = xsp;
    if (jj_3R_Relay_323_106_29()) return true;
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_TrazaSPAM_183_3_8()
 {
    if (jj_scan_token(SEC)) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    if (jj_3R_SpamNoBloq_272_3_16()) return true;
    return false;
  }

  private boolean jj_3R_Relay_323_3_22()
 {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_Relay_323_5_24()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_SpamNoBloq_272_3_16()
 {
    if (jj_scan_token(SEC_PASSED)) return true;
    if (jj_3R_CualquierCosa_315_3_21()) return true;
    if (jj_scan_token(SPAM)) return true;
    return false;
  }

  private boolean jj_3R_CualquierCosa_316_5_23()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(7)) {
    jj_scanpos = xsp;
    if (jj_scan_token(6)) {
    jj_scanpos = xsp;
    if (jj_scan_token(12)) {
    jj_scanpos = xsp;
    if (jj_scan_token(9)) {
    jj_scanpos = xsp;
    if (jj_scan_token(11)) {
    jj_scanpos = xsp;
    if (jj_scan_token(10)) {
    jj_scanpos = xsp;
    if (jj_scan_token(18)) {
    jj_scanpos = xsp;
    if (jj_scan_token(19)) {
    jj_scanpos = xsp;
    if (jj_scan_token(14)) {
    jj_scanpos = xsp;
    if (jj_scan_token(15)) {
    jj_scanpos = xsp;
    if (jj_scan_token(16)) {
    jj_scanpos = xsp;
    if (jj_scan_token(17)) {
    jj_scanpos = xsp;
    if (jj_scan_token(13)) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_TrazaOUT_176_3_6()
 {
    if (jj_scan_token(SMTP_OUT)) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    if (jj_3R_RelayTo_262_3_14()) return true;
    return false;
  }

  private boolean jj_3R_CualquierCosa_315_3_21()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(30)) jj_scanpos = xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_CualquierCosa_316_5_23()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_TipoServidor_218_3_17()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(24)) {
    jj_scanpos = xsp;
    if (jj_scan_token(25)) {
    jj_scanpos = xsp;
    if (jj_scan_token(26)) {
    jj_scanpos = xsp;
    if (jj_scan_token(27)) {
    jj_scanpos = xsp;
    if (jj_scan_token(28)) return true;
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_TrazaIN_169_3_5()
 {
    if (jj_scan_token(SMTP_IN)) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    if (jj_3R_Intento_251_3_13()) return true;
    return false;
  }

  private boolean jj_3R_Relay_323_83_28()
 {
    if (jj_scan_token(PUNTO)) return true;
    return false;
  }

  private boolean jj_3R_RelayTo_262_3_14()
 {
    if (jj_scan_token(RELAY_TO)) return true;
    if (jj_3R_Relay_323_3_22()) return true;
    return false;
  }

  private boolean jj_3_8()
 {
    if (jj_3R_TrazaValida_211_3_11()) return true;
    return false;
  }

  private boolean jj_3_7()
 {
    if (jj_3R_TrazaBAD_204_3_10()) return true;
    return false;
  }

  private boolean jj_3R_Relay_323_32_26()
 {
    if (jj_scan_token(NUMERO)) return true;
    return false;
  }

  private boolean jj_3_6()
 {
    if (jj_3R_TrazaOVER_197_3_9()) return true;
    return false;
  }

  private boolean jj_3R_TrazaValida_211_3_11()
 {
    if (jj_3R_TipoServidor_218_3_17()) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_TrazaValida_211_48_20()) jj_scanpos = xsp;
    if (jj_3R_CualquierCosa_315_3_21()) return true;
    return false;
  }

  private boolean jj_3_5()
 {
    if (jj_3R_TrazaSPAM_183_3_8()) return true;
    return false;
  }

  private boolean jj_3R_TrazaValida_211_48_20()
 {
    if (jj_scan_token(BOUNCED)) return true;
    return false;
  }

  private boolean jj_3R_Nodest_303_3_19()
 {
    if (jj_3R_CualquierCosa_315_3_21()) return true;
    if (jj_scan_token(BAD)) return true;
    return false;
  }

  private boolean jj_3_4()
 {
    if (jj_3R_TrazaINF_190_3_7()) return true;
    return false;
  }

  private boolean jj_3R_TrazaGEN_162_3_4()
 {
    if (jj_scan_token(MSA)) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    if (jj_3R_Intento_251_3_13()) return true;
    return false;
  }

  private boolean jj_3_3()
 {
    if (jj_3R_TrazaOUT_176_3_6()) return true;
    return false;
  }

  private boolean jj_3_2()
 {
    if (jj_3R_TrazaIN_169_3_5()) return true;
    return false;
  }

  private boolean jj_3_1()
 {
    if (jj_3R_TrazaGEN_162_3_4()) return true;
    return false;
  }

  private boolean jj_3R_Intento_251_3_13()
 {
    if (jj_scan_token(FROM)) return true;
    if (jj_scan_token(REMIT)) return true;
    if (jj_scan_token(DOM)) return true;
    return false;
  }

  private boolean jj_3R_TrazaBAD_204_3_10()
 {
    if (jj_3R_TipoServidor_218_3_17()) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    if (jj_3R_Nodest_303_3_19()) return true;
    return false;
  }

  private boolean jj_3R_Sobrecarga_292_3_18()
 {
    if (jj_3R_CualquierCosa_315_3_21()) return true;
    if (jj_scan_token(OVERLOAD)) return true;
    return false;
  }

  private boolean jj_3R_TrazaOVER_197_3_9()
 {
    if (jj_3R_TipoServidor_218_3_17()) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    if (jj_3R_Sobrecarga_292_3_18()) return true;
    return false;
  }

  private boolean jj_3R_Relay_323_60_27()
 {
    if (jj_scan_token(GUION_BAJO)) return true;
    return false;
  }

  private boolean jj_3R_Relay_323_106_29()
 {
    if (jj_scan_token(GUION)) return true;
    return false;
  }

  private boolean jj_3R_Numero_238_3_12()
 {
    if (jj_scan_token(NUMERO)) return true;
    return false;
  }

  private boolean jj_3R_TrazaINF_190_3_7()
 {
    if (jj_scan_token(SEC)) return true;
    if (jj_3R_Numero_238_3_12()) return true;
    if (jj_scan_token(8)) return true;
    if (jj_3R_Infectado_282_3_15()) return true;
    return false;
  }

  private boolean jj_3R_Infectado_282_3_15()
 {
    if (jj_3R_CualquierCosa_315_3_21()) return true;
    if (jj_scan_token(INFECTED)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[10];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
	   jj_la1_init_0();
	   jj_la1_init_1();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {0x50,0x800000,0x40,0x100000,0x1f000000,0x40000000,0xffec0,0xffec0,0x1ac0,0x1ac0,};
	}
	private static void jj_la1_init_1() {
	   jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
	}
  final private JJCalls[] jj_2_rtns = new JJCalls[8];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new ParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 10; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 10; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new ParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 10; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new ParserTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 10; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 10; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 10; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   if (++jj_gc > 100) {
		 jj_gc = 0;
		 for (int i = 0; i < jj_2_rtns.length; i++) {
		   JJCalls c = jj_2_rtns[i];
		   while (c != null) {
			 if (c.gen < jj_gen) c.first = null;
			 c = c.next;
		   }
		 }
	   }
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error {
    @Override
    public Throwable fillInStackTrace() {
      return this;
    }
  }
  static private final LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
	 if (jj_scanpos == jj_lastpos) {
	   jj_la--;
	   if (jj_scanpos.next == null) {
		 jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
	   } else {
		 jj_lastpos = jj_scanpos = jj_scanpos.next;
	   }
	 } else {
	   jj_scanpos = jj_scanpos.next;
	 }
	 if (jj_rescan) {
	   int i = 0; Token tok = token;
	   while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
	   if (tok != null) jj_add_error_token(kind, i);
	 }
	 if (jj_scanpos.kind != kind) return true;
	 if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
	 return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
	 if (pos >= 100) {
		return;
	 }

	 if (pos == jj_endpos + 1) {
	   jj_lasttokens[jj_endpos++] = kind;
	 } else if (jj_endpos != 0) {
	   jj_expentry = new int[jj_endpos];

	   for (int i = 0; i < jj_endpos; i++) {
		 jj_expentry[i] = jj_lasttokens[i];
	   }

	   for (int[] oldentry : jj_expentries) {
		 if (oldentry.length == jj_expentry.length) {
		   boolean isMatched = true;

		   for (int i = 0; i < jj_expentry.length; i++) {
			 if (oldentry[i] != jj_expentry[i]) {
			   isMatched = false;
			   break;
			 }

		   }
		   if (isMatched) {
			 jj_expentries.add(jj_expentry);
			 break;
		   }
		 }
	   }

	   if (pos != 0) {
		 jj_lasttokens[(jj_endpos = pos) - 1] = kind;
	   }
	 }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[37];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 10; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		   if ((jj_la1_1[i] & (1<<j)) != 0) {
			 la1tokens[32+j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 37; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 jj_endpos = 0;
	 jj_rescan_token();
	 jj_add_error_token(0, 0);
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
	 jj_rescan = true;
	 for (int i = 0; i < 8; i++) {
	   try {
		 JJCalls p = jj_2_rtns[i];

		 do {
		   if (p.gen > jj_gen) {
			 jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
			 switch (i) {
			   case 0: jj_3_1(); break;
			   case 1: jj_3_2(); break;
			   case 2: jj_3_3(); break;
			   case 3: jj_3_4(); break;
			   case 4: jj_3_5(); break;
			   case 5: jj_3_6(); break;
			   case 6: jj_3_7(); break;
			   case 7: jj_3_8(); break;
			 }
		   }
		   p = p.next;
		 } while (p != null);

		 } catch(LookaheadSuccess ls) { }
	 }
	 jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
	 JJCalls p = jj_2_rtns[index];
	 while (p.gen > jj_gen) {
	   if (p.next == null) { p = p.next = new JJCalls(); break; }
	   p = p.next;
	 }

	 p.gen = jj_gen + xla - jj_la; 
	 p.first = token;
	 p.arg = xla;
  }

  static final class JJCalls {
	 int gen;
	 Token first;
	 int arg;
	 JJCalls next;
  }

}
