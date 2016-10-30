package compiladorgui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class CompiladorController {

    @FXML
    private MenuItem btnAbrir;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button btnCompilar;
    @FXML
    private TextArea txtEditor;
    private char delimitadores[] = {',', '?', ':', ';', '.', '_', '-', '!', '"', '/', '*', '#', '@', '+', '%', '=', '[', ']', '{', '}'};
    String[] lineasDeCodigo = null;
    List<Variable> variablesGuardadas = new ArrayList();
    List variablesEntero = new ArrayList();
    List variablesFlotante = new ArrayList();
    List variablesCadena = new ArrayList();
    List variablesFecha = new ArrayList();
    List variablesBooleano = new ArrayList();
    ArrayList tokensPorFila = null;
    int contadorDeVariables = 0;
    int contadorDeOperadores = 0;
    String[] numeros = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    String[] tiposDeDatos = {"entero", "flotante", "cadena", "fecha", "booleano"};
    String[] casteoTipos = {"(entero)", "(cadena)", "(booleano)", "(fecha), (flotante)"};
    String[] sentenciaDecisionIF ={"IF","(",")","[","]"};
    public static String MensajeDeConsola = "";
    public String tipoDeDatosUsado = null;
    public String operadorEncontradoUsado = null;
    public String casteoEncontradoUsado = null;
    public String numeroAsignado = null;
    public String variableEncontrada = null;
    public String pathFiles;
    File file;

    //String[][] tokens = new String[0][0];
    @FXML
    void initialize() {
        // System.out.println("probando inicializador");
    }

    @FXML
    void abrirAcercaDe(ActionEvent event) {
    }

    @FXML
    void abrirArchivo(ActionEvent event) throws FileNotFoundException, IOException {
        FileChooser fileChooser = new FileChooser();
        FileReader FR = null;
        BufferedReader BR = null;

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show open file dialog
        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            if (file.getAbsolutePath().endsWith(".txt")) {
                //Guardamos la direccion de donde proviene el archivo
                pathFiles = file.getParent();
                FR = new FileReader(file);
                BR = new BufferedReader(FR);
                //leyendo el archivo
                String linea = "";//variable para leer linea por linea el archivo de entrada
                String texto = "";
                while ((linea = BR.readLine()) != null) { //cuando termina el texto del archivo?
                    texto += linea + "\n";
                }
                txtEditor.setText(texto);
            } else {
                System.out.println("Archivo no soportado Oops! Error");
            }

        }


    }

    @FXML
    void limpiarTexto(ActionEvent event) {
        txtEditor.setText("");
    }

    @FXML
    void abrirManual(ActionEvent event) {
    }

    @FXML
    void eventoCompilar(ActionEvent event) throws IOException {
        String codigo;

        codigo = txtEditor.getText();
        if (codigo.isEmpty() || codigo.trim().isEmpty()) {
            MensajeDeConsola = "No se encontró codigo";
        } else {
            guardarLineasDeCodigo(codigo);
            //Proceso de almacenamiento de parte de codigo
            for (int i = 0; i < lineasDeCodigo.length; i++) {
                validarTokens(lineasDeCodigo[i], i);
            }
        }

        if (MensajeDeConsola != null) {
            //Escribimos archivo de log
            if (MensajeDeConsola.isEmpty() || MensajeDeConsola.trim().isEmpty()) {
                MensajeDeConsola = "Compilado exitosamente 0 Errores";
            }
            if (!(txtEditor.getText().isEmpty())) {
                guardarLogConsola();
            }
            Dialogs.showErrorDialog();
            variablesGuardadas.clear();
            variablesEntero.clear();
            variablesFlotante.clear();
            variablesCadena.clear();
            variablesFecha.clear();
            variablesBooleano.clear();

        }
    }

    public void guardarLogConsola() throws IOException {
        Date fecha = new Date();
        String rutaLog = (pathFiles.concat("\\LogConsolaCompilador.txt"));

        File Ffichero = new File(rutaLog);
        if (!Ffichero.exists()) {
            Ffichero.createNewFile();
            BufferedWriter logWritte = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Ffichero), "utf-8"));
            logWritte.append("-------------------------------------------------------------------------------------------");
            logWritte.newLine();
            logWritte.append(fecha.toString());
            logWritte.newLine();
            logWritte.append(MensajeDeConsola);
            logWritte.flush();
            logWritte.close();
        } else {
            //Sobreescribiremos archivo
            StringBuilder contenidoViejo = new StringBuilder();
            try {
                BufferedReader entrada = new BufferedReader(new FileReader(pathFiles.concat("\\LogConsolaCompilador.txt")));
                try {
                    String line = null;
                    while ((line = entrada.readLine()) != null) {
                        contenidoViejo.append(line);
                        contenidoViejo.append(System.getProperty("line.separator"));
                    }
                } finally {
                    entrada.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            BufferedWriter logWritte = new BufferedWriter(new FileWriter(pathFiles.concat("\\LogConsolaCompilador.txt")));
            try {
                logWritte.write(contenidoViejo.toString());
                logWritte.newLine();
                logWritte.append("-------------------------------------------------------------------------------------------");
                logWritte.newLine();
                logWritte.append(fecha.toString());
                logWritte.newLine();
            logWritte.append(MensajeDeConsola);
                logWritte.flush();
                logWritte.close();
            } finally {
                logWritte.close();
            }
        }
    }

    public void validarTokens(String fila, int numFila) {
        numFila++;
        //Declaramos variables
        StringTokenizer tokens = new StringTokenizer(fila);
        boolean tipoEncontrado = false;
        boolean variableEncontrado = false;
        boolean casteoEncontrado = false;
        String elToken = "";
        //Obtenemos el primer token de la linea
        if (tokens.hasMoreTokens()) {
            elToken = tokens.nextToken();
        } else {
            return;
        }
        //Verificamos si es una declaracion de variable o si es una operacion con variables
        tipoEncontrado = validarTipo(elToken);
        variableEncontrado = encontrarVariable(elToken);
        //Si es declaracion de variables
        if (tipoEncontrado) {
            while (tokens.hasMoreTokens()) {
                elToken = tokens.nextToken();
                //verificamos si la variable no existe
                if (!encontrarVariable(elToken)) {
                    guardarVariables(elToken, numFila);
                } else {
                    //error variable ya existe.
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): La variable: '" + elToken + "' ya ha sido declarada");
                }
            }
        } //si encontramos una variable 
        else if (variableEncontrado) {
            contadorDeOperadores = 0;
            contadorDeVariables = 0;
            int iteracion = 1;
            while (tokens.hasMoreTokens()) {
                elToken = tokens.nextToken();
                //token impar
                if (iteracion % 2 != 0) {
                    //buscamos los operadores en toda la linea
                    if (buscarOperador(elToken)) {
                        contadorDeOperadores++;
                        if (iteracion == 1) {
                            if (!buscarOperadorInicial(elToken)) {
                                //error el operador inicial debe ser =
                                MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): El operador inicial debe ser '='");
                            }
                        } else {//otra iteracion
                            if (buscarOperadorInicial(elToken)) {
                                //error no pueden haber mas operadores  =
                                MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): Existe mas de un operador '='");
                            }
                        }
                    } else if (buscarOperador(elToken)) {
                        return;
                    } else if (encontrarVariable(elToken)) {
                        MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): No pueden haber dos variables seguidas");
                    } else {
                        MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): Operador '" + elToken + "' desconocido");
                    }
                    //buscamos un posible casteo
                    if (buscarCasteo(elToken)) {
                        casteoEncontrado = true;
                    }

                } //token par
                else if (iteracion % 2 == 0) {
                    //validamos el casteo
                    if (casteoEncontrado) {
                        if (!encontrarVariable(elToken)) {
                            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): Debe exisitir una variable despues de un casteo");

                        }
                    }

                    //buscamos las variables en toda la linea
                    if (encontrarVariable(elToken)) {
                        contadorDeVariables++;
                        for (int r = 0; r < variablesGuardadas.size(); r++) {
                            if (variableEncontrada.equals(variablesGuardadas.get(r).getNombre())) {
                                if ((variablesGuardadas.get(r).getValor()) ==null) {
                                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): La variable '" + elToken + "' no esta inicializada");
                                }
                            }
                        }
                    } else if (buscarOperador(elToken)) {
                        MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): No pueden haber dos operadores seguidos");
                    } else if (buscarOperador(elToken)) {
                        // no hacemos nada ya que no es variable
                        return;
                    } else if (!esNumero(elToken)) {
                        for (int r = 0; r < variablesGuardadas.size(); r++) {
                            if (variableEncontrada.equals(variablesGuardadas.get(r).getNombre())) {
                                if ((variablesGuardadas.get(r).getTipo().equals("cadena"))) {
                                    variablesGuardadas.get(r).setValor(elToken);
                                }else {
                                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): No se ha declarado la variable '" + elToken + "'");

                                }
                            }
                        }
                    }
                    if (esNumero(elToken)) {
                        //vamos a asignar el valor numerico a la variable aterior
                        for (int r = 0; r < variablesGuardadas.size(); r++) {
                            if (variableEncontrada.equals(variablesGuardadas.get(r).getNombre())) {
                                if ((variablesGuardadas.get(r).getTipo().equals("entero")) || (variablesGuardadas.get(r).getTipo().equals("flotante")) || (variablesGuardadas.get(r).getTipo().equals("cadena"))) {
                                    variablesGuardadas.get(r).setValor(numeroAsignado);
                                }
                            }
                        }
                    }
                }
                iteracion++;
            }
            //validamos que las operaciones tengan el mismo tipo de datos
            validarOperacionesConTipos(fila, numFila);

        } else {  //Significa que encontro otro caracter al inicio de la linea
            if (!encontrarVariable(elToken)) {
                MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): No se ha declarado la variable '" + elToken + "'");
            } else if (buscarOperador(elToken)) {
                MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): No puede iniciar la linea con '" + elToken + "'");
            } else {
                MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): Caracter invalido '" + elToken + "'");
            }

        }

    }
    
    public void validarSentenciaIF(String fila, int numFila){
        numFila++;
        //Declaramos variables
        StringTokenizer tokens = new StringTokenizer(fila);
        String elToken = "";
        boolean sentenciaCorrecta = false;
        //Obtenemos el primer token de la linea
        if (tokens.hasMoreTokens()) {
            elToken = tokens.nextToken();
        } else {
            return;
        }
         if(elToken.equals("IF")){
             for (int i=0; i< sentenciaDecisionIF.length ; i++){
               while (tokens.hasMoreTokens()) {
                //   if(tokens.)
             }
         }
        
        
    }
    }

    public void guardarLineasDeCodigo(String codigo) {
        int linea = 0;
        lineasDeCodigo = codigo.split("\n");
        for (int i = 0; i < lineasDeCodigo.length; i++) {
            linea++;
            if (!lineasDeCodigo[i].endsWith(";")) {
                MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + linea + "): la sentencia debe terminar con ';'");
            }
        }
        lineasDeCodigo = null;
        lineasDeCodigo = codigo.split(";");

        //lineasDeCodigo = codigo.split("\n");
    }

    private boolean validarTipo(String tokenEnviado) {
        boolean valido = false;

        for (int i = 0; i < tiposDeDatos.length; i++) {
            if (tokenEnviado.equals(tiposDeDatos[i])) {
                tipoDeDatosUsado = tokenEnviado;
                valido = true;
            }
        }
        return valido;
    }

    private boolean encontrarVariable(String tokenEnviado) {
        boolean valido = false;
        if (variablesGuardadas != null) {
            for (int i = 0; i < variablesGuardadas.size(); i++) {
                if (tokenEnviado.equals(variablesGuardadas.get(i).getNombre())) {
                    variableEncontrada = tokenEnviado;
                    valido = true;
                }
            }
        }

        return valido;
    }

    private String encontrarTipoDeVariable(String tokenEnviado) {
        String tipo = null;
        //buscamos si es entero
        if (variablesEntero != null) {
            for (int i = 0; i < variablesEntero.size(); i++) {
                if (tokenEnviado.equals(variablesEntero.get(i))) {
                    tipo = "entero";
                }
            }
        }
        //flotante
        if (variablesFlotante != null) {
            for (int j = 0; j < variablesFlotante.size(); j++) {
                if (tokenEnviado.equals(variablesFlotante.get(j))) {
                    tipo = "flotante";
                }
            }
        }
        //cadena
        if (variablesCadena != null) {
            for (int k = 0; k < variablesCadena.size(); k++) {
                if (tokenEnviado.equals(variablesCadena.get(k))) {
                    tipo = "cadena";
                }
            }
        }
        //fecha
        if (variablesFecha != null) {
            for (int l = 0; l < variablesFecha.size(); l++) {
                if (tokenEnviado.equals(variablesFecha.get(l))) {
                    tipo = "fecha";
                }
            }
        }
        //booleana
        if (variablesBooleano != null) {
            for (int m = 0; m < variablesBooleano.size(); m++) {
                if (tokenEnviado.equals(variablesBooleano.get(m))) {
                    tipo = "booleano";
                }
            }
        }

        return tipo;
    }

    private void guardarVariables(String elToken, int numFila) {
        //validamos las variables
        Variable nuevaVariable = new Variable();
        String tokenMinuscula = elToken.toLowerCase(Locale.ENGLISH);
        boolean minusculas = false;
        boolean numInicial = false;
        if (elToken.equals(tokenMinuscula)) {
            minusculas = true;
        }
        for (int w = 0; w < numeros.length; w++) {
            if (elToken.startsWith(numeros[w])) {
                numInicial = true;
            }
        }
        if (!minusculas) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): La variable '" + elToken + "' debe estar en minuscula");
        }
        if (numInicial) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): La variable '" + elToken + "' no debe comenzar con un numero");
        }
        if (minusculas && !numInicial) {
            if (tipoDeDatosUsado.equals("entero")) {
                variablesEntero.add(elToken);
            }
            if (tipoDeDatosUsado.equals("flotante")) {
                variablesFlotante.add(elToken);
            }
            if (tipoDeDatosUsado.equals("cadena")) {
                variablesCadena.add(elToken);
            }
            if (tipoDeDatosUsado.equals("fecha")) {
                variablesFecha.add(elToken);
            }
            if (tipoDeDatosUsado.equals("booleano")) {
                variablesBooleano.add(elToken);
            }
            nuevaVariable.setNombre(elToken);
            nuevaVariable.setTipo(tipoDeDatosUsado);
            variablesGuardadas.add(nuevaVariable);
        }

    }

    private boolean buscarOperador(String elToken) {
        boolean valido = false;
        String[] operadores = {"=", "+", "-", "/", "*"};
        for (int i = 0; i < operadores.length; i++) {
            if (elToken.equals(operadores[i])) {
                valido = true;
            }
        }
        return valido;
    }

    private boolean buscarOperadoresSecundarios(String elToken) {
        boolean valido = false;

        String[] operadores = {"+", "-", "/", "*"};
        for (int i = 0; i < operadores.length; i++) {
            if (elToken.equals(operadores[i])) {
                valido = true;
                operadorEncontradoUsado = elToken;
            }
        }
        return valido;
    }

    private boolean buscarCasteo(String elToken) {
        boolean valido = false;
        for (int i = 0; i < casteoTipos.length; i++) {
            if (elToken.equals(casteoTipos[i])) {
                valido = true;
                casteoEncontradoUsado = elToken;
            }
        }
        return valido;
    }

    private boolean esNumero(String cadena) {
        try {
            Integer.parseInt(cadena);
            numeroAsignado = cadena;
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean buscarOperadorInicial(String elToken) {
        boolean valido = false;
        String operador = "=";
        if (elToken.equals(operador)) {
            valido = true;
        }

        return valido;
    }

    private void validarOperacionesConTipos(String fila, int numFila) {
        //Declaramos variables
        StringTokenizer tokens = new StringTokenizer(fila);
        boolean operadorEncontrado = false;
        boolean variableEncontrado = false;
        boolean casteoEncontrado = false;
        boolean entero = false;
        boolean flotante = false;
        boolean cadena = false;
        boolean fecha = false;
        boolean booleano = false;
        String elToken = "";
        String primeraVariable = null;
        String tipoDeDatoPrimeraVariable = null;
        int iteracion = 0;
        //Obtenemos el primer token de la linea
        while (tokens.hasMoreTokens()) {
            elToken = tokens.nextToken();
            variableEncontrado = encontrarVariable(elToken);
            operadorEncontrado = buscarOperadoresSecundarios(elToken);
            casteoEncontrado = buscarCasteo(elToken);

            //encontro una variable
            if (variableEncontrado) {
                //buscamos el tipo de dato de la variable


                //guardamos la primera variable
                if (iteracion == 0) {
                    primeraVariable = elToken;
                    tipoDeDatoPrimeraVariable = encontrarTipoDeVariable(elToken);
                }
                //buscamos si es entero
                for (int i = 0; i < variablesEntero.size(); i++) {
                    if (elToken.equals(variablesEntero.get(i))) {
                        entero = true;
                    }
                }
                for (int j = 0; j < variablesFlotante.size(); j++) {
                    if (elToken.equals(variablesFlotante.get(j))) {
                        flotante = true;
                    }
                }
                for (int k = 0; k < variablesCadena.size(); k++) {
                    if (elToken.equals(variablesCadena.get(k))) {
                        cadena = true;
                    }
                }
                for (int l = 0; l < variablesFecha.size(); l++) {
                    if (elToken.equals(variablesFecha.get(l))) {
                        fecha = true;
                    }
                }
                for (int m = 0; m < variablesBooleano.size(); m++) {
                    if (elToken.equals(variablesBooleano.get(m))) {
                        booleano = true;
                    }
                }

            }
            //si encontramos un casteo
            if (casteoEncontrado) {

                if (tipoDeDatoPrimeraVariable.equals("entero")) {
                    if (casteoEncontradoUsado.equals("(entero)")) {
                        flotante = false;
                        cadena = false;
                        fecha = false;
                        booleano = false;
                    } else {
                        MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede convertir " + casteoEncontradoUsado + " a tipo 'entero'");

                    }
                }
                if (tipoDeDatoPrimeraVariable.equals("flotante")) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede convertir " + casteoEncontradoUsado + " a tipo 'flotante'");

                }
                if (tipoDeDatoPrimeraVariable.equals("cadena")) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede convertir " + casteoEncontradoUsado + " a tipo 'cadena'");

                }
                if (tipoDeDatoPrimeraVariable.equals("fecha")) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede convertir " + casteoEncontradoUsado + " a tipo 'fecha'");

                }
                if (tipoDeDatoPrimeraVariable.equals("booleano")) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede convertir " + casteoEncontradoUsado + " a tipo 'booleano'");


                }

                //entero
                if (entero == true && cadena == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'entero' con tipo 'cadena'");

                }
                if (entero == true && fecha == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'entero' con tipo 'fecha'");

                }
                if (entero == true && booleano == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'entero' con tipo 'booleano'");

                }
                //flotante
                if (flotante == true && cadena == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'flotante' con tipo 'cadena'");

                }
                if (flotante == true && fecha == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'flotante' con tipo 'fecha'");

                }
                if (flotante == true && booleano == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'flotante' con tipo 'booleano'");

                }
                //cadena
                if (cadena == true && fecha == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'cadena' con tipo 'fecha'");

                }
                if (cadena == true && booleano == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'cadena' con tipo 'booleano'");

                }
                //fecha
                if (fecha == true && booleano == true) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'fecha' con tipo 'booleano'");

                }

            }
            //si es una cadena verificaremos el uso de operadores solo sea =
            if (cadena) {
                if (operadorEncontrado) {
                    MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar una variable tipo cadena con '" + operadorEncontradoUsado + "'");

                }
            }
            //booleano

            iteracion++;
        }
        //entero
        if (entero == true && flotante == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nADVERTENCIA en la linea (" + numFila + "): puede haber perdida de informacion al  operar tipo 'entero' con tipo 'flotante'");

        }
        if (entero == true && cadena == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'entero' con tipo 'cadena'");

        }
        if (entero == true && fecha == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'entero' con tipo 'fecha'");

        }
        if (entero == true && booleano == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'entero' con tipo 'booleano'");

        }
        //flotante
        if (flotante == true && cadena == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'flotante' con tipo 'cadena'");

        }
        if (flotante == true && fecha == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'flotante' con tipo 'fecha'");

        }
        if (flotante == true && booleano == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'flotante' con tipo 'booleano'");

        }
        //cadena
        if (cadena == true && fecha == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'cadena' con tipo 'fecha'");

        }
        if (cadena == true && booleano == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'cadena' con tipo 'booleano'");

        }
        //fecha
        if (fecha == true && booleano == true) {
            MensajeDeConsola = MensajeDeConsola.concat("\nERROR en la linea (" + numFila + "): no se puede operar tipo 'fecha' con tipo 'booleano'");

        }

    }
}
