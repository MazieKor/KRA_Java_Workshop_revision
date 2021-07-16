package pl.mkkor.TaskManagerUpgraded;

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

//3nd Solution - adding some additional options
public class TaskManagerUpgr {
    final static String CSV_FILE = "tasks2.csv";
    final static String[] OPTIONS_TO_SELECT = new String[]{"add", "remove", "list", "save", "exit"};
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
                case "save":
                    save();
                    break;
                case "exit":
                    save();
                    System.out.println(PURPLE_BRIGHT + "Bye, bye");
                    return;
            }
        }
    }

//READ DATA FROM FILE METHOD
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


//DISPLAYING ALL OPTIONS
    private static void displayOptions() {
        System.out.println(BLUE + "Please select an option (type number or option name):" + RESET);
        int counter = 1;
        for (String row : OPTIONS_TO_SELECT) {
            System.out.println(" " + counter+ ". " + row);
            counter++;
        }
    }


//CHOOSING OF THE OPTION
    private static String choosingOption() {
        Scanner scan = new Scanner(System.in);
        String chosenOption;
        while (true) {
            chosenOption = scan.nextLine().trim();
            chosenOption = changeNumberToEquivalentListedOption(chosenOption);
            if (!StringUtils.equalsAnyIgnoreCase(chosenOption, OPTIONS_TO_SELECT[0], OPTIONS_TO_SELECT[1],
                    OPTIONS_TO_SELECT[2], OPTIONS_TO_SELECT[3], OPTIONS_TO_SELECT[4])) {
                System.out.println(RED + "Option chosen by you is not supported by this app. ");
                displayOptions();
                continue;
            }
            break;
        }
        return chosenOption.toLowerCase();
    }

    private static String changeNumberToEquivalentListedOption(String chosenOption) {
        if(NumberUtils.isDigits(chosenOption)){
            try {
                chosenOption = OPTIONS_TO_SELECT[Integer.parseInt(chosenOption)-1];
            } catch (IndexOutOfBoundsException e){
                return chosenOption;
            }
        }
        return chosenOption;
    }


//ADD OPTION
    private static void add() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please add task description. If you want to quit adding option type 'quit'");
        String description = scan.nextLine().trim();
        if(isQuitting(description)) return;
        dataFromFileArray = Arrays.copyOf(dataFromFileArray, dataFromFileArray.length + 1);  //differently than in readDataFromFile() method, I don't use addAll from ArrayUtils here;
        dataFromFileArray[dataFromFileArray.length - 1] = new String[3];
        dataFromFileArray[dataFromFileArray.length - 1][0] = description;
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
            if (!initialFormatValidation(date, dateArray)) continue;
            if (!yearValidation(dateArray)) continue;
            if (!monthValidation(dateArray)) continue;
            if (!dayValidation(dateArray)) continue;
            break;
        }
        return date;
    }

    private static boolean initialFormatValidation(String date, String[] dateArray) {
        if (dateArray.length != 3 || date.length() != 10) {
            System.out.println(RED + "Date format is incorrect. " + RESET);
            return false;
        }
        return true;
    }

    private static boolean yearValidation(String[] dateArray) {
        if (dateArray[0].length() != 4 || !StringUtils.isNumeric(dateArray[0])) {
            System.out.println(RED + "You have typed incorrect Year format (not all of the data are numbers or there are too many/ too few signs). " + RESET);
            return false;
        }
        return true;
    }

    private static boolean monthValidation(String[] dateArray) {
        if (dateArray[1].length() != 2 || !NumberUtils.isDigits(dateArray[1])) {
            System.out.println(RED + "You have given incorrect Month format (not all of the signs are numbers or there are too many/ too few signs). " + RESET);
            return false;
        }
        if (Integer.parseInt(dateArray[1]) < 1 || Integer.parseInt(dateArray[1]) > 12) {
            System.out.println(RED + "You have given incorrect Month date (Month date shouldn't be greater than 12 or less than 1). " + RESET);
            return false;
        }
        return true;
    }

    private static boolean dayValidation(String[] dateArray) {
        if (dateArray[2].length() != 2 || !NumberUtils.isDigits(dateArray[2])) {
            System.out.println(RED + "You have given incorrect Day format (not all of the signs are numbers or there are too many/ too few signs). " + RESET);
            return false;
        }
        if (Integer.parseInt(dateArray[2]) < 1 || Integer.parseInt(dateArray[2]) > 31) {
            System.out.println(RED + "You have typed incorrect Day date (Day date shouldn't be greater than 31 or less than 1). " + RESET);
            return false;
        }
        return true;
    }

    private static String importanceAddAndValidation() {
        Scanner scan = new Scanner(System.in);
        String importance;
        while (true) {
            System.out.println("Is your task important? If yes type '1' or 'true', if not type '2' or 'false'");
            importance = scan.nextLine().trim().toLowerCase();
            if(importance.equals("1"))
                importance = "true";
            if(importance.equals("2"))
                importance = "false";
            if (!StringUtils.equalsAny(importance, "true", "false")) {
                System.out.println(RED + "You have given incorrect data. Please try once again. " + RESET);
                continue;
            }
            break;
        }
        return importance;
    }


//REMOVE OPTION
    private static void remove() {
        Scanner scan = new Scanner(System.in);
        String numberToRemove;
        while (true) {
            System.out.println("Please select number to remove from the list. If you want to display list type 'list', if you want to quit remove option type 'quit'.");
            numberToRemove = scan.nextLine().trim();

            if (isQuitting(numberToRemove)) break;

            if (numberToRemove.equalsIgnoreCase("list")) {
                list();
                continue;
            }
            if (!StringUtils.isNumeric(numberToRemove)) {
                System.out.print(RED + "Typed data is not a number. " + RESET);
                continue;
            }
            try {
                dataFromFileArray = ArrayUtils.remove(dataFromFileArray, Integer.parseInt(numberToRemove));
                System.out.println(YELLOW + "Entry number " + numberToRemove + " was removed" + RESET);
                break;
            } catch (IndexOutOfBoundsException e) {
                System.out.print(RED + "Given number doesn't exist. " + RESET);
            }
        }
    }

    private static boolean isQuitting(String isQuitting) {
        if (isQuitting.equals("quit")) {
            System.out.println("You have quitted this option.\n" );
            return true;
        }
        return false;
    }


//LISTING OF ENTRIES OPTION
    private static void list() {
        int counter = 1;
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


//SAVE OPTION
    private static void save() {
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
            System.out.println(RED + "There was a problem with finding or writing to a file. Check directory. " + RED_BOLD_BRIGHT +"Data were not saved" + RESET);
            e.printStackTrace();
        }

    }
}