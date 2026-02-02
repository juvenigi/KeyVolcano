package foo.bar.keyvolcano;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TrayWebSocketUI {

    private static final Logger log = LoggerFactory.getLogger(TrayWebSocketUI.class);
    private final Map<String, Runnable> activeConnections = new ConcurrentHashMap<>();
    private final TrayIcon trayIcon;
    private final PopupMenu popupMenu;

    public TrayWebSocketUI() throws AWTException {
        log.info("java.awt.headless = {}", System.getProperty("java.awt.headless"));
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("System tray not supported");
        }

        popupMenu = new PopupMenu();

        Image image = Toolkit.getDefaultToolkit().createImage(
                getClass().getResource("/img.png")
        );

        trayIcon = new TrayIcon(image, "KeyVolcano", popupMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray.getSystemTray().add(trayIcon);

        // Optional exit item
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        var placeholder = new MenuItem("no connections");
        placeholder.setEnabled(false);
        popupMenu.add(placeholder);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);
    }

    // Call this when a new websocket announces itself
    public void promptIncomingConnection(String name, Runnable onAccept, Runnable onReject) {
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.showConfirmDialog(null,
                    "Allow connection for " + name + "?",
                    "Incoming WebSocket",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                addConnection(name, onAccept);
                onAccept.run();
            } else {
                onReject.run();
            }
        });
    }

    private void addConnection(String name, Runnable revokeAction) {
        activeConnections.put(name, revokeAction);
        MenuItem item = new MenuItem(name);
        item.addActionListener(e -> {
            // Remove from menu
            popupMenu.remove(item);
            activeConnections.remove(name);
            revokeAction.run();
        });
        popupMenu.insert(item, 0); // Add at top
    }

    public CompletableFuture<Boolean> promptIncomingConnection(String name) {
        var future = new CompletableFuture<Boolean>();

        SwingUtilities.invokeLater(() -> {
            JFrame parentComponent = null;
            try {
                parentComponent = straightUpJFrame();
                int result = JOptionPane.showConfirmDialog(
                        parentComponent,
                        "Allow connection for " + name + "?",
                        "Incoming WebSocket",
                        JOptionPane.YES_NO_OPTION
                );

                boolean yes = result == JOptionPane.YES_OPTION;
                if (yes) {
                    addConnection(name, () -> log.info("{} revoked", name));
                }
                future.complete(yes);
            } catch (Exception e) {
                future.completeExceptionally(e);
            } finally {
                if (parentComponent != null) {
                    parentComponent.dispose();
                }
            }
        });

        return future;
    }

    public static JFrame straightUpJFrame() {
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }
}
