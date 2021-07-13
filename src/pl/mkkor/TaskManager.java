package pl.mkkor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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

        while(true) {
            String[] optionsToSelect = displayOptions();
            String chosenOption = choosingOption(optionsToSelect);

            switch (chosenOption) {
                case "add":
                    dataFromFileArray = add(dataFromFileArray);
                    break;
                case "remove":
                    dataFromFileArray = remove(dataFromFileArray);
                    break;
                case "list":
                    list(dataFromFileArray);
                    break;
                case "exit": exit(dataFromFileArray, csvFile);
                    System.out.println(RED + "Bye, bye");
                    return;
            }
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
        String[] optionsToSelect = new String[]{"add", "remove", "list", "exit"};
        System.out.println(BLUE + "Please select an option (choose only one option):" + RESET);
        for (String row : optionsToSelect) {
            System.out.println(row);
        }
        return optionsToSelect;
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

    private static String[][] add(String[][] dataFromFileArray) {
        Scanner scan = new Scanner(System.in);
        dataFromFileArray = Arrays.copyOf(dataFromFileArray, dataFromFileArray.length+1);  //differently than in readDataFromFile() method, I don't use addAll from ArrayUtils here;
        dataFromFileArray[dataFromFileArray.length-1] = new String[3];  //NEW muszę inicjalizować 2. wymiar, bez tego jt null
        System.out.println("Please add task description");
        dataFromFileArray[dataFromFileArray.length-1][0] = scan.nextLine();
        dataFromFileArray[dataFromFileArray.length-1][1] = dateAddAndValidation();
        dataFromFileArray[dataFromFileArray.length-1][2] = importanceAddAndValidation();
        return dataFromFileArray;
    }

    private static String dateAddAndValidation() {         //In this task I want to validate with loops, Array and NumberUtils and without date API or regex
        Scanner scan = new Scanner(System.in);
        String date;
        while(true) {
            System.out.println("Please add task due date(YYYY-MM-DD)");
            date = scan.nextLine().trim();
            String[] dateArray = date.split("-");
            if(dateArray.length != 3){
                System.out.print("Date format is incorrect. ");
                continue;
            }
            if(dateArray[0].length() != 4 || !NumberUtils.isDigits(dateArray[0])){
                System.out.print("You have given incorrect Year format (not all of the data are numbers or there are too many/ too few numbers). ");
                continue;
            }
            if(dateArray[1].length() != 2 || !NumberUtils.isDigits(dateArray[1])){
                System.out.print("You have given incorrect Month format (not all of the data are numbers or there are too many/ too few numbers). ");
                continue;
            }
            if(Integer.parseInt(dateArray[1]) < 1 || Integer.parseInt(dateArray[1]) >12){
                System.out.print("You have given incorrect Month date (Month date shouldn't be greater than 12 or less than 1). ");
                continue;
            }
            if(!NumberUtils.isDigits(dateArray[2])){
                System.out.print("You have given incorrect Day format (not all of the data are numbers). ");
                continue;
            }
            if(Integer.parseInt(dateArray[2]) < 1 || Integer.parseInt(dateArray[2]) >31){
                System.out.print("You have given incorrect Day date (Day date shouldn't be greater than 31 or less than 1). ");
                continue;
            }
           break;
        }
        return date;
    }
    private static String importanceAddAndValidation() {
        Scanner scan = new Scanner(System.in);
        String importance;
        while(true){
            System.out.println("Is your task important: true/false");
            importance = scan.nextLine().trim().toLowerCase();
            if(!StringUtils.equalsAny(importance,"true", "false")){
                System.out.println("You have given incorrect data (neither true nor false). Please try once again. ");
                continue;
            }
            break;
        }
        return importance;
    }

    private static String[][] remove(String[][] dataFromFileArray) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please select number to remove from the list. If you want to display list type \'list\', if you want to quit remove option type \'quit\'.");
        String numberToRemove;
        while (true){
            numberToRemove = scan.nextLine().trim();
            if(numberToRemove.equalsIgnoreCase("list")){
                list(dataFromFileArray);
                System.out.println("Please select number to remove from the list");
                continue;
            }
            if(numberToRemove.equalsIgnoreCase("quit")){
                System.out.println("You are quitting the option.");
                break;
            }
            if(!StringUtils.isNumeric(numberToRemove)){
                System.out.println("Inserted data is not a number. Please select number to remove from the list. If you want to display list type \'list\', if you want to quit remove option type \'quit\'.");
                continue;
            }
            try{
                dataFromFileArray = ArrayUtils.remove(dataFromFileArray, Integer.parseInt(numberToRemove));
                System.out.println("Entry number " + numberToRemove + " was removed");
                break;
            } catch (IndexOutOfBoundsException e){
                System.out.println("Given number doesn't exist. Please type a number from the list. If you want to display list type \'list\', if you want to quit remove option type \'quit\'.");
                continue;
            }
        }
        return dataFromFileArray;
    }

    private static void list(String[][] dataFromFileArray) {
        int counter = 0;
        System.out.println(CYAN + "List: ");
        for (String[] array : dataFromFileArray) {
            System.out.print(counter + " : ");
            for (String index : array) {
                System.out.print(index + " ");
            }
            System.out.println("\b");
        }
        System.out.println();
    }

    private static void exit(String[][] dataFromFileArray, String csvFile) {
        try(FileWriter fileWriter = new FileWriter(csvFile)) {
            for (int i = 0; i < dataFromFileArray.length; i++) {
                for (int j = 0; j < dataFromFileArray[i].length; j++) {
                    if(j == dataFromFileArray[i].length - 1)
                        fileWriter.append(dataFromFileArray[i][j]).append("\n");
                    else
                        fileWriter.append(dataFromFileArray[i][j]).append(", ");
                }
            }
        } catch (IOException e) {
            System.out.println("There was a problem with finding or writing to a file. Check directory");
            e.printStackTrace();
            return;
        }

    }
}