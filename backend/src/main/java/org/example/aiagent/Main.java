void process(String file) throws IOException {
    try {
        ...
    } finally {
        System.out.println("END");
    }
}

public static void main(String[] args) {
    try {
        process1();
        process2();
        process3();
    } catch (IOException e) {
        System.out.println("Bad input");
    } catch (NumberFormatException e) {
        System.out.println("Bad input");
    } catch (Exception e) {
        System.out.println("Unknown error");
    }
}



    
 
    

