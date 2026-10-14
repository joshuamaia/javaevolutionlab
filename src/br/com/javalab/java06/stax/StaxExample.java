package br.com.javalab.java06.stax;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Java 6 - StAX: Streaming API for XML (JSR 173, javax.xml.stream)
 *
 * <p>Status: Definitivo (incorporada ao JDK no Java 6; hoje no módulo {@code java.xml}).</p>
 *
 * <p>O que é: API de XML "pull": o código PEDE o próximo evento ({@code next()}) com
 * {@code XMLStreamReader}, e escreve XML incrementalmente com {@code XMLStreamWriter}.</p>
 * <p>Benefício: baixo uso de memória (não carrega a árvore inteira, como o DOM) e
 * controle de fluxo simples (diferente do SAX, onde o parser "empurra" eventos para
 * callbacks e o estado fica espalhado em campos).</p>
 * <p>Antes: DOM (memória proporcional ao documento) ou SAX (push, com handlers).</p>
 * <p>Obs.: JAXB e JAX-WS também chegaram no Java 6, mas foram removidos do JDK no
 * Java 11 (JEP 320); hoje são bibliotecas externas (Jakarta XML Binding / XML Web Services).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/stax/StaxExample.java
 * </pre>
 */
public class StaxExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== XMLStreamWriter: gerando XML de pedidos ===");
        StringWriter out = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeStartElement("pedidos");
        String[][] orders = {{"PED-1", "Ana", "59.70"}, {"PED-2", "Bruno & Cia", "249.00"}};
        for (String[] o : orders) {
            writer.writeStartElement("pedido");
            writer.writeAttribute("id", o[0]);
            writer.writeStartElement("cliente");
            writer.writeCharacters(o[1]);       // escapa '&' automaticamente
            writer.writeEndElement();
            writer.writeStartElement("total");
            writer.writeCharacters(o[2]);
            writer.writeEndElement();
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.close();
        String xml = out.toString();
        System.out.println(xml);

        System.out.println();
        System.out.println("=== XMLStreamReader: lendo em modo pull ===");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // junta textos adjacentes ("Bruno ", "&", " Cia") num único evento CHARACTERS
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(xml));
        String currentId = null;
        String currentElement = null;
        double sum = 0;
        while (reader.hasNext()) {
            int event = reader.next(); // o código decide quando avançar
            if (event == XMLStreamConstants.START_ELEMENT) {
                currentElement = reader.getLocalName();
                if (currentElement.equals("pedido")) {
                    currentId = reader.getAttributeValue(null, "id");
                }
            } else if (event == XMLStreamConstants.CHARACTERS && !reader.isWhiteSpace()) {
                if ("cliente".equals(currentElement)) {
                    System.out.print(currentId + " cliente=" + reader.getText());
                } else if ("total".equals(currentElement)) {
                    double total = Double.parseDouble(reader.getText());
                    sum += total;
                    System.out.println(" total=" + total);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                currentElement = null;
            }
        }
        reader.close();
        System.out.println("Soma dos pedidos: " + sum);
    }
}
