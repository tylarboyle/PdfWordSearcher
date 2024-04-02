import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Main {

    static String pdfLocation;
    static JTextField textFieldPdfLocation;
    static JTextField textFieldWordLocation;
    static ArrayList<Character> delimiters;

    public static void main(String[] args) throws IOException {

        Setup();
        JFrame frame = new JFrame();
        SetupUI(frame);
    }

    public static void Setup() {
        delimiters = new ArrayList<Character>();
        delimiters.add(' ');
        delimiters.add(',');
        delimiters.add('.');
        delimiters.add(';');
        delimiters.add(':');
        delimiters.add('!');
        delimiters.add('?');
    }

    public static void SetupUI(JFrame frame) {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        GridLayout grid = new GridLayout(400, 300);
        grid.setColumns(1);
        grid.setRows(5);
        frame.setLayout(grid);

        JLabel wordLocationLabel = new JLabel("words.txt location");
        JLabel pdfLocationLabel = new JLabel("pdf location");
        textFieldWordLocation = new JTextField();
        textFieldPdfLocation = new JTextField();

        //textFieldWordLocation.setText("/Users/tylarboyle/Desktop/TargetWords/words.txt");
        //textFieldPdfLocation.setText("/Users/tylarboyle/Desktop/TargetDocuments/");

        JButton pdfSelectbutton = new JButton("Select PDF Location");
        pdfSelectbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // PDF chooser
                final JFileChooser pdfFileChooser = new JFileChooser();
                pdfFileChooser.setDialogTitle("Open PDF Location");
                pdfFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal1 = pdfFileChooser.showOpenDialog(pdfSelectbutton);
                textFieldPdfLocation.setText(pdfFileChooser.getSelectedFile().getPath());

            }
        });
        pdfSelectbutton.setForeground(Color.BLUE);

        JButton wordsSelectbutton = new JButton("Select words.txt");
        wordsSelectbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // words chooser
                final JFileChooser wordsFileChooser = new JFileChooser();
                wordsFileChooser.setDialogTitle("Open words.txt");
                wordsFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int returnVal2 = wordsFileChooser.showOpenDialog(wordsSelectbutton);
                textFieldWordLocation.setText(wordsFileChooser.getSelectedFile().getPath());
            }
        });
        wordsSelectbutton.setForeground(Color.BLUE);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File fileDirectory = new File(textFieldPdfLocation.getText());
                File[] files = fileDirectory.listFiles();

                List<String> wordsOfInterest = null;
                try {
                    wordsOfInterest = LoadWordsOfInterest(textFieldWordLocation.getText());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                List<FileTracker> trackers = new ArrayList<FileTracker>();

                for (int i = 0; i < files.length; i++) {

                    if (!files[i].getName().equals(".DS_Store")) {
                        String text = WordInfoTracker.ScanForIllegals(LoadTextFromPdf(files[i]));
                        trackers.add(TrackFileOccurences(text.toLowerCase(), files[i].getName(), wordsOfInterest));
                    }
                }

                PrintResults(trackers);

            }
        });
        startButton.setForeground(Color.BLUE);

        frame.getContentPane().add(wordsSelectbutton);
        frame.getContentPane().add(textFieldWordLocation);
        frame.getContentPane().add(pdfSelectbutton);
        frame.getContentPane().add(textFieldPdfLocation);
        frame.getContentPane().add(startButton);
        frame.setVisible(true);

    }

    public static boolean FilenameCheck(String filename) {

       String fileType = filename.substring(filename.length()-4,filename.length());
       return fileType.equals(".pdf");
    }

    public static String LoadTextFromPdf(File file) {
        String text = "";
        PDDocument document = null;

        System.out.println("Reading : " + file.getName());
        try {
            document = Loader.loadPDF(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!document.isEncrypted()) {
            PDFTextStripper stripper = new PDFTextStripper();
            try {
                text = stripper.getText(document);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            document.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return text;
    }

    public static void PrintResults(List<FileTracker> trackers) {

        try {
            PrintWriter writer = new PrintWriter("results.txt", "UTF-8");
            writer.println("-----Results-----");
            writer.println("");
            for (int i = 0; i < trackers.size(); i++) {
                writer.println("-----------");
                writer.println(trackers.get(i).filename);
                writer.println("-----------");
                writer.println("");

                for (int j = 0; j < trackers.get(i).infoTrackers.size(); j++) {

                    writer.println(trackers.get(i).infoTrackers.get(j).targetWordLowerCase + " : "
                            + trackers.get(i).infoTrackers.get(j).numberOfOccurances);

                }
                writer.println("");
                writer.println("-----------");
                writer.println("");
            }
            writer.close();
            System.out.println("Done");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static List<String> LoadWordsOfInterest(String fileLocation) throws IOException {
        List<String> listOfStrings = new ArrayList<String>();
        FileReader fr = new FileReader(fileLocation);
        String s = new String();
        char ch;
        while (fr.ready()) {
            ch = (char) fr.read();
            if (ch == '\n') {
                listOfStrings.add(s.toString());
                s = new String();
            } else {
                s += ch;
            }
        }
        if (s.length() > 0) {
            listOfStrings.add(s.toString());
        }

        return listOfStrings;
    }

    public static FileTracker TrackFileOccurences(String fileContents, String fileName, List<String> words) {
        FileTracker fileTracker = new FileTracker(fileName);
        for (int i = 0; i < words.size(); i++) {
            // System.out.print(words.get(i));
            fileTracker.AddTracker(TrackWordOccurence(fileContents, words.get(i)));
        }
        return fileTracker;
    }

    public static WordInfoTracker TrackWordOccurence(String fileContents, String word) {
        WordInfoTracker infoTracker = new WordInfoTracker(word);

        infoTracker.numberOfOccurances = CountOccurences(fileContents, infoTracker.targetWordLowerCase,
                infoTracker.absoluteSearch);

        return infoTracker;
    }

    public static int CountOccurences(String fileContents, String word, boolean absoluteSearch) {
        int lastIndex = 0;
        int count = 0;
        while (lastIndex != -1) {

            lastIndex = fileContents.indexOf(word, lastIndex);

            if (lastIndex != -1) {

                if (absoluteSearch) {
                    count++;
                } else if (DelimiterCheck(fileContents.substring(lastIndex - 1, lastIndex + 1 + word.length()))) {
                    count++;
                }

                lastIndex += word.length();
            }
        }
        return count;
    }

    public static boolean DelimiterCheck(String word) {

        char first = word.charAt(0);
        char last = word.charAt(word.length() - 1);
        return CheckChar(first) && CheckChar(last);
    }

    public static boolean CheckChar(char c) {
        // System.out.println(c);
        return delimiters.contains(c);
    }

}
