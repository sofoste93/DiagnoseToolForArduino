package com.sofoste.arduino;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.prefs.Preferences;

public final class App extends Application {
    private static final String VERSION = "2.0.0";
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final SerialConnection.Port DEMO = new SerialConnection.Port("DEMO", "Simulated Arduino telemetry");
    private static final int MAX_HISTORY = 600;

    private final SerialConnection serial = new SerialConnection();
    private final TelemetryParser parser = new TelemetryParser();
    private final Preferences preferences = Preferences.userNodeForPackage(App.class);
    private final StringBuilder receiveBuffer = new StringBuilder();
    private final List<TelemetrySample> history = new ArrayList<>();
    private final Map<String, XYChart.Series<Number, Number>> series = new LinkedHashMap<>();

    private VBox content;
    private Label pageTitle;
    private Label pageSubtitle;
    private Label connectionStatus;
    private Label packetMetric;
    private Label channelMetric;
    private Label lastMetric;
    private Label sessionMetric;
    private TextArea terminal;
    private LineChart<Number, Number> chart;
    private ComboBox<SerialConnection.Port> portBox;
    private ComboBox<Integer> baudBox;
    private ComboBox<String> endingBox;
    private CheckBox autoScroll;
    private Button connectButton;
    private Button activeNavigation;
    private Timeline demoTimeline;
    private Timeline sessionTimeline;
    private SessionLog sessionLog;
    private Instant sessionStarted;
    private long sampleCount;
    private boolean demoMode;

    @Override public void start(Stage stage) {
        var shell = new BorderPane();
        shell.getStyleClass().add("app-shell");
        shell.setLeft(sidebar());
        shell.setTop(topbar());
        content = new VBox(16);
        content.getStyleClass().add("content");
        shell.setCenter(content);
        shell.setBottom(statusbar());

        var scene = new Scene(shell, 1320, 840);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/sofoste/arduino/theme.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Arduino Mission Control");
        stage.setMinWidth(1060);
        stage.setMinHeight(700);
        try { stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/sofoste/arduino/icon.png")))); }
        catch (Exception ignored) { }
        stage.setOnCloseRequest(event -> disconnect());
        stage.show();
        refreshPorts();
        showOverview();
    }

    private VBox sidebar() {
        var mark = label("∞", "logo-mark");
        var identity = new VBox(label("ARDUINO", "logo-title"), label("MISSION CONTROL", "logo-caption"));
        var logo = new HBox(12, mark, identity);
        logo.setAlignment(Pos.CENTER_LEFT);
        logo.getStyleClass().add("logo");

        var overview = navButton("⌁", "Telemetry", this::showOverview);
        var terminalButton = navButton(">_", "Terminal", this::showTerminal);
        var settings = navButton("◌", "Settings", this::showSettings);
        var help = navButton("?", "Help & about", this::showHelp);
        overview.getStyleClass().add("active");
        activeNavigation = overview;

        var spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        connectionStatus = label("●  STANDBY", "system-state");
        var systemCard = new VBox(7, connectionStatus, label("SERIAL LINK / LOCAL ONLY", "system-meta"),
                label("v" + VERSION + " · THOR LABS", "system-meta"));
        systemCard.getStyleClass().add("system-card");
        return styled(new VBox(logo, label("FLIGHT DECK", "section-label"), overview, terminalButton, settings, help, spacer, systemCard), "sidebar");
    }

    private HBox topbar() {
        pageTitle = label("Telemetry", "page-title");
        pageSubtitle = label("Live signals from your microcontroller", "page-subtitle");
        var titles = new VBox(2, pageTitle, pageSubtitle);
        portBox = new ComboBox<>();
        portBox.setPromptText("Select a serial port");
        portBox.setPrefWidth(285);
        baudBox = new ComboBox<>(FXCollections.observableArrayList(9600, 19200, 38400, 57600, 115200));
        baudBox.setValue(preferences.getInt("baud", 9600));
        baudBox.setPrefWidth(105);
        connectButton = primaryButton("CONNECT", this::toggleConnection);
        var refresh = iconButton("↻", this::refreshPorts, "Refresh ports");
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var top = new HBox(12, titles, spacer, portBox, baudBox, refresh, connectButton);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("topbar");
        return top;
    }

    private HBox statusbar() {
        var clock = label("", "status-text");
        var timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> clock.setText("UTC LINK  " + CLOCK.format(LocalTime.now()))));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        clock.setText("UTC LINK  " + CLOCK.format(LocalTime.now()));
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var bar = new HBox(clock, spacer, label("PRIVATE · OFFLINE · NO TELEMETRY UPLOAD", "privacy"));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("statusbar");
        return bar;
    }

    private void showOverview() {
        setPage("Telemetry", "Live signals from your microcontroller");
        packetMetric = label(Long.toString(sampleCount), "metric-value");
        channelMetric = label(Long.toString(history.stream().map(TelemetrySample::channel).distinct().count()), "metric-value");
        lastMetric = label(history.isEmpty() ? "—" : format(history.get(history.size() - 1).value()), "metric-value");
        sessionMetric = label(sessionStarted == null ? "00:00" : elapsed(), "metric-value");
        var metrics = new HBox(14,
                metric("SAMPLES", packetMetric, "↗", "lime"),
                metric("CHANNELS", channelMetric, "●", "cyan"),
                metric("LAST VALUE", lastMetric, "⌁", "orange"),
                metric("SESSION", sessionMetric, "◷", "violet"));
        metrics.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));

        var xAxis = new NumberAxis();
        xAxis.setLabel("samples");
        xAxis.setForceZeroInRange(false);
        var yAxis = new NumberAxis();
        yAxis.setLabel("value");
        yAxis.setForceZeroInRange(false);
        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(true);
        chart.setTitle("SIGNAL ORBIT");
        chart.getStyleClass().add("telemetry-chart");
        chart.setMinHeight(360);
        series.clear();
        long historyIndex = Math.max(0, sampleCount - history.size());
        for (TelemetrySample sample : history) plot(sample, ++historyIndex);

        var pins = new TextField("A0,A1");
        pins.setPromptText("A0,A1,2");
        var start = primaryButton("START STREAM", () -> send("START"));
        var pause = secondaryButton("PAUSE", () -> send("PAUSE"));
        var applyPins = secondaryButton("SET PINS", () -> send("PINS:" + pins.getText().trim()));
        var actions = new HBox(10, pins, applyPins, start, pause);
        HBox.setHgrow(pins, Priority.ALWAYS);
        var chartPanel = styled(new VBox(10, chart, actions), "panel");
        VBox.setVgrow(chart, Priority.ALWAYS);
        content.getChildren().setAll(hero(), metrics, chartPanel);
        VBox.setVgrow(chartPanel, Priority.ALWAYS);
    }

    private Node hero() {
        var text = new VBox(8, label("HARDWARE / SOFTWARE HANDSHAKE", "eyebrow"),
                label("See every signal.\nCommand every orbit.", "hero-title"),
                label("Connect a board or launch Demo telemetry to explore without hardware.", "hero-copy"));
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var demo = secondaryButton("LAUNCH DEMO", () -> {
            portBox.setValue(DEMO);
            if (!connected()) toggleConnection();
        });
        var hero = new HBox(text, spacer, demo);
        hero.setAlignment(Pos.BOTTOM_LEFT);
        hero.getStyleClass().add("hero");
        return hero;
    }

    private VBox metric(String title, Label value, String symbol, String color) {
        var glyph = label(symbol, "metric-icon");
        glyph.getStyleClass().add(color);
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var card = new VBox(12, new HBox(label(title, "metric-label"), spacer, glyph), value);
        card.getStyleClass().add("metric-card");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private void showTerminal() {
        setPage("Terminal", "Raw serial conversation and manual commands");
        terminal = new TextArea();
        terminal.setEditable(false);
        terminal.setWrapText(true);
        terminal.setPromptText("Connect a board to begin…");
        terminal.getStyleClass().add("terminal");
        var command = new TextField();
        command.setPromptText("Command, for example STATUS");
        command.setOnAction(event -> { send(command.getText()); command.clear(); });
        var send = primaryButton("SEND  ↗", () -> { send(command.getText()); command.clear(); });
        var clear = secondaryButton("CLEAR", () -> terminal.clear());
        var controls = new HBox(10, command, send, clear);
        HBox.setHgrow(command, Priority.ALWAYS);
        var tips = new HBox(10,
                quickCommand("START"), quickCommand("PAUSE"), quickCommand("STATUS"), quickCommand("STOP"));
        var panel = styled(new VBox(14, terminal, controls, tips), "panel");
        VBox.setVgrow(terminal, Priority.ALWAYS);
        content.getChildren().setAll(panel);
        VBox.setVgrow(panel, Priority.ALWAYS);
    }

    private Button quickCommand(String command) {
        var button = secondaryButton(command, () -> send(command));
        button.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    private void showSettings() {
        setPage("Settings", "Serial protocol and local storage");
        endingBox = new ComboBox<>(FXCollections.observableArrayList("LF", "CRLF", "None"));
        endingBox.setValue(preferences.get("ending", "LF"));
        endingBox.valueProperty().addListener((o, old, value) -> preferences.put("ending", value));
        autoScroll = new CheckBox("Follow incoming messages");
        autoScroll.setSelected(preferences.getBoolean("autoscroll", true));
        autoScroll.selectedProperty().addListener((o, old, value) -> preferences.putBoolean("autoscroll", value));
        var grid = new GridPane();
        grid.setHgap(22);
        grid.setVgap(18);
        grid.addRow(0, formLabel("BAUD RATE"), label("Set it from the top bar. It must match Serial.begin().", "setting-copy"));
        grid.addRow(1, formLabel("LINE ENDING"), endingBox);
        grid.addRow(2, formLabel("TERMINAL"), autoScroll);
        grid.addRow(3, formLabel("SESSION LOGS"), label(logDirectory().toString(), "path-label"));
        ColumnConstraints first = new ColumnConstraints(150);
        ColumnConstraints second = new ColumnConstraints();
        second.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(first, second);
        var privacy = infoCard("LOCAL BY DESIGN", "Telemetry is written only to your application-data folder. The app contains no analytics, cloud account, or network transport.");
        content.getChildren().setAll(styled(new VBox(20, label("SERIAL LINK", "eyebrow"), grid), "panel"), privacy);
    }

    private void showHelp() {
        setPage("Help & about", "Board setup, protocol, and crew notes");
        var cards = new HBox(14,
                infoCard("01 · CONNECT", "Plug in the board, choose its port and baud rate, then press Connect. Linux users may need dialout permission."),
                infoCard("02 · STREAM", "Upload the bundled sketch. Lines such as A0:512 or TEMP=23.4 become live chart channels."),
                infoCard("03 · COMMAND", "Use START, PAUSE, PINS:A0,A1, STATUS and STOP from Telemetry or Terminal."));
        cards.getChildren().forEach(node -> HBox.setHgrow(node, Priority.ALWAYS));
        var protocol = styled(new VBox(9, label("SUPPORTED TELEMETRY", "eyebrow"),
                label("A0:512    TEMP=23.4    RPM,1450", "protocol"),
                label("English: connect → start → inspect → export.\nDeutsch: verbinden → starten → prüfen → exportieren.", "help-copy")), "panel");
        var credits = styled(new VBox(8, label("ARDUINO MISSION CONTROL  /  v" + VERSION, "eyebrow"),
                label("Restored for the next hardware orbit.", "about-title"),
                label("Original project by Stephane Sob Fouodji and A. Franz. Built with JavaFX and jSerialComm.", "hero-copy"),
                label("THOR // transmission complete beyond the serial frontier.", "thor")), "about-card");
        content.getChildren().setAll(cards, protocol, credits);
    }

    private VBox infoCard(String title, String copy) {
        var body = label(copy, "help-copy");
        body.setWrapText(true);
        var card = new VBox(16, label(title, "help-title"), body);
        card.getStyleClass().add("help-card");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private void refreshPorts() {
        var selected = portBox == null ? null : portBox.getValue();
        List<SerialConnection.Port> ports;
        try { ports = new ArrayList<>(serial.availablePorts()); }
        catch (Throwable error) { ports = new ArrayList<>(); }
        ports.add(DEMO);
        portBox.setItems(FXCollections.observableArrayList(ports));
        if (selected != null) ports.stream().filter(p -> p.name().equals(selected.name())).findFirst().ifPresent(portBox::setValue);
        if (portBox.getValue() == null && ports.size() == 1) portBox.setValue(DEMO);
    }

    private void toggleConnection() {
        if (connected()) { disconnect(); return; }
        var selected = portBox.getValue();
        if (selected == null) { alert("Select a serial port or Demo telemetry first."); return; }
        try {
            sampleCount = 0;
            history.clear();
            sessionStarted = Instant.now();
            sessionLog = new SessionLog(logDirectory());
            preferences.putInt("baud", baudBox.getValue());
            if (selected == DEMO || selected.name().equals("DEMO")) startDemo();
            else serial.connect(selected.name(), baudBox.getValue(), bytes ->
                    Platform.runLater(() -> receive(new String(bytes, StandardCharsets.UTF_8))));
            connectButton.setText("DISCONNECT");
            connectButton.getStyleClass().add("connected");
            portBox.setDisable(true);
            baudBox.setDisable(true);
            connectionStatus.setText("●  LINK ONLINE");
            connectionStatus.getStyleClass().add("online");
            startSessionClock();
            appendTerminal("SYS", "Connected to " + selected.name() + " at " + baudBox.getValue() + " baud");
            showOverview();
        } catch (RuntimeException error) {
            closeLog();
            sessionStarted = null;
            alert(error.getMessage());
        }
    }

    private void startDemo() {
        demoMode = true;
        final double[] tick = {0};
        var random = new Random();
        demoTimeline = new Timeline(new KeyFrame(Duration.millis(280), event -> {
            tick[0] += .22;
            processLine("A0:" + Math.round(510 + Math.sin(tick[0]) * 360));
            if (((int) (tick[0] * 10)) % 5 == 0) processLine("TEMP=" + String.format(Locale.ROOT, "%.1f", 22 + random.nextDouble() * 3));
        }));
        demoTimeline.setCycleCount(Timeline.INDEFINITE);
        demoTimeline.play();
    }

    private void receive(String chunk) {
        receiveBuffer.append(chunk.replace("\r", ""));
        int newline;
        while ((newline = receiveBuffer.indexOf("\n")) >= 0) {
            String line = receiveBuffer.substring(0, newline).trim();
            receiveBuffer.delete(0, newline + 1);
            if (!line.isEmpty()) processLine(line);
        }
    }

    private void processLine(String line) {
        appendTerminal("RX", line);
        parser.parse(line).ifPresent(sample -> {
            history.add(sample);
            if (history.size() > MAX_HISTORY) history.remove(0);
            sampleCount++;
            if (sessionLog != null) sessionLog.append(sample);
            if (chart != null && chart.getScene() != null) plot(sample, sampleCount);
            updateMetrics(sample);
        });
    }

    private void plot(TelemetrySample sample, long index) {
        var dataSeries = series.computeIfAbsent(sample.channel(), channel -> {
            var created = new XYChart.Series<Number, Number>();
            created.setName(channel);
            if (series.size() < 6) chart.getData().add(created);
            return created;
        });
        if (!chart.getData().contains(dataSeries)) return;
        dataSeries.getData().add(new XYChart.Data<>(index, sample.value()));
        if (dataSeries.getData().size() > 180) dataSeries.getData().remove(0);
    }

    private void updateMetrics(TelemetrySample sample) {
        if (packetMetric != null) packetMetric.setText(Long.toString(sampleCount));
        if (channelMetric != null) channelMetric.setText(Long.toString(history.stream().map(TelemetrySample::channel).distinct().count()));
        if (lastMetric != null) lastMetric.setText(format(sample.value()));
    }

    private void send(String command) {
        if (command == null || command.isBlank()) return;
        if (!connected()) { alert("Connect a board or launch Demo telemetry first."); return; }
        String value = command.trim();
        appendTerminal("TX", value);
        if (demoMode) return;
        try { serial.send(value + lineEnding()); }
        catch (RuntimeException error) { alert(error.getMessage()); }
    }

    private void appendTerminal(String direction, String message) {
        if (terminal == null) return;
        terminal.appendText(CLOCK.format(LocalTime.now()) + "  " + direction + "  " + message + "\n");
        boolean follow = autoScroll == null ? preferences.getBoolean("autoscroll", true) : autoScroll.isSelected();
        if (follow) terminal.positionCaret(terminal.getLength());
    }

    private void disconnect() {
        if (demoTimeline != null) demoTimeline.stop();
        if (sessionTimeline != null) sessionTimeline.stop();
        demoMode = false;
        serial.close();
        closeLog();
        sessionStarted = null;
        if (connectButton != null) {
            connectButton.setText("CONNECT");
            connectButton.getStyleClass().remove("connected");
            portBox.setDisable(false);
            baudBox.setDisable(false);
            connectionStatus.setText("●  STANDBY");
            connectionStatus.getStyleClass().remove("online");
        }
    }

    private void startSessionClock() {
        sessionTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (sessionMetric != null) sessionMetric.setText(elapsed());
        }));
        sessionTimeline.setCycleCount(Timeline.INDEFINITE);
        sessionTimeline.play();
    }

    private String elapsed() {
        long seconds = java.time.Duration.between(sessionStarted, Instant.now()).toSeconds();
        return String.format(Locale.ROOT, "%02d:%02d", seconds / 60, seconds % 60);
    }

    private void closeLog() { if (sessionLog != null) { sessionLog.close(); sessionLog = null; } }
    private boolean connected() { return demoMode || serial.isConnected(); }
    private String lineEnding() {
        String value = endingBox == null ? preferences.get("ending", "LF") : endingBox.getValue();
        return switch (value) { case "CRLF" -> "\r\n"; case "None" -> ""; default -> "\n"; };
    }

    private Path logDirectory() {
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win")) {
            String local = System.getenv("LOCALAPPDATA");
            return Path.of(local == null ? home : local, "Arduino Mission Control", "sessions");
        }
        if (os.contains("mac")) return Path.of(home, "Library", "Application Support", "Arduino Mission Control", "sessions");
        String xdg = System.getenv("XDG_DATA_HOME");
        return Path.of(xdg == null || xdg.isBlank() ? Path.of(home, ".local", "share").toString() : xdg,
                "arduino-mission-control", "sessions");
    }

    private void setPage(String title, String subtitle) {
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
        content.getChildren().clear();
    }

    private Button navButton(String icon, String text, Runnable action) {
        var button = new Button(icon + "   " + text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> {
            if (activeNavigation != null) activeNavigation.getStyleClass().remove("active");
            button.getStyleClass().add("active");
            activeNavigation = button;
            action.run();
        });
        return button;
    }

    private Button primaryButton(String text, Runnable action) { return actionButton(text, "primary-button", action); }
    private Button secondaryButton(String text, Runnable action) { return actionButton(text, "secondary-button", action); }
    private Button iconButton(String text, Runnable action, String tooltip) {
        var button = actionButton(text, "icon-button", action);
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }
    private Button actionButton(String text, String style, Runnable action) {
        var button = new Button(text);
        button.getStyleClass().add(style);
        button.setOnAction(event -> action.run());
        return button;
    }
    private Label formLabel(String text) { return label(text, "form-label"); }
    private static Label label(String text, String style) { var label = new Label(text); label.getStyleClass().add(style); return label; }
    private static <T extends Pane> T styled(T node, String style) { node.getStyleClass().add(style); return node; }
    private static String format(double value) { return Math.abs(value % 1) < .0001 ? Long.toString(Math.round(value)) : String.format(Locale.ROOT, "%.2f", value); }
    private void alert(String message) {
        var alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText("Arduino Mission Control");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        if (List.of(args).contains("--diagnostics")) {
            var parser = new TelemetryParser();
            if (parser.parse("A0:512").orElseThrow().value() != 512) throw new IllegalStateException("Parser diagnostic failed.");
            System.out.println("Arduino Mission Control " + VERSION + " · systems nominal");
            return;
        }
        launch(args);
    }
}
