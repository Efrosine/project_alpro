/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class model_trans {

    File dataUser = new File("dataNasabah.txt");

    public void isiData() throws FileNotFoundException, IOException {

        RandomAccessFile rUser = new RandomAccessFile(dataUser, "rw");
        while (rUser.getFilePointer() < rUser.length()) {
            String dataString = rUser.readLine();
            String[] split = dataString.split("!");
            if (split[0].equals(classHelper.getUserName())) {
                classHelper.setPw(split[1]);
                classHelper.setNoReq(split[2]);
                classHelper.setNama(split[3]);
                classHelper.setNoHP(split[4]);
                classHelper.setEmail(split[5]);
                classHelper.setSaldo(split[6]);
                break;
            }
        }
    }

    public boolean SimpanUser(String userName, String pw, String nama, String noHP, String email) {
        boolean found = false;
        try {
            //file User
            if (!dataUser.exists()) {
                dataUser.createNewFile();
            }
            RandomAccessFile rUser = new RandomAccessFile(dataUser, "rw");
            while (rUser.getFilePointer() < rUser.length()) {
                String dataString = rUser.readLine();
                String[] split = dataString.split("!");
                if (split[0].equals(userName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                String noReq = createRandInt(5);
                String data = MessageFormat.format("{0}!{1}!{2}!{3}!{4}!{5}!100000", userName, pw, noReq, nama, noHP, email);
                rUser.writeBytes(data);
                rUser.writeBytes(System.lineSeparator());
                rUser.close();
                createLog(noReq);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "username sudah ada", "EROR", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(model_trans.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean cekLogIn(String userName, String pw) {
        boolean cek = false;
        try {
            RandomAccessFile raf = new RandomAccessFile(dataUser, "rw");
            while (raf.getFilePointer() < raf.length()) {
                String data = raf.readLine();
                String[] split = data.split("!");
                if (split[0].equals(userName) && split[1].equals(pw)) {
                    cek = true;
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(model_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cek;
    }

    public void tarikDana(int nominal) throws FileNotFoundException, IOException {
        logUser(classHelper.getNoReq(), "Tarik Tunai", -nominal, "Tarik Tunai");
    }

    public void beliPulsa(int harga) throws FileNotFoundException, IOException {
        logUser(classHelper.getNoReq(), "Beli Pulsa", -harga, "Beli Pulsa");
    }

    public void transfer(String noReq, int dana, String pesan) throws FileNotFoundException, IOException {
        logUser(noReq, "Transfer Masuk", dana, pesan);
        logUser(classHelper.getNoReq(), "Transfer Keluar", -dana, pesan);
    }

    private void logUser(String noReq, String jenis, int dana, String pesan) throws FileNotFoundException, IOException {
        String temp = noReq + ".txt";
        FileWriter writer = new FileWriter(temp, true);
        DateTimeFormatter log = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime time = LocalDateTime.now();
        String logTime = log.format(time);
        String msg = MessageFormat.format("{0}!{1}!{2}!{3}", logTime, jenis, String.valueOf(dana), pesan);
        writer.write(msg);
        writer.write(System.lineSeparator());
        writer.close();
        perubahanDana(noReq, dana);
    }

    private void perubahanDana(String noReq, int dana) throws FileNotFoundException, IOException {
        Scanner sc = new Scanner(dataUser);
        StringBuffer buffer = new StringBuffer();
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine() + System.lineSeparator());
        }
        String data = buffer.toString();
        sc.close();
        String oldLine = MessageFormat.format("{0}!{1}!{2}!{3}!{4}!{5}!{6}", getArr(noReq)[0], getArr(noReq)[1], getArr(noReq)[2], getArr(noReq)[3], getArr(noReq)[4], getArr(noReq)[5], getArr(noReq)[6]);
        int temp = Integer.parseInt(getArr(noReq)[6]);
        String newLine = MessageFormat.format("{0}!{1}!{2}!{3}!{4}!{5}!{6}", getArr(noReq)[0], getArr(noReq)[1], getArr(noReq)[2], getArr(noReq)[3], getArr(noReq)[4], getArr(noReq)[5], String.valueOf(temp + dana));
        data = data.replaceAll(oldLine, newLine);
        FileWriter writer = new FileWriter(dataUser);
        writer.append(data);
        writer.flush();
    }

    private String[] getArr(String noReq) throws FileNotFoundException, IOException {
        String[] split = new String[7];
        RandomAccessFile raf = new RandomAccessFile(dataUser, "rw");
        while (raf.getFilePointer() < raf.length()) {
            String dataString = raf.readLine();
            split = dataString.split("!");
            if (split[2].equals(noReq)) {
                break;
            }
        }
        return split;
    }

    public ArrayList<String[]> getLogData(String noReq) throws FileNotFoundException, IOException {
        ArrayList<String[]> arr = new ArrayList<>();
        String[] split = new String[4];
        String namaFile = noReq + ".txt";
        File file = new File(namaFile);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        while (raf.getFilePointer() < raf.length()) {
            String dataString = raf.readLine();
            split = dataString.split("!");
            arr.add(split);
        }
        return arr;
    }
    
    public ArrayList<String[]> getLogData() throws FileNotFoundException, IOException {
        ArrayList<String[]> arr = new ArrayList<>();
        String[] split = new String[8];
        RandomAccessFile raf = new RandomAccessFile(dataUser, "rw");
        while (raf.getFilePointer() < raf.length()) {
            String dataString = raf.readLine();
            split = dataString.split("!");
            arr.add(split);
        }
        return arr;
    }

    public ArrayList<String[]> getLogData(String noReq, Date awal, Date akhir) throws FileNotFoundException, IOException, ParseException {
        ArrayList<String[]> arr = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String[] split = new String[4];
        String namaFile = noReq + ".txt";
        File file = new File(namaFile);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        while (raf.getFilePointer() < raf.length()) {
            String dataString = raf.readLine();
            split = dataString.split("!");
            String s = split[0];
            int i = Integer.parseInt(split[2]);
            Date logDate = format.parse(s);
            if (logDate.after(awal) && logDate.before(akhir) && i < 0) {
                arr.add(split);
            }
        }
        return arr;
    }

    private void createLog(String userName) throws IOException {
        String namaFile = userName + ".txt";
        File logUser = new File(namaFile);
        if (!logUser.exists()) {
            logUser.createNewFile();
        }
    }

    public String createRandInt(int length) {
        String noReq = "";
        for (int i = 0; i < length; i++) {
            noReq += String.valueOf((int) (10 * Math.random()));
        }
        return noReq;
    }
}
