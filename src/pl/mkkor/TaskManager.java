package pl.mkkor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import static pl.mkkor.ConsoleColors.*;

public class TaskManager {
    public static void main(String[] args) {
        String[][] dataFromFileArray;
        String csvFile = "tasks.csv";
        try {
            dataFromFileArray = readDataFromFile(csvFile);
        } catch (FileNotFoundException e) {
            System.out.println("Given file: \'" + csvFile + "\' has not been found. Check file directory");
            return;
        }
        System.out.println("TEST: " + Arrays.deepToString(dataFromFileArray));

        final String[] OPTIONS_TO_SELECT = displayOptions();
        String chosenOption = choosingOption(OPTIONS_TO_SELECT);

        switch (chosenOption) {
            case "add":
                add();
                break;
            case "remove":
                remove();
                break;
            case "list":
                list();
                break;
            case "exit":
                exit();
                break;
        }


    }

    private static String[][] readDataFromFile(String csvFileString) throws FileNotFoundException {
        String[][] dataFromFileArray = new String[0][];
        File csvFile = new File(csvFileString);

        Scanner scan = new Scanner(csvFile);
        for (int i = 0; scan.hasNextLine(); i++) {
            dataFromFileArray = ArrayUtils.addAll(dataFromFileArray, new String[1][]);  //NEW dodawanie tablicy
            dataFromFileArray[i] = scan.nextLine().trim().split(", ");  //NEW przypisanie splitem
        }
        scan.close();
        return dataFromFileArray;
    }

    private static String[] displayOptions() {
        final String[] OPTIONS_TO_SELECT = new String[]{"add", "remove", "list", "exit"};
        System.out.println(BLUE + "Please select an option (choose only one option):" + RESET);
        for (String row : OPTIONS_TO_SELECT) {
            System.out.println(row);
        }
        return OPTIONS_TO_SELECT;
    }

    private static String choosingOption(String[] optionsToSelect) {
        Scanner scan = new Scanner(System.in);
        String chosenOption;
        while (true) {
            chosenOption = scan.nextLine().trim();
            if (!StringUtils.equalsAnyIgnoreCase(chosenOption, optionsToSelect[0], optionsToSelect[1],
                    optionsToSelect[2], optionsToSelect[3])) {
                System.out.println(RED + "Option chosen by you is not supported by this app. ");
                displayOptions();
                continue;
            }
            break;
        }
        return chosenOption.toLowerCase();
    }

    private static void add() {
    }

    private static void remove() {
    }

    private static void list() {
    }

    private static void exit() {
    }
}