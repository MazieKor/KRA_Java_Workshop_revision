package pl.mkkor.TaskManagers;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static pl.mkkor.TaskManagers.ConsoleColors.*;

//2nd Solution - with class fields instead of local variables as in 1st Solution (class TaskManager)
public class TaskManager2 {
    final static String CSV_FILE = "tasks2.csv";
    final static String[] OPTIONS_TO_SELECT = new String[]{"add", "remove", "list", "exit"};
    static String[][] dataFromFileArray;

    public static void main(String[] args) {
        try {
            readDataFromFile();
        } catch (FileNotFoundException e) {
            System.out.println("Given file: '" + CSV_FILE + "' has not been found. Check file directory");
            return;
        }

        while (true) {
            displayOptions();
            String chosenOption = choosingOption();

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
                    System.out.println(PURPLE_BRIGHT + "Bye, bye");
                    return;
            }
        }
    }

    private static void readDataFromFile() throws FileNotFoundException {
        dataFromFileArray = new String[0][];
        File csvFileDirectory = new File(CSV_FILE);

        Scanner scan = new Scanner(csvFileDirectory);
        for (int i = 0; scan.hasNextLine(); i++) {
            dataFromFileArray = ArrayUtils.addAll(dataFromFileArray, new String[1][]);
            dataFromFileArray[i] = scan.nextLine().trim().split(", ");
        }
        scan.close();
    }

    private static void displayOptions() {
        System.out.println(BLUE + "Please select an option (choose only one option):" + RESET);
        for (String row : OPTIONS_TO_SELECT) {
            System.out.println(row);
        }
    }

    private static String choosingOption() {
        Scanner scan = new Scanner(System.in);
        String chosenOption;
        while (true) {
            chosenOption = scan.nextLine().trim();
            if (!StringUtils.equalsAnyIgnoreCase(chosenOption, OPTIONS_TO_SELECT[0], OPTIONS_TO_SELECT[1],
                    OPTIONS_TO_SELECT[2], OPTIONS_TO_SELECT[3])) {
                System.out.println(RED + "Option chosen by you is not supported by this app. ");
                displayOptions();
                continue;
            }
            break;
        }
        return chosenOption.toLowerCase();
    }

    private static void add() {
        Scanner scan = new Scanner(System.in);
        dataFromFileArray = Arrays.copyOf(dataFromFileArray, dataFromFileArray.length + 1);  //differently than in readDataFromFile() method, I don't use addAll from ArrayUtils here;
        dataFromFileArray[dataFromFileArray.length - 1] = new String[3];
        System.out.println("Please add task description");
        dataFromFileArray[dataFromFileArray.length - 1][0] = scan.nextLine().trim();
        dataFromFileArray[dataFromFileArray.length - 1][1] = dateAddAndValidation();
        dataFromFileArray[dataFromFileArray.length - 1][2] = importanceAddAndValidation();
    }

    private static String dateAddAndValidation() {         //In this task I want to validate with loops, Array and NumberUtils and without date API or regex
        Scanner scan = new Scanner(System.in);
        String date;
        while (true) {
            System.out.println("Please add task due date(YYYY-MM-DD)");
            date = scan.nextLine().trim();
            String[] dateArray = date.split("-");
            if (dateArray.length != 3 || date.length() != 10) {
                System.out.println(RED + "Date format is incorrect. " + RESET);
                continue;
            }
            if (dateArray[0].length() != 4 || !StringUtils.isNumeric(dateArray[0])) {
                System.out.println(RED + "You have typed incorrect Year format (not all of the data are numbers or there are too many/ too few signs). " + RESET);
                continue;
            }
            if (dateArray[1].length() != 2 || !NumberUtils.isDigits(dateArray[1])) {
                System.out.println(RED + "You have given incorrect Month format (not all of the signs are numbers or there are too many/ too few signs). " + RESET);
                continue;
            }
            if (Integer.parseInt(dateArray[1]) < 1 || Integer.parseInt(dateArray[1]) > 12) {
                System.out.println(RED + "You have given incorrect Month date (Month date shouldn't be greater than 12 or less than 1). " + RESET);
                continue;
            }
            if (dateArray[2].length() != 2 || !NumberUtils.isDigits(dateArray[2])) {
                System.out.println(RED + "You have given incorrect Day format (not all of the signs are numbers or there are too many/ too few signs). " + RESET);
                continue;
            }
            if (Integer.parseInt(dateArray[2]) < 1 || Integer.parseInt(dateArray[2]) > 31) {
                System.out.println(RED + "You have typed incorrect Day date (Day date shouldn't be greater than 31 or less than 1). " + RESET);
                continue;
            }
            break;
        }
        return date;
    }

    private static String importanceAddAndValidation() {
        Scanner scan = new Scanner(System.in);
        String importance;
        while (true) {
            System.out.println("Is your task important: true/false");
            importance = scan.nextLine().trim().toLowerCase();
            if (!StringUtils.equalsAny(importance, "true", "false")) {
                System.out.println(RED + "You have given incorrect data (neither true nor false). Please try once again. " + RESET);
                continue;
            }
            break;
        }
        return importance;
    }

    private static void remove() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please select number to remove from the list. If you want to display list type 'list', if you want to quit remove option type 'quit'.");
        String numberToRemove;
        while (true) {
            numberToRemove = scan.nextLine().trim();
            if (numberToRemove.equalsIgnoreCase("list")) {
                list();
                System.out.println("Please select number to remove from the list");
                continue;
            }
            if (numberToRemove.equalsIgnoreCase("quit")) {
                System.out.println("You are quitting the remove option.");
                break;
            }
            if (!StringUtils.isNumeric(numberToRemove)) {
                System.out.println(RED + "Typed data is not a number. Please select number to remove from the list." + RESET + " If you want to display list type 'list', if you want to quit remove option type 'quit'.");
                continue;
            }
            try {
                dataFromFileArray = ArrayUtils.remove(dataFromFileArray, Integer.parseInt(numberToRemove));
                System.out.println(YELLOW + "Entry number " + numberToRemove + " was removed" + RESET);
                break;
            } catch (IndexOutOfBoundsException e) {
                System.out.println(RED + "Given number doesn't exist. Please type a number from the list." + RESET + " If you want to display list type 'list', if you want to quit remove option type 'quit'.");
            }
        }
    }

    private static void list() {
        int counter = 0;
        System.out.println(PURPLE + "List: " + RESET);
        for (String[] array : dataFromFileArray) {
            System.out.print(" " + counter + " : ");
            for (String index : array) {
                System.out.print(index + " ");
            }
            System.out.println("\b");
            counter++;
        }
        System.out.println();
    }

    private static void exit() {
        try (FileWriter fileWriter = new FileWriter(CSV_FILE)) {
            for (int i = 0; i < dataFromFileArray.length; i++) {
                for (int j = 0; j < dataFromFileArray[i].length; j++) {
                    if (j == dataFromFileArray[i].length - 1)
                        fileWriter.append(dataFromFileArray[i][j]).append("\n");
                    else
                        fileWriter.append(dataFromFileArray[i][j]).append(", ");
                }
            }
            System.out.println(WHITE_UNDERLINED + "All data were saved in a file");
        } catch (IOException e) {
            System.out.println(RED + "There was a problem with finding or writing to a file. Check directory" + RESET);
            e.printStackTrace();
        }

    }
}