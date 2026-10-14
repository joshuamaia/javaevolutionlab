package br.com.javalab.java06.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * Java 6 - Scripting API (JSR 223, javax.script)
 *
 * <p>Status: Definitivo (API). Os engines mudaram: o Java 6 trazia o Rhino (JavaScript);
 * o Java 8 trocou por Nashorn (JEP 174), deprecated no Java 11 (JEP 335) e removido no
 * Java 15 (JEP 372). No JDK 25 a API existe (módulo {@code java.scripting}), mas
 * NENHUM engine vem embutido: {@code getEngineFactories()} retorna lista vazia.</p>
 *
 * <p>O que é: API neutra de linguagem para executar scripts dentro da JVM:
 * {@code ScriptEngineManager} descobre engines (via ServiceLoader), {@code ScriptEngine}
 * executa ({@code eval}), {@code Bindings}/{@code ScriptContext} trocam variáveis
 * entre Java e o script (escopos ENGINE e GLOBAL).</p>
 * <p>Benefício: regras e fórmulas configuráveis sem recompilar (Groovy, JRuby, Jython,
 * GraalJS e Nashorn standalone implementam a JSR 223).</p>
 * <p>Neste exemplo implementamos um engine mínimo ("calc": atribuições e expressões
 * aritméticas) com {@code AbstractScriptEngine} + {@code ScriptEngineFactory} e o
 * registramos com {@code registerEngineName}, para demonstrar a API sem dependências.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/scripting/ScriptingApiExample.java
 * </pre>
 */
public class ScriptingApiExample {

    /** Engine "calc": cada linha é "nome = expressão", "print expressão" ou "expressão". */
    static class CalcEngine extends AbstractScriptEngine {
        private final ScriptEngineFactory factory;
        private String src;
        private int pos;
        private ScriptContext ctx;

        CalcEngine(ScriptEngineFactory factory) {
            this.factory = factory;
        }

        @Override
        public Object eval(String script, ScriptContext context) throws ScriptException {
            return eval(new StringReader(script), context);
        }

        @Override
        public Object eval(Reader reader, ScriptContext context) throws ScriptException {
            Object last = null;
            this.ctx = context;
            try {
                BufferedReader lines = new BufferedReader(reader);
                String line;
                while ((line = lines.readLine()) != null) {
                    line = line.trim();
                    if (line.length() == 0) {
                        continue;
                    }
                    if (line.startsWith("print ")) {
                        PrintWriter out = new PrintWriter(context.getWriter(), true); // saída do contexto
                        out.println("  [calc] " + evalExpression(line.substring(6)));
                    } else if (line.matches("[a-zA-Z_]\\w*\\s*=.*")) {
                        int eq = line.indexOf('=');
                        last = evalExpression(line.substring(eq + 1));
                        context.setAttribute(line.substring(0, eq).trim(), last, ScriptContext.ENGINE_SCOPE);
                    } else {
                        last = evalExpression(line);
                    }
                }
            } catch (IOException e) {
                throw new ScriptException(e);
            }
            return last;
        }

        @Override
        public Bindings createBindings() {
            return new SimpleBindings();
        }

        @Override
        public ScriptEngineFactory getFactory() {
            return factory;
        }

        // ---- parser descendente recursivo: expr = term (+|- term)*, term = factor (*|/ factor)* ----
        private Double evalExpression(String expression) throws ScriptException {
            src = expression.replace(" ", "");
            pos = 0;
            double value = expr();
            if (pos != src.length()) {
                throw new ScriptException("Caractere inesperado '" + src.charAt(pos) + "' em: " + expression);
            }
            return value;
        }

        private double expr() throws ScriptException {
            double v = term();
            while (pos < src.length() && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) {
                v = src.charAt(pos++) == '+' ? v + term() : v - term();
            }
            return v;
        }

        private double term() throws ScriptException {
            double v = factor();
            while (pos < src.length() && (src.charAt(pos) == '*' || src.charAt(pos) == '/')) {
                v = src.charAt(pos++) == '*' ? v * factor() : v / factor();
            }
            return v;
        }

        private double factor() throws ScriptException {
            if (pos >= src.length()) {
                throw new ScriptException("Expressão incompleta");
            }
            char c = src.charAt(pos);
            if (c == '-') {
                pos++;
                return -factor();
            }
            if (c == '(') {
                pos++;
                double v = expr();
                pos++; // ')'
                return v;
            }
            int start = pos;
            if (Character.isDigit(c)) {
                while (pos < src.length() && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.')) {
                    pos++;
                }
                return Double.parseDouble(src.substring(start, pos));
            }
            while (pos < src.length() && Character.isLetterOrDigit(src.charAt(pos))) {
                pos++;
            }
            String name = src.substring(start, pos);
            Object value = ctx.getAttribute(name); // procura no ENGINE_SCOPE e depois no GLOBAL_SCOPE
            if (!(value instanceof Number)) {
                throw new ScriptException("Variável indefinida ou não numérica: '" + name + "'");
            }
            return ((Number) value).doubleValue();
        }
    }

    /** Fábrica: metadados do engine, usados pelo ScriptEngineManager na descoberta. */
    static class CalcEngineFactory implements ScriptEngineFactory {
        @Override public String getEngineName() { return "Calc Engine (exemplo)"; }
        @Override public String getEngineVersion() { return "1.0"; }
        @Override public List<String> getExtensions() { return Arrays.asList("calc"); }
        @Override public List<String> getMimeTypes() { return Arrays.asList("text/x-calc"); }
        @Override public List<String> getNames() { return Arrays.asList("calc"); }
        @Override public String getLanguageName() { return "Calc"; }
        @Override public String getLanguageVersion() { return "1.0"; }
        @Override public Object getParameter(String key) {
            return ScriptEngine.NAME.equals(key) ? "calc" : null;
        }
        @Override public String getMethodCallSyntax(String obj, String m, String... args) { return null; }
        @Override public String getOutputStatement(String toDisplay) { return "print " + toDisplay; }
        @Override public String getProgram(String... statements) {
            StringBuilder sb = new StringBuilder();
            for (String s : statements) {
                sb.append(s).append('\n');
            }
            return sb.toString();
        }
        @Override public ScriptEngine getScriptEngine() { return new CalcEngine(this); }
    }

    public static void main(String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();

        System.out.println("=== Engines descobertos automaticamente (ServiceLoader) ===");
        List<ScriptEngineFactory> factories = manager.getEngineFactories();
        System.out.println("Quantidade: " + factories.size()
                + (factories.isEmpty() ? " (JDK 15+ não traz engine; no Java 6 apareceria o Rhino)" : ""));
        for (ScriptEngineFactory f : factories) {
            System.out.println(" - " + f.getEngineName() + " " + f.getNames());
        }
        System.out.println("getEngineByName(\"javascript\") = " + manager.getEngineByName("javascript"));

        System.out.println();
        System.out.println("=== Registrando um engine próprio ===");
        manager.registerEngineName("calc", new CalcEngineFactory());
        ScriptEngine engine = manager.getEngineByName("calc");
        System.out.println("Engine: " + engine.getFactory().getEngineName()
                + ", linguagem " + engine.getFactory().getLanguageName());

        System.out.println();
        System.out.println("=== eval, put/get e escopos ===");
        manager.put("taxRate", 0.1);             // GLOBAL_SCOPE: visível para todos os engines do manager
        engine.put("subtotal", 250.0);           // ENGINE_SCOPE: Java -> script
        Object result = engine.eval("shipping = 15\ntotal = subtotal * (1 + taxRate) + shipping\nprint total");
        System.out.println("Resultado do eval: " + result);
        System.out.println("engine.get(\"total\") = " + engine.get("total")); // script -> Java

        System.out.println();
        System.out.println("=== Bindings separados: mesma fórmula, clientes diferentes ===");
        String formula = "subtotal - subtotal * discount";
        String[] customers = {"Ana", "Bruno"};
        double[] discounts = {0.05, 0.20};
        for (int i = 0; i < customers.length; i++) {
            Bindings bindings = engine.createBindings();
            bindings.put("subtotal", 100);
            bindings.put("discount", discounts[i]);
            System.out.println(customers[i] + " paga " + engine.eval(formula, bindings));
        }

        System.out.println();
        System.out.println("=== Erros chegam como ScriptException ===");
        try {
            engine.eval("total * unknownVar");
        } catch (ScriptException e) {
            System.out.println("ScriptException: " + e.getMessage());
        }
        System.out.println("Programa gerado pela factory: " + engine.getFactory().getProgram("x = 2", "print x * 21").replace("\n", " | "));
    }
}
