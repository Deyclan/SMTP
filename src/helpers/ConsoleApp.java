package helpers;

public abstract class ConsoleApp {

    public ConsoleApp(){

    }

    public static void setConsoleColor(ConsoleColor color){
        System.out.print(color);
    }

    public static void resetConsoleColor(){
        System.out.print(ConsoleColor.ANSI_RESET);
    }

    public static void log(String message){
        resetConsoleColor();
        System.out.println(message);
    }

    public static void log(String message, ConsoleColor color){
        setConsoleColor(color);
        System.out.println(message);
        resetConsoleColor();
    }
}
