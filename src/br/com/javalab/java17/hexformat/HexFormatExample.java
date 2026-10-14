package br.com.javalab.java17.hexformat;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Java 17 - java.util.HexFormat (API nova, sem JEP; JDK-8251989)
 *
 * <p>Status: Definitivo.</p>
 *
 * <p>O que é: classe imutável e thread-safe para converter bytes e números em hexadecimal
 * e vice-versa, com delimitador, prefixo/sufixo e maiúsculas/minúsculas configuráveis.</p>
 * <p>Benefício: acaba com os utilitários caseiros ({@code String.format("%02x")} em laço,
 * {@code DatatypeConverter} do JAXB, Apache Commons Codec) para hashes, MACs e dumps.</p>
 * <p>Antes: laços com {@code String.format("%02x", b)} ou {@code Integer.toHexString(b & 0xff)}.</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java17/hexformat/HexFormatExample.java
 * </pre>
 */
public class HexFormatExample {

    public static void main(String[] args) throws Exception {
        byte[] hash = MessageDigest.getInstance("SHA-256")
                .digest("pedido-12345".getBytes(StandardCharsets.UTF_8));

        System.out.println("=== Abordagem antiga: laço com String.format ===");
        StringBuilder manual = new StringBuilder();
        for (byte b : hash) {
            manual.append(String.format("%02x", b));
        }
        System.out.println(manual);

        System.out.println();
        System.out.println("=== HexFormat ===");
        HexFormat hex = HexFormat.of();
        String digest = hex.formatHex(hash);
        System.out.println("SHA-256 : " + digest);
        System.out.println("Iguais? " + digest.contentEquals(manual));

        System.out.println();
        System.out.println("=== Formatos configuráveis ===");
        byte[] mac = {(byte) 0x00, (byte) 0x1A, (byte) 0x2B, (byte) 0x3C, (byte) 0x4D, (byte) 0x5E};
        System.out.println("Endereço MAC : " + HexFormat.ofDelimiter(":").withUpperCase().formatHex(mac));
        System.out.println("Array em C   : {" + HexFormat.ofDelimiter(", ").withPrefix("0x").formatHex(mac, 0, 3) + "}");

        System.out.println();
        System.out.println("=== Conversões ===");
        byte[] parsed = HexFormat.ofDelimiter(":").parseHex("CA:FE:BA:BE");
        System.out.println("parseHex(\"CA:FE:BA:BE\") -> " + parsed.length + " bytes, primeiro = " + parsed[0]);
        System.out.println("toHexDigits(255)          -> " + hex.toHexDigits(255));          // int: 8 dígitos
        System.out.println("toHexDigits((byte) 255)   -> " + hex.toHexDigits((byte) 255));   // byte: 2 dígitos
        System.out.println("fromHexDigits(\"7f\")       -> " + HexFormat.fromHexDigits("7f"));
        System.out.println("isHexDigit('g')           -> " + HexFormat.isHexDigit('g'));
    }
}
