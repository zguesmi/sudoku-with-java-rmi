import java.rmi.RMISecurityManager;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class SudokuClient {

    public SudokuClient(){
        try{
            //locate registry and find the factory interface
            Registry registry = LocateRegistry.getRegistry("localhost",1099);
            IFactory factory =(IFactory) registry.lookup("Factory");
            ISudoku sudoku = (ISudoku) factory.newBoard();
            if(sudoku == null){
                throw new NullPointerException("Sorry, there's already 10 client playing right now!");
            }
            System.out.println("\n	" + (char)27 + "[31;1;4;52m	WELCOME SUDOKU GAME" + (char)27 + "[0m");
            //System.out.println("                 " + (char)27 + "[31;1;4m	SUDOKU GAME" + (char)27 + "[0m\n");

            Scanner scanner = new Scanner(System.in);
            int[] rowColValue = new int[3];
            int[] rowCol = new int[2];
            String line = new String();
            boolean continueWithBoard = true;
            int choice, play = 1 ;

            while(play == 1){
                sudoku.generateSudokuBoard();
                System.out.println(sudoku.clientBoardToString());
                printPrompt("34", "normal mode", "");
                continueWithBoard = true;

                while(continueWithBoard){
                    choice = getValidChoice(scanner, 1, 6);
                    switch(choice){
                        case 1: //insert mode
                            printPrompt("36", "insert Mode", "Enter 3 integers in this form: 'row-column-value' hyphen included");
                            System.out.println("Enter 'q' to exit insert mode");
                            printPrompt("36", "insert Mode", "");
                            while(choice == 1){
                                line = getValidInput(scanner);
                                if(line.equals("q")){
                                    printPrompt("34", "normal mode", "");
                                    choice = 0;
                                }else{
                                    splitLine(line, rowColValue);
                                    String temp = new String(sudoku.insertValue(rowColValue[0], rowColValue[1], rowColValue[2]));
                                    System.out.println(sudoku.clientBoardToString());
                                    printPrompt("36", "insert mode", temp );
                                }
                            }
                            break;
                        case 2: //submit board
                            if(sudoku.isBoardFull()){
                                if(sudoku.iscorrectBoard()){
                                    System.out.println("Congratulations! you solved this sudoku board successfully, be proud you're smart");
                                    continueWithBoard = false;
                                }else{
                                    printPrompt("34", "normal mode", "You haven't solved this board yet, try again!");
                                }
                            }else{
                                printPrompt("34", "normal mode", "You haven't finished all values yet !");
                            }
                            break;
                        case 3: //clear board
                            sudoku.clearBoard();
                            System.out.println(sudoku.clientBoardToString());
                            printPrompt("34", "normal mode", "");
                            break;
                        case 4: //print solution
                            System.out.println(sudoku.solutionBoardToString());
                            continueWithBoard = false;
                            break;
                        case 5: //get help
                            printPrompt("34", "normal mode", "Enter the row and the colomn of the cell that you want to unhide: row-col");
                            getValidRowCol(scanner, rowCol);
                            sudoku.getHelp(rowCol[0], rowCol[1]);
                            System.out.println(sudoku.clientBoardToString());
                            printPrompt("34", "normal mode", "");
                            break;
                        case 6: //quit board
                            continueWithBoard = false;
                            break;
                    }
                }
                printPrompt("34", "normal mode", " Do you want to:   0-Quit   1-Replay with another board");
                play = getValidChoice(scanner, 0, 1);
            }
            factory.decreaseNumberOfUsers();
        }

        catch(NullPointerException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public String getValidInput(Scanner scanner){
        while(true){
            String line = scanner.nextLine();
            if(line.equals("q"))
                return line;
            if(line.length() == 5 && Character.isDigit(line.charAt(0)) && Character.isDigit(line.charAt(2)) && Character.isDigit(line.charAt(4)) && line.charAt(1) == '-' && line.charAt(3) == '-' && line.charAt(0) != '9' && line.charAt(2) != '9')
                return line;
            printPrompt("36","insert mode", "Please respect the form row-colomn-value! row,colomn in [0..8] and value in [1..9]");
        }
    }

    public void getValidRowCol(Scanner scanner, int[] table){
        while(true){
            String line = scanner.nextLine();
            if(line.length() == 3 && Character.isDigit(line.charAt(0)) && Character.isDigit(line.charAt(2)) && line.charAt(1) == '-' && line.charAt(0) != '9' && line.charAt(2) != '9'){
                String[] rowCol = line.split("-");
                table[0] = Integer.parseInt(rowCol[0]);
                table[1] = Integer.parseInt(rowCol[1]);
                break;
            }
            printPrompt("34","normal mode", "Please respect the form row-colomn-value! row,colomn in [0..8]");
        }
    }

    public int getValidChoice(Scanner scanner, int min, int max){
        while(true){
            String line = scanner.nextLine();
            if(line.matches("\\d")){
                int choice = Integer.parseInt(line);
                if (min <= choice && choice <= max)
                    return choice;
            }
            printPrompt("34", "normal mode", "Please enter a valid option !");
        }
    }

    public void printPrompt(String color, String mode, String str){
        System.out.print((char)27 + "["+ color + ";1m" + " [" + mode + "]$ " + (char)27 + "[0m" + str );
        if(!str.equals(""))
            System.out.print("\n" + (char)27 + "["+ color + ";1m" + " [" + mode + "]$ " + (char)27 + "[0m");
    }

    public void splitLine(String line, int[] table){
        String[] rowColValue = new String[3];
        rowColValue = line.split("-");
        table[0] = Integer.parseInt(rowColValue[0]);
        table[1] = Integer.parseInt(rowColValue[1]);
        table[2] = Integer.parseInt(rowColValue[2]);
    }
}
