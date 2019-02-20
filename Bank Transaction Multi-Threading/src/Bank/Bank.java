package Bank;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class Bank {
    public static final int INITIAL_BALACE = 1000;
    public static final int TOTAL_ACCOUNTS = 20;
    private List<Account> accountManager = new ArrayList<Account>();
    private final int threadsNumber;
    private BlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(10);
    private final String File;

    public Bank(int threadsNumber, String name){
        this.File = name;
        this.threadsNumber = threadsNumber;
        for (int i = 0;i<TOTAL_ACCOUNTS;i++){
            accountManager.add(new Account(i,INITIAL_BALACE));
        }
    }
    public void getTransactions() throws InterruptedException {
        File file = new File(this.File);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner stream = new Scanner(is);
        while (stream.hasNextLine()) {
            String[] split =  stream.nextLine().split(" ");
            Transaction tran = new Transaction(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]));
            queue.put(tran);
        }
        for (int i = 0;i<threadsNumber;i++){
            queue.put(Transaction.NULLTRANS);
        }
    }

    public void displayAccounts() {
        for(int i=0; i < accountManager.size();i++) {
            System.out.println(accountManager.get(i));
        }
    }

    public void processTransactions()throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threadsNumber);
        for (int i = 0; i < threadsNumber; i++) {
            System.out.println("assigned workers...");
            pool.submit(new worker());
        }
        pool.shutdown();
        getTransactions();
        if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("Threads pool has not been shutdown");
        } else {
            displayAccounts();
        }
    }


    private class worker implements Runnable {

        public void run(){
            System.out.println("Worker starts to process the transaction...");
            boolean isRunning = true;
            try {
                while (isRunning) {
                    System.out.println("Acquiring transaction records...");
                    Transaction tran = queue.take();
                    if (!Transaction.NULLTRANS.equals(tran)) {
                    	synchronized (tran) {
                    		process(tran);
                    	}
                    	
                    }else{
                        isRunning = false;
                    }
                }
            }
            catch (InterruptedException e) {
                    e.printStackTrace();
             } finally {
                System.out.println(Thread.currentThread().getName() + " Finished");
            }
        }
        public void process(Transaction tran){
            int amount = tran.getAmount();
            Account from = accountManager.get(tran.getWithdrawID());
            Account to = accountManager.get(tran.getDepositID());
            synchronized (from) {
                from .withdraw(amount);
            }
            synchronized (to){
                to.Deposit(amount);
            }
        }
    }


    public static void main(String[] args) {

        Bank bank = new Bank(5, "C:\\Users\\haoli\\eclipse-workspace\\Bank\\src\\Bank\\5k.txt");
        try {
            bank.processTransactions();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

    
    
