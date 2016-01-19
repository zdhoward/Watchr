import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;

/**
 * Created by Seutonious on 1/12/2016.
 */
public class Menu extends JFrame {

    private JList lst_subFolder;
    private JLabel lbl_rootFolder;
    private JLabel lbl_subFolder;
    private JLabel lbl_fileExtensions;
    private JTextField txt_fileExtensions;
    private JButton btn_rootFolder;
    private JTextField txt_rootFolder;
    private JButton btn_clearFileExtensions;
    private JPanel pnl_Menu;
    private JScrollPane scrollPane;
    private JList lst_results;
    private JList lst_leafFolder;
    private JLabel lbl_leafFolder;
    private JList lst_branchFolder;
    private JLabel lbl_branchFolder;
    private JButton btn_refresh;
    private JButton btn_reset;

    private int lastSelected;
    private boolean downClick;

    private Tools toolkit;

    private List<String> lines = new ArrayList<String>();
    private String line = null;

    public Menu() {
        // Initialize anything
        List<String> names = new ArrayList<String>();
        lastSelected = 0;

        File f = new File("rootFolder.cnf");
        if (f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("rootFolder.cnf"));
                txt_rootFolder.setText(br.readLine());
            } catch (Exception e) {
                System.out.println("Something really went wrong");
            }
        }
        f = new File("extensions.cnf");
        if (f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("extensions.cnf"));
                txt_fileExtensions.setText(br.readLine());
            } catch (Exception e) {
                System.out.println("Something really went wrong");
            }
        }

        // Draw Window
        setContentPane(pnl_Menu);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add Listeners
        btn_rootFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                lastSelected = 0;
                names.clear();
                lines.clear();

                fc.setCurrentDirectory(new java.io.File(txt_rootFolder.getText()));
                fc.setDialogTitle("Select Root Folder...");
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setAcceptAllFileFilterUsed(false);
                //
                if (fc.showOpenDialog(Menu.this) == JFileChooser.APPROVE_OPTION) {
                    //System.out.println("getCurrentDirectory(): " + fc.getCurrentDirectory());
                    //System.out.println("getSelectedFile() : " + fc.getSelectedFile());

                    txt_rootFolder.setText(fc.getSelectedFile().toString());
                    System.out.println(fc.getSelectedFile().toString());
                    //write to a file to save

                    try {
                        PrintWriter writer = new PrintWriter("rootFolder.cnf", "UTF-8");
                        writer.println(fc.getSelectedFile().toString());
                        writer.close();

                        writer = new PrintWriter("extensions.cnf", "UTF-8");
                        writer.println(txt_fileExtensions.getText());
                        writer.close();
                    } catch (Exception ex) {
                        System.out.println("Something went wrong :(");
                    }

                    File[] fileList = fc.getSelectedFile().listFiles();

                    for (File file : fileList) {
                        names.add(file.getName());
                    }

                    // GREP out all files that do not match our filters
                    StringTokenizer st = new StringTokenizer(txt_fileExtensions.getText());
                    ArrayList<String> filter = new ArrayList<String>();
                    while (st.hasMoreTokens()) {
                        filter.add(st.nextToken());
                    }

                    names.removeIf(p -> p.startsWith("."));

                    ArrayList<String> sArrPass = new ArrayList<String>();
                    ArrayList<String> sArrFail = new ArrayList<String>();

                    for (int i = 0; i < names.size(); i++) {
                        boolean pass = false;
                        for (int j = 0; j < filter.size(); j++) {
                            if (names.get(i).endsWith(filter.get(j))) {
                                pass = true;
                            }
                        }
                        if (pass) {
                            // This may need fine tuning
                            //resultNames.remove(i);
                            sArrPass.add(names.get(i));
                        }

                        if (!pass) {

                            sArrFail.add(names.get(i));
                        }
                    }

                    for (String s : sArrPass) {
                        names.removeIf(p -> p.equals(s));
                    }

                    ArrayList<String> rv = new ArrayList<String>();

                    for (int i = 0; i < sArrFail.size(); i++) {

                        if (sArrFail.get(i).contains(".")) {
                            rv.add(sArrFail.get(i));
                        }
                    }

                    for (String r : rv) {
                        sArrFail.remove(r);
                    }

                    String resultsFolder = new String();
                    switch (lastSelected) {
                        case 0:
                            resultsFolder = txt_rootFolder.getText();
                            break;
                        case 1:
                            resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue();
                            break;
                        case 2:
                            resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue();
                            break;
                        case 3:
                            resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue();
                            break;
                        default:
                            System.out.println("Something went wrong with resultsFolder");
                    }

                    // RESULTS
                    for (int i = 0; i < sArrPass.size(); i++){
                        String tmp = resultsFolder.toString() + "\\" + sArrPass.get(i).toString();
                        //System.out.println(tmp);
                        for (String line : lines) {
                            //System.out.println(line);

                            if (tmp.equals(line.toString())) {
                                //System.out.println("[SEEN] " + sArrPass.get(i));
                                sArrPass.set(i, "[SEEN] " + sArrPass.get(i));
                            }
                        }


                    }


                    lst_subFolder.setListData(sArrFail.toArray());
                    lst_results.setListData(sArrPass.toArray());

                }
                else {
                    System.out.println("No Selection ");
                }
            }
        });

        btn_clearFileExtensions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = JOptionPane.showConfirmDialog(Menu.this, "Confirmed!");

                //System.out.println(returnValue);

                switch (returnValue) {
                    case 0:

                        txt_rootFolder.setText("C:\\");
                        txt_fileExtensions.setText("");
                        lst_subFolder.clearSelection();
                        lst_branchFolder.clearSelection();
                        lst_leafFolder.clearSelection();
                        lst_results.clearSelection();

                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:

                }
            }
        });

        lst_subFolder.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent le) {
                downClick ^= true;
                if (downClick) {
                    lastSelected = 1;
                    int idx = lst_subFolder.getSelectedIndex();
                    if (idx >= 0) {
                        String string_subfolder = txt_rootFolder.getText() + "\\" + names.get(idx);

                        ArrayList<File> results = new ArrayList<File>(Arrays.asList(new java.io.File(string_subfolder).listFiles()));

                        ArrayList<String> resultNames = new ArrayList<String>();

                        for (int i = 0; i < results.size(); i++) {
                            resultNames.add(results.get(i).getName());
                        }

                        // GREP out all files that do not match our filters
                        StringTokenizer st = new StringTokenizer(txt_fileExtensions.getText());
                        ArrayList<String> filter = new ArrayList<String>();
                        while (st.hasMoreTokens()) {
                            filter.add(st.nextToken());
                        }

                        resultNames.removeIf(p -> p.startsWith("."));

                        ArrayList<String> sArrPass = new ArrayList<String>();
                        ArrayList<String> sArrFail = new ArrayList<String>();

                        for (int i = 0; i < resultNames.size(); i++) {
                            boolean pass = false;
                            for (int j = 0; j < filter.size(); j++) {
                                if (resultNames.get(i).endsWith(filter.get(j))) {
                                    pass = true;
                                }
                            }
                            if (pass) {
                                // This may need fine tuning
                                //resultNames.remove(i);
                                sArrPass.add(resultNames.get(i));
                            }

                            if (!pass) {

                                sArrFail.add(resultNames.get(i));
                            }
                        }

                        for (String s : sArrPass) {
                            resultNames.removeIf(p -> p.equals(s));
                        }

                        ArrayList<String> rv = new ArrayList<String>();

                        for (int i = 0; i < sArrFail.size(); i++) {

                            if (sArrFail.get(i).contains(".")) {
                                // Then s is a file, disregard it
                                //System.out.println(sArrFail.get(i));
                                rv.add(sArrFail.get(i));
                            }
                        }

                        for (String r : rv) {
                            sArrFail.remove(r);
                        }

                        String resultsFolder = new String();
                        switch (lastSelected) {
                            case 0:
                                resultsFolder = txt_rootFolder.getText();
                                break;
                            case 1:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue();
                                break;
                            case 2:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue();
                                break;
                            case 3:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue();
                                break;
                            default:
                                System.out.println("Something went wrong with resultsFolder");
                        }

                        // RESULTS
                        for (int i = 0; i < sArrPass.size(); i++){
                            // Convert read files to array and compare
                            //watched.log
                            String tmp = resultsFolder.toString() + "\\" + sArrPass.get(i).toString();
                            //System.out.println(tmp);
                            for (String line : lines) {
                                //System.out.println(line);

                                if (tmp.equals(line.toString())) {
                                    //System.out.println("[SEEN] " + sArrPass.get(i));
                                    sArrPass.set(i, "[SEEN] " + sArrPass.get(i));
                                }
                            }


                        }


                        lst_branchFolder.setListData(sArrFail.toArray());

                        lst_results.setListData(sArrPass.toArray());

                    } else {
                        System.out.println("Please choose a sub folder.");
                    }
                }
            }
        });

        lst_branchFolder.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent le) {
                downClick ^= true;
                if (downClick) {
                    lastSelected = 2;
                    int idx = lst_branchFolder.getSelectedIndex();
                    if (idx >= 0) {
                        // Useless...
                        String string_leafFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue();
                        // Can't Remove?

                        //ArrayList<File> results = new ArrayList<File>(Arrays.asList(new java.io.File(txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue()).listFiles()));

                        String root = new String(txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue());

                        File rootFile = new File(root);

                        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(rootFile.listFiles()));

                        ArrayList<String> resultNames = new ArrayList<String>();

                        for (int i = 0; i < fileList.size(); i++) {
                            resultNames.add(fileList.get(i).getName());
                        }

                        // GREP out all files that do not match our filters
                        StringTokenizer st = new StringTokenizer(txt_fileExtensions.getText());
                        ArrayList<String> filter = new ArrayList<String>();
                        while (st.hasMoreTokens()) {
                            filter.add(st.nextToken());
                        }

                        resultNames.removeIf(p -> p.startsWith("."));

                        ArrayList<String> sArrPass = new ArrayList<String>();
                        ArrayList<String> sArrFail = new ArrayList<String>();

                        for (int i = 0; i < resultNames.size(); i++) {
                            boolean pass = false;
                            for (int j = 0; j < filter.size(); j++) {
                                if (resultNames.get(i).endsWith(filter.get(j))) {
                                    pass = true;
                                }
                            }
                            if (pass) {
                                // This may need fine tuning
                                //resultNames.remove(i);
                                sArrPass.add(resultNames.get(i));
                            }

                            if (!pass) {

                                sArrFail.add(resultNames.get(i));
                            }
                        }

                        for (String s : sArrPass) {
                            resultNames.removeIf(p -> p.equals(s));
                        }

                        ArrayList<String> rv = new ArrayList<String>();

                        for (int i = 0; i < sArrFail.size(); i++) {

                            if (sArrFail.get(i).contains(".")) {
                                // Then s is a file, disregard it
                                //System.out.println(sArrFail.get(i));
                                rv.add(sArrFail.get(i));
                            }
                        }

                        for (String r : rv) {
                            sArrFail.remove(r);
                        }


                        String resultsFolder = new String();
                        switch (lastSelected) {
                            case 0:
                                resultsFolder = txt_rootFolder.getText();
                                break;
                            case 1:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue();
                                break;
                            case 2:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue();
                                break;
                            case 3:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue();
                                break;
                            default:
                                System.out.println("Something went wrong with resultsFolder");
                        }

                        // RESULTS
                        for (int i = 0; i < sArrPass.size(); i++){
                            // Convert read files to array and compare
                            //watched.log
                            String tmp = resultsFolder.toString() + "\\" + sArrPass.get(i).toString();
                            //System.out.println(tmp);
                            for (String line : lines) {
                                //System.out.println(line);

                                if (tmp.equals(line.toString())) {
                                    //System.out.println("[SEEN] " + sArrPass.get(i));
                                    sArrPass.set(i, "[SEEN] " + sArrPass.get(i));
                                }
                            }


                        }


                        lst_leafFolder.setListData(sArrFail.toArray());

                        lst_results.setListData(sArrPass.toArray());

                    } else {
                        System.out.println("Please choose a sub folder.");
                    }
                }
            }
        });

        lst_leafFolder.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent le) {
                downClick ^= true;
                if (downClick) {
                    lastSelected = 3;
                    int idx = lst_leafFolder.getSelectedIndex();
                    if (idx >= 0) {
                        // Useless...
                        String string_leafFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue();
                        // Can't Remove?

                        //ArrayList<File> results = new ArrayList<File>(Arrays.asList(new java.io.File(txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue()).listFiles()));

                        String root = new String(txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue());

                        File rootFile = new File(root);

                        ArrayList<File> fileList = new ArrayList<File>(Arrays.asList(rootFile.listFiles()));

                        ArrayList<String> resultNames = new ArrayList<String>();

                        //lst_results.setListData(resultNames);

                        for (int i = 0; i < fileList.size(); i++) {
                            resultNames.add(fileList.get(i).getName());
                        }

                        // GREP out all files that do not match our filters
                        StringTokenizer st = new StringTokenizer(txt_fileExtensions.getText());
                        ArrayList<String> filter = new ArrayList<String>();
                        while (st.hasMoreTokens()) {
                            filter.add(st.nextToken());
                        }

                        resultNames.removeIf(p -> p.startsWith("."));

                        ArrayList<String> sArrPass = new ArrayList<String>();
                        ArrayList<String> sArrFail = new ArrayList<String>();

                        for (int i = 0; i < resultNames.size(); i++) {
                            boolean pass = false;
                            for (int j = 0; j < filter.size(); j++) {
                                if (resultNames.get(i).endsWith(filter.get(j))) {
                                    pass = true;
                                }
                            }
                            if (pass) {
                                // This may need fine tuning
                                //resultNames.remove(i);
                                sArrPass.add(resultNames.get(i));
                            }

                            if (!pass) {

                                sArrFail.add(resultNames.get(i));
                            }
                        }


                        // File stuff

                        String resultsFolder = new String();
                        switch (lastSelected) {
                            case 0:
                                resultsFolder = txt_rootFolder.getText();
                                break;
                            case 1:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue();
                                break;
                            case 2:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue();
                                break;
                            case 3:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue();
                                break;
                            default:
                                System.out.println("Something went wrong with resultsFolder");
                        }

                        // RESULTS
                        for (int i = 0; i < sArrPass.size(); i++){
                            // Convert read files to array and compare
                            //watched.log
                            String tmp = resultsFolder.toString() + "\\" + sArrPass.get(i).toString();
                            //System.out.println(tmp);
                            for (String line : lines) {
                                //System.out.println(line);

                                if (tmp.equals(line.toString())) {
                                    //System.out.println("[SEEN] " + sArrPass.get(i));
                                    sArrPass.set(i, "[SEEN] " + sArrPass.get(i));
                                }
                            }


                        }

                        lst_results.setListData(sArrPass.toArray());


                    } else {
                        System.out.println("Please choose a sub folder.");
                    }
                }
            }
        });

        lst_results.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent le) {
                downClick ^= true;
                if (downClick) {
                    //lastSelected = 4;
                    boolean reset = false;
                    int idx = lst_results.getSelectedIndex();
                    if (idx >= 0) {
                        // Useless...
                        String string_leafFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue();
                        // Can't Remove?

                        //ArrayList<File> results = new ArrayList<File>(Arrays.asList(new java.io.File(txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue()).listFiles()));


                        String resultsFolder = new String();
                        switch (lastSelected) {
                            case 0:
                                //System.out.println("Please select a folder");
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_results.getSelectedValue();
                                break;
                            case 1:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_results.getSelectedValue();
                                break;
                            case 2:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_results.getSelectedValue();
                                break;
                            case 3:
                                resultsFolder = txt_rootFolder.getText() + "\\" + lst_subFolder.getSelectedValue() + "\\" + lst_branchFolder.getSelectedValue() + "\\" + lst_leafFolder.getSelectedValue() + "\\" + lst_results.getSelectedValue();
                                break;
                            default:
                                System.out.println("Something went wrong with resultsFolder");
                        }

                        String root = new String(resultsFolder);

                        File rootFile = new File(root);

                        try {
                            toolkit.open(rootFile);

                            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("watched.log", true)))) {
                                out.println(resultsFolder);
                            }catch (IOException e) {
                                System.out.println("IO Exception for watched.log");
                            }

                            reset = true;
                        } catch (IOException e) {
                            System.out.println("Something went wrong; IOException");
                        }
                    }
                }
            }
        });

        btn_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open File and read to array
                File f = new File("watched.log");
                if (f.exists()) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader("watched.log"));
                        while ((line = br.readLine()) != null) {
                            lines.add(line);
                            System.out.println(line);
                        }
                    } catch (Exception ex) {
                        System.out.println("Something really went wrong");
                    }
                }
            }
        });

        btn_reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = JOptionPane.showConfirmDialog(Menu.this, "You are about to delete your watched show history, do you want to continue?");

                //System.out.println(returnValue);

                switch (returnValue) {
                    case 0:
                        //try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("watched.log", true)))) {
                        try (PrintWriter out = new PrintWriter("watched.log")) {
                            out.print("");
                            out.close();
                        }catch (IOException ex) {
                            System.out.println("IO Exception for watched.log");
                        }
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:

                }
            }
        });


    }
}
