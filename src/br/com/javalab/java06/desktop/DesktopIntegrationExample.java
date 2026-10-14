package br.com.javalab.java06.desktop;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;

/**
 * Java 6 - Integração com o desktop: java.awt.Desktop e java.awt.SystemTray
 *
 * <p>Status: Definitivo (módulo {@code java.desktop}).</p>
 *
 * <p>O que é: {@code Desktop} permite abrir arquivos/URLs com o aplicativo padrão do
 * sistema ({@code browse(URI)}, {@code open(File)}, {@code edit}, {@code print},
 * {@code mail}); {@code SystemTray} + {@code TrayIcon} colocam um ícone na bandeja
 * do sistema operacional, com menu e notificações ({@code displayMessage}).</p>
 * <p>Benefício: integração nativa sem JNI nem {@code Runtime.exec("cmd /c start ...")}.</p>
 * <p>Antes: comandos específicos de cada SO via {@code Runtime.exec} ou a biblioteca
 * JDIC (de onde essas APIs vieram).</p>
 * <p>Este exemplo é SOMENTE consulta: verifica o suporte e lista as ações disponíveis,
 * sem abrir janelas, navegador ou ícones (para rodar em IDE/CI sem efeitos colaterais).
 * Em ambiente headless ({@code -Djava.awt.headless=true}) nada é suportado.</p>
 * <p>Evolução: o Java 9 (JEP 272) ampliou o {@code Desktop} (menus, eventos do app...).</p>
 *
 * <p>Execução (JDK 25):</p>
 * <pre>
 * java src/br/com/javalab/java06/desktop/DesktopIntegrationExample.java
 * java -Djava.awt.headless=true src/br/com/javalab/java06/desktop/DesktopIntegrationExample.java
 * </pre>
 */
public class DesktopIntegrationExample {

    public static void main(String[] args) {
        System.out.println("=== Ambiente gráfico ===");
        System.out.println("Headless? " + GraphicsEnvironment.isHeadless());

        System.out.println();
        System.out.println("=== Abordagem antiga: comando específico do SO ===");
        String os = System.getProperty("os.name");
        String command = os.startsWith("Windows") ? "cmd /c start https://exemplo.com"
                : os.startsWith("Mac") ? "open https://exemplo.com" : "xdg-open https://exemplo.com";
        System.out.println("Runtime.getRuntime().exec(\"" + command + "\")  (não executado)");

        System.out.println();
        System.out.println("=== java.awt.Desktop ===");
        boolean desktopSupported = Desktop.isDesktopSupported();
        System.out.println("Desktop suportado? " + desktopSupported);
        if (desktopSupported) {
            Desktop desktop = Desktop.getDesktop();
            for (Desktop.Action action : new Desktop.Action[] {Desktop.Action.BROWSE, Desktop.Action.OPEN,
                    Desktop.Action.EDIT, Desktop.Action.PRINT, Desktop.Action.MAIL}) {
                System.out.println("  " + action + ": " + desktop.isSupported(action));
            }
            System.out.println("  (uso real: desktop.browse(new URI(\"https://...\")) - não executado aqui)");
        }

        System.out.println();
        System.out.println("=== java.awt.SystemTray ===");
        boolean traySupported = SystemTray.isSupported();
        System.out.println("SystemTray suportado? " + traySupported);
        if (traySupported) {
            System.out.println("  Tamanho do ícone na bandeja: " + SystemTray.getSystemTray().getTrayIconSize());
            System.out.println("  (uso real: tray.add(new TrayIcon(imagem, \"App\")) - não executado aqui)");
        }
    }
}
