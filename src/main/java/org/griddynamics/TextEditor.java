package org.griddynamics;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    // Current file
    private File target;

    // JTextArea instance
    private JTextArea textArea;

    // Scroll wrapper around textArea
    private JScrollPane spane;

    // Control panel
    private JPanel ctrlPanel;

    // File chooser
    private JFileChooser jfc;

    // regex check box reference
    private JCheckBox useRegexCheckBox;

    // searchRegex flag
    private boolean useRegexFlag;

    // Search text field
    private JTextField searchRequest;

    // Search indexes result
    private List<SearchResult> searchResults;

    // Index of result to highlight
    private int currentResultIndex;

    public TextEditor() {
        createCrtlPanel();

        createTextArea();

        createMenu();

        createLayout();

        initializeFrame();
    }

    private void createLayout() {
        var pane = getContentPane();
        var gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        GroupLayout.ParallelGroup gh = gl.createParallelGroup();
        gh.addComponent(ctrlPanel);
        gh.addComponent(spane);
        gl.setHorizontalGroup(gh);

        GroupLayout.SequentialGroup gv = gl.createSequentialGroup();
        gv.addComponent(ctrlPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        gv.addComponent(spane);
        gl.setVerticalGroup(gv);

        pack();
    }

    private void initializeFrame() {
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setTitle("Text Editor");
        setVisible(true);
    }

    private void createTextArea() {
        textArea = new JTextArea();
        textArea.setName("TextArea");

        spane = new JScrollPane(textArea);
        spane.setName("ScrollPane");
        spane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        spane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    private void createCrtlPanel() {
        ctrlPanel = new JPanel();
        ctrlPanel.setBackground(Color.lightGray);

        ImageIcon saveImageIcon = new ImageIcon("/Users/bpylypchenko/IdeaProjects/Text Editor/Text Editor/task/assets/SaveButtonIcon.png");
        saveImageIcon.setImage(saveImageIcon.getImage().getScaledInstance(25, 25, 1));
        var saveButton = new JButton(saveImageIcon);
        saveButton.setName("SaveButton");
        saveButton.addActionListener(e -> saveAction());

        ctrlPanel.add(saveButton);

        ImageIcon openImageIcon = new ImageIcon("/Users/bpylypchenko/IdeaProjects/Text Editor/Text Editor/task/assets/OpenFileIcon.png");
        openImageIcon.setImage(openImageIcon.getImage().getScaledInstance(25, 25, 1));
        var openButton = new JButton(openImageIcon);
        openButton.setName("OpenButton");
        openButton.addActionListener(e -> openAction());

        ctrlPanel.add(openButton);

        searchRequest = new JTextField();
        searchRequest.setName("SearchField");
        Dimension dim = new Dimension(100, 30);
        searchRequest.setMinimumSize(dim);
        searchRequest.setPreferredSize(dim);
        searchRequest.setMaximumSize(dim);

        ctrlPanel.add(searchRequest);

        ImageIcon searchImageIcon = new ImageIcon("/Users/bpylypchenko/IdeaProjects/Text Editor/Text Editor/task/assets/SearchIcon.png");
        searchImageIcon.setImage(searchImageIcon.getImage().getScaledInstance(25, 25, 1));
        var searchButton = new JButton((searchImageIcon));
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(e -> searchAction());

        ctrlPanel.add(searchButton);

        ImageIcon previousMatchIcon = new ImageIcon("/Users/bpylypchenko/IdeaProjects/Text Editor/Text Editor/task/assets/PreviousMatch.png");
        previousMatchIcon.setImage(previousMatchIcon.getImage().getScaledInstance(25, 25, 1));
        var previousMatchButton = new JButton(previousMatchIcon);
        previousMatchButton.setName("PreviousMatchButton");
        previousMatchButton.addActionListener(e -> previousMatch());

        ctrlPanel.add(previousMatchButton);

        ImageIcon nextMatchIcon = new ImageIcon("/Users/bpylypchenko/IdeaProjects/Text Editor/Text Editor/task/assets/NextMatch.png");
        nextMatchIcon.setImage(nextMatchIcon.getImage().getScaledInstance(25, 25, 1));
        var nextMatchButton = new JButton(nextMatchIcon);
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(e -> nextMatch());

        ctrlPanel.add(nextMatchButton);

        useRegexFlag = false;
        useRegexCheckBox = new JCheckBox("Use regex");
        useRegexCheckBox.setName("UseRegExCheckbox");
        useRegexCheckBox.addItemListener(e -> useRegexFlag = !useRegexFlag);

        ctrlPanel.add(useRegexCheckBox);
    }

    private void createMenu() {
        jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setName("FileChooser");
        this.add(jfc);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        menuBar.add(fileMenu);

        JMenuItem loadMenuItem = new JMenuItem();
        loadMenuItem.setName("MenuOpen");
        loadMenuItem.setText("Load");
        loadMenuItem.addActionListener(e -> openAction());
        fileMenu.add(loadMenuItem);

        JMenuItem saveMenuItem = new JMenuItem();
        saveMenuItem.setName("MenuSave");
        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(e -> saveAction());
        fileMenu.add(saveMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setName("MenuExit");
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(e -> this.dispose());
        fileMenu.add(exitMenuItem);

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        menuBar.add(searchMenu);

        JMenuItem startSearchItem = new JMenuItem();
        startSearchItem.setName("MenuStartSearch");
        startSearchItem.setText("Search");
        startSearchItem.addActionListener(e -> searchAction());
        searchMenu.add(startSearchItem);

        JMenuItem previousMatchItem = new JMenuItem();
        previousMatchItem.setName("MenuPreviousMatch");
        previousMatchItem.setText("Previous match");
        previousMatchItem.addActionListener(e -> this.previousMatch());
        searchMenu.add(previousMatchItem);

        JMenuItem nextMatchItem = new JMenuItem();
        nextMatchItem.setName("MenuNextMatch");
        nextMatchItem.setText("Next match");
        nextMatchItem.addActionListener(e -> this.nextMatch());
        searchMenu.add(nextMatchItem);

        JMenuItem useRegularExpressionItem = new JMenuItem();
        useRegularExpressionItem.setName("MenuUseRegExp");
        useRegularExpressionItem.setText("Use regular expression");
        useRegularExpressionItem.addActionListener(e -> useRegexCheckBox.doClick());
        searchMenu.add(useRegularExpressionItem);
    }

    private void saveAction() {
        if (target == null) {
            target = new File(LocalDateTime.now().toString() + ".txt");
        }

        // Saving
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
            writer.write(textArea.getText());
        } catch (IOException exception) {
        }
    }

    private void openAction() {
        // Getting file to open
        int successFlag = jfc.showOpenDialog(null);
        if (successFlag != JFileChooser.APPROVE_OPTION) {
            return;
        }
        target = jfc.getSelectedFile();

        // Reading
        try (FileInputStream fis = new FileInputStream(target)) {
            textArea.setText(new String(fis.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException exception) {
            textArea.setText("");
        }
    }

    private void searchAction() {
        searchResults = new ArrayList<>();
        currentResultIndex = -1;
        if (useRegexFlag) {
            searchByMatch();
        } else {
            searchSubstring();
        }
    }

    /**
     * Class to store search results
     */
    private static class SearchResult {
        // Result pos in text
        private final int pos;

        // String - search result
        private final String result;

        /**
         * Default constructor
         */
        public SearchResult(int pos, String result) {
            this.pos = pos;
            this.result = result;
        }

        /**
         * Pos getter
         */
        public int getPos() {
            return pos;
        }

        /**
         * Result getter
         */
        public String getResult() {
            return result;
        }
    }

    private void searchSubstring() {
        String searchTarget = searchRequest.getText();
        if ("".equals(searchTarget)) {
            return;
        }

        f(searchTarget, textArea.getText(), 0);

        if (!searchResults.isEmpty()) {
            currentResultIndex = 0;
            showMatchInTextArea(searchResults.get(0));
        }
    }

    private void f(String searchTarget, String text, int offset) {
        if (searchTarget.length() > text.length()) {
            return;
        }

        int index = text.indexOf(searchTarget);

        if (index == -1) {
            return;
        }

        searchResults.add(new SearchResult(index + offset, searchTarget));

        int nextOffset = index + offset + searchTarget.length();
        f(searchTarget,
          text.substring(index + searchTarget.length()),
          nextOffset);
    }

    private void searchByMatch() {
        Pattern searchTarget = Pattern.compile(searchRequest.getText());
        String text = textArea.getText();
        Matcher matcher = searchTarget.matcher(text);
        while (matcher.find()) {
            searchResults.add(new SearchResult(matcher.start(), matcher.group()));
        }

        if (!searchResults.isEmpty()) {
            currentResultIndex = 0;
            showMatchInTextArea(searchResults.get(currentResultIndex));
        }
    }

    private void nextMatch() {
        if (currentResultIndex != -1) {
            currentResultIndex++;
            if (currentResultIndex >= searchResults.size()) {
                currentResultIndex = 0;
            }
            showMatchInTextArea(searchResults.get(currentResultIndex));
        }
    }

    private void previousMatch() {
        if (currentResultIndex != -1) {
            currentResultIndex--;
            if (currentResultIndex < 0) {
                currentResultIndex = searchResults.size() - 1;
            }
            showMatchInTextArea(searchResults.get(currentResultIndex));
        }
    }

    private void showMatchInTextArea(SearchResult current) {
        textArea.setCaretPosition(current.getPos() + current.getResult().length());
        textArea.select(current.getPos(), current.getPos() + current.getResult().length());
        textArea.grabFocus();
    }
}
