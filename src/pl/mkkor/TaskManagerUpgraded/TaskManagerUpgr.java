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

//3rd Solution - upgraded - adding some additional options and validations
public class TaskManagerUpgr {
    final static String CSV_FILE = "tasks2.csv";
    final static String[] OPTIONS_TO_SELECT = new String[]{"add", "remove", "list", "list ordered", "list important", "list important ordered", "save", "exit", "exit w/o save"};
    static String[][] dataFromFileArray;

    static int lengthOfTable = 110;
    static int lengthOfElem1 = 6;
    static int lengthOfElem3 = 12;
    static int lengthOfElem4 = 11;
    static int lengthOfElem2 = lengthOfTable-lengthOfElem1-lengthOfElem3-lengthOfElem4;

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
                    list("List of All Tasks:");
                    break;
                case "list ordered":
                    listOrdered("List of tasks ordered by date, from the earliest:");
                    break;
                case "list important":
                    listImportant("List of important tasks:");
                    break;
                case "list important ordered":
                    listImportantOrdered("List of important tasks ordered by date, from the earliest:");
                    break;
                case "save":
                    save();
                    break;
                case "exit":
                    save();
                    System.out.println(PURPLE_BRIGHT + "Bye, bye");
                    return;
                case "exit w/o save":
                    System.out.println(PURPLE_BRIGHT + "Bye, bye");
                    return;
            }
        }
    }


//READ DATA FROM FILE
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
        System.out.println(BLUE + "\nPlease select an option (type number + a dot or option name, eg. '3.' or 'list'):" + RESET);
        int counter = 1;
        for (String row : OPTIONS_TO_SELECT) {
            System.out.print(counter + "." + row + "  |  ");
            counter++;
        }
        System.out.println("\b\b\b");
    }


//CHOOSING OF AN OPTION
    private static String choosingOption() {
        Scanner scan = new Scanner(System.in);
        String chosenOption;
        while (true) {
            chosenOption = scan.nextLine().trim();
            chosenOption = changeNumberToEquivalentListedOption(chosenOption);
            if (!StringUtils.equalsAnyIgnoreCase(chosenOption, OPTIONS_TO_SELECT[0], OPTIONS_TO_SELECT[1],
                    OPTIONS_TO_SELECT[2], OPTIONS_TO_SELECT[3], OPTIONS_TO_SELECT[4], OPTIONS_TO_SELECT[5], OPTIONS_TO_SELECT[6], OPTIONS_TO_SELECT[7], OPTIONS_TO_SELECT[8])) {
                System.out.println(RED + "Option chosen by you is not supported by this app. ");
                displayOptions();
                continue;
            }
            break;
        }
        return chosenOption.toLowerCase();
    }

    private static String changeNumberToEquivalentListedOption(String chosenOption) {
        if(chosenOption.length()== 2 && chosenOption.charAt(1)=='.') {  //I added this condition to make users typing numbers with a dot. The reason behind that is that inside remove option there is also possibilty to list all entries (to remind which entry should be removed). To list all entries inside remove option user must type 'list' (not a number); but if user will be accustomed to typing 3 (without dot) for listing he/she can type 3 also in remove method, what results in removing number 3 entry (instead of displaying list). So I want to get user accustemd to type number + dot (what, if user mistakenly type in remove option, doesn't remove anything)
            chosenOption = chosenOption.substring(0, 1);
            if (NumberUtils.isDigits(chosenOption)) {
                try {
                    chosenOption = OPTIONS_TO_SELECT[Integer.parseInt(chosenOption) - 1];
                } catch (IndexOutOfBoundsException e) {
                    return chosenOption;
                }
            }
        }
        return chosenOption;
    }


//ADD OPTION
    private static void add() {
        String description = descriptionAddAndValidation();
        if(isQuitting(description)) return;
        dataFromFileArray = Arrays.copyOf(dataFromFileArray, dataFromFileArray.length + 1);  //differently than in readDataFromFile() method, I don't use addAll from ArrayUtils here;
        dataFromFileArray[dataFromFileArray.length - 1] = new String[3];
        dataFromFileArray[dataFromFileArray.length - 1][0] = description;
        dataFromFileArray[dataFromFileArray.length - 1][1] = dateAddAndValidation();
        dataFromFileArray[dataFromFileArray.length - 1][2] = importanceAddAndValidation();
        System.out.println(YELLOW_BRIGHT + "New task was added"+ RESET);
    }

    private static String descriptionAddAndValidation() {
        Scanner scan = new Scanner(System.in);
        String description;
        while(true){
            System.out.println("Please add task description. Don't use commas (,). If you want to quit 'adding task' type 'quit'");
            description = scan.nextLine().trim();
            if(description.length() + 2 > lengthOfElem2){   //I add 2 because in fillInsideOfTable method I add 2-points long marigin at the beginning of description. Without checking the length of a description format of displaying table ('list' option) would be broken
                System.out.println(description);
                System.out.println(RED+"Length of your description: " + description.length() +
                        " signs, is too long; it can't be save. Maximal length is: " + (lengthOfElem2 - 2)+
                        ". Copy your description from above and make it shorter to successful save in a file." + RESET );
                continue;
            }
            if (description.contains(",")) {
                System.out.println(RED + "In description are used commas. Description can't be saved" + RESET);
                continue;
            }
            break;
        }
        return description;
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
            System.out.println("Is your task important? If YES type: '1' or 'true', if NOT type: '2' or 'false'");
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

        removingLoop:
        while (true) {
            System.out.println("Please select number (or numbers) of task you want to remove from the original, full list (not the ordered or only important tasks list). For removing multiple tasks separate numbers with a 1 comma (,) ).\nIf you want to display list: type 'list', if you want to quit 'remove option' type: 'quit'.");
            numberToRemove = scan.nextLine().trim();

            if (isQuitting(numberToRemove)) break;
            if (numberToRemove.equalsIgnoreCase("list")) { list("List of All Tasks:"); continue; }

            String[] tasksToRemove = numberToRemove.split(",");
            trimArrayElements(tasksToRemove);
            if (!areTrimmedArrayElementsDigits(tasksToRemove)) continue removingLoop;
            if (!areElementsToRemoveInTheList(tasksToRemove)) continue removingLoop;

            for (String taskToRemove : tasksToRemove)
                    Arrays.fill(dataFromFileArray, Integer.parseInt(taskToRemove)-1, Integer.parseInt(taskToRemove), null);
            dataFromFileArray = ArrayUtils.removeAllOccurences(dataFromFileArray, null);  //NEW pamiętać ze ArrayUtil robi kopię

            if(tasksToRemove.length>1)
                System.out.println(YELLOW_BRIGHT + "Entry numbers: " + String.join(", ", tasksToRemove) + " were removed" + RESET);
            else
                System.out.println(YELLOW_BRIGHT + "Entry number " + String.join("", tasksToRemove) + " was removed" + RESET);
            break;
        }
    }

    private static boolean isQuitting(String isQuitting) {
        if (isQuitting.equals("quit")) {
            System.out.println("You have quitted this option." );
            return true;
        }
        return false;
    }

    private static void trimArrayElements(String[] tasksToRemove) {
        for (int i = 0; i < tasksToRemove.length; i++) {
            tasksToRemove[i] = tasksToRemove[i].trim();
        }
    }

    private static boolean areTrimmedArrayElementsDigits(String[] tasksToRemove) {
        if(tasksToRemove.length == 0) {
            System.out.println(RED + "You didn't type any element to remove" + RESET);
            return false;
        }
        for (String taskToRemove : tasksToRemove) {
            if (!NumberUtils.isDigits(taskToRemove)) {   //NEW: Uwaga z digitami jeśli potem robię z nimi jakieś działania, np. wpisuję 1 albo 0 - sprawdza, ze jt digit, ale w dalszej części kodu ta liczba jest zmniejszana np. o 1 lub 2 i powstaje iczba minusowa - niby też liczba, ale z -, którego normalnie ta funkcja by nie przepuściała (bo np. index tablicy nie może być minusowy)
                System.out.println(RED + "Not all elements to remove you typed are numbers or there are some empty elements or there are some characters other than 1 comma (eg. space, dot or double comma)" + RESET);
                return false;
            }
        }
        return true;
    }

    private static boolean areElementsToRemoveInTheList(String[] tasksToRemove) {
        for (String taskToRemove : tasksToRemove) {
            if(Integer.parseInt(taskToRemove) - 1 >= dataFromFileArray.length || Integer.parseInt(taskToRemove) == 0 ){
                System.out.println(RED + "Number " + taskToRemove + " you inserted is not on the list." + RESET);
                return false;
            }
        }
        return true;
    }


//LISTING OF ENTRIES OPTION

    private static void listDecorationTop(String title) {
        char decorElement = '\u2588';
        String topElement = new String(new char[lengthOfTable]).replace('\u0000', decorElement);
        String titleLine = new String(new char[lengthOfTable - title.length() - 1]).replace('\u0000', ' ');

        System.out.println(GREEN + "\t " + topElement);
        System.out.println("\t│ " + PURPLE_BOLD + title + titleLine + GREEN + "│");
        System.out.println("\t│" + RED + fillInsideOfTable("No.", "description", "date", "important") + GREEN + "│");
    }

    private static void listDecorationBottom() {
        char decorElement = '■';
        String bottomElement = new String(new char[lengthOfTable]).replace('\u0000', decorElement);
        System.out.println(GREEN + "\t " + bottomElement + RESET);
    }

    private static void list(String title) {
        listDecorationTop(title);

        int counter = 1;
        String description;
        String date;
        String importance;
        for (int i = 0; i < dataFromFileArray.length; i++) {
            description = dataFromFileArray[i][0];   //NEW żeby się dostac do 2. wymiaru nie muszę robić pętli drugiej
            date = dataFromFileArray[i][1];
            importance = dataFromFileArray[i][2];
            System.out.println(GREEN + "\t│" + WHITE_BRIGHT + fillInsideOfTable(String.valueOf(counter),description,date,importance) + GREEN + "│");
            counter++;
        }

        listDecorationBottom();
    }

    private static String fillInsideOfTable(String counter, String description, String date, String importance) {
        String elementCounter = new String(new char[lengthOfElem1]).replace('\u0000', ' ');
        String elementDescription = new String(new char[lengthOfElem2]).replace('\u0000', ' ');
        String elementDate = new String(new char[lengthOfElem3]).replace('\u0000', ' ');
        String elementImportance = new String(new char[lengthOfElem4]).replace('\u0000', ' ');
        int marigin = 2;

        StringBuilder counterLine = new StringBuilder(elementCounter);
        counterLine.replace(4,5,":").replace(marigin,counter.length() + marigin, counter);

        StringBuilder descriptionLine = new StringBuilder(elementDescription);
        descriptionLine.replace(marigin,description.length() + marigin, description);

        StringBuilder dateLine = new StringBuilder(elementDate);
        dateLine.replace(marigin,date.length() + marigin, date);

        StringBuilder importanceLine = new StringBuilder(elementImportance);
        importanceLine.replace(marigin,importance.length() + marigin, importance);

        return String.valueOf(counterLine.append(descriptionLine).append(dateLine).append(importanceLine));
    }

    private static void listOrdered(String title) {
        listDecorationTop(title);
        String[] datesFromArray = extractDimensionFrom2DimArray(1, dataFromFileArray);
        String[] uniqueDatesFromArray = createArrayWithUniqueElements(datesFromArray);

        int counter = 1;
        String description;
        String date;
        String importance;
        for (int i = 0; i < uniqueDatesFromArray.length; i++) {
            for (int j = 0; j < dataFromFileArray.length; j++){
                if(uniqueDatesFromArray[i].equals(dataFromFileArray[j][1])) {      //NEW IF NIE KOńCZY PĘTLI jak stosuję if w pętli a nie dam continue to po warunku sprawdza następne
                    description = dataFromFileArray[j][0];
                    date = dataFromFileArray[j][1];
                    importance = dataFromFileArray[j][2];
                    System.out.println(GREEN + "\t│" + WHITE_BRIGHT + fillInsideOfTable(String.valueOf(counter),description,date,importance) + GREEN + "│");
                    counter++;
                }
            }
        }
        listDecorationBottom();
    }

    private static String[] extractDimensionFrom2DimArray(int indexToExtract, String[][] extractedArray) {
        String[] indexFrom2DimArray = new String[extractedArray.length];
        for (int i = 0; i < extractedArray.length; i++) {
            if(indexToExtract >= extractedArray[i].length)
                throw new ArrayIndexOutOfBoundsException("index you want to extract doesn't exist");
            indexFrom2DimArray[i] = extractedArray[i][indexToExtract];
        }
        return indexFrom2DimArray;
    }

    private static String[] createArrayWithUniqueElements(String[] inputArray) {  //In this workshop I don't want to use Sets or other Collection to get unique values
        if(inputArray.length == 0)
            return inputArray;
        Arrays.sort(inputArray);
        String[] uniqueFromArray = new String[1];
        uniqueFromArray[0] = inputArray[0];
        for (int i = 1; i < inputArray.length; i++) {
            if(inputArray[i].equals(inputArray[i-1]))
                continue;
            uniqueFromArray = ArrayUtils.add(uniqueFromArray, inputArray[i]);
        }
        return uniqueFromArray;
    }

    private static void listImportant(String title) {
        listDecorationTop(title);

        int counter = 1;
        String description;
        String date;
        String importance;
        for (int i = 0; i < dataFromFileArray.length; i++) {
            if(dataFromFileArray[i][2].equals("true")) {
                description = dataFromFileArray[i][0];
                date = dataFromFileArray[i][1];
                importance = dataFromFileArray[i][2];
                System.out.println(GREEN + "\t│" + WHITE_BRIGHT + fillInsideOfTable(String.valueOf(counter),description,date,importance) + GREEN + "│");
                counter++;
            }
        }

        listDecorationBottom();
        System.out.println("\t " + CYAN_UNDERLINED+"There are also " + (dataFromFileArray.length-(counter-1)) + " entries that are not important (importance = false)");
    }

    private static void listImportantOrdered(String title) {
        listDecorationTop(title);

        String[] datesFromArray = extractDimensionFrom2DimArray(1, dataFromFileArray);
        String[] uniqueDatesFromArray = createArrayWithUniqueElements(datesFromArray);

        int counter = 1;
        String description;
        String date;
        String importance;
        for (int i = 0; i < uniqueDatesFromArray.length; i++) {
            for (int j = 0; j < dataFromFileArray.length; j++){
                if(uniqueDatesFromArray[i].equals(dataFromFileArray[j][1]) && dataFromFileArray[j][2].equals("true")) {
                    description = dataFromFileArray[j][0];
                    date = dataFromFileArray[j][1];
                    importance = dataFromFileArray[j][2];
                    System.out.println(GREEN + "\t│" + WHITE_BRIGHT + fillInsideOfTable(String.valueOf(counter),description,date,importance) + GREEN + "│");
                    counter++;
                }
            }
        }
        listDecorationBottom();
        System.out.println("\t " + CYAN_UNDERLINED+"There are also " + (dataFromFileArray.length-(counter-1)) + " entries that are not important (importance = false)");
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
            System.out.println(YELLOW_UNDERLINED + "Inserted data were saved in a file.");
        } catch (IOException e) {
            System.out.println(RED + "There was a problem with finding or writing to a file. Check directory. " + RED_BOLD_BRIGHT +"Data were not saved" + RESET);
            e.printStackTrace();
        }

    }
}