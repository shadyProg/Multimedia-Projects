package com.audio;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    Logger logger = Logger.getLogger(App.class.getName());
    //File myObj = new File("D:\\special for shady\\ComputerScience\\ComputerScience_Materials\\Multimedia\\projects\\sound Project\\soundpro\\src\\filedata.txt");
    boolean flag = true;
    int c = 0;
    String Result_report = "";
    String info_file1 = "";  
    String info_file2 = "";
    while (flag) {
    
        
    Scanner input = new Scanner(System.in);
    try {
        
        System.out.println("[*] Enter Path of sound 1 : ");
        String path1 = input.nextLine();
        logger.info("step 1 complete");
        File file1 = new File(path1);
        System.out.println("[*] Enter Path of sound 2 : ");
        String path2 = input.nextLine();
        logger.info("step 2 complete");
        
        File file2 = new File(path2);
        
    
        _File_Sound sound = new _File_Sound(file1);
_File_Sound sound2 = new _File_Sound(file2);
        _File_Sound_control control = new _File_Sound_control(sound, sound2);
        _File_Sound_control.DiffReport report = control.compare();
        Result_report = report.getSummary();
        info_file1 = sound.getInfo();
        info_file2 = sound2.getInfo();
        
        




// Discrete info
/* 
System.out.println(sound.getSampleRate());   
System.out.println(sound.getDurationSeconds()); 
System.out.println(sound.getChannelLabel());    

// All info at once
System.out.println(sound.getInfo());
*/






    flag = false;
    } catch (Exception e) {
        System.err.println("Error loading sound: " + e.getMessage());
    }
    do {
            ViewChoice(c);
        System.out.print("[-] Enter your choice : ");
        c = input.nextInt();
        System.out.println();
        switch (c) {
            case 1 :
                System.out.println(Result_report);
                break;
            case 2 :
                System.out.println(info_file1);
                break;
            case 3 :
                System.out.println(info_file2);
                break;
            case 4 :
                System.out.println("Exiting...");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
        }
        } while (c !=4);

        

    }
}

    private static void ViewChoice(int  c) {
        // TODO Auto-generated method stub
        System.out.println(String.format(
            "╔══════════════════════════════════════╗\n" +
            "║         Table of choises               ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║ Get Different   : [1] \n" +
            "║ Info file 1     : [2] \n" +
            "║ Info file 2     : [3] \n" +
            "║ exit            : [4] \n" +
            "╚══════════════════════════════════════╝ \n "));
        
    }
}