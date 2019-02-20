package Cracker;
import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
	private static List<String> synchList = Collections.synchronizedList(new ArrayList<String>());
	public static int WORK_NUMBER;
	public static int MAX_LENGTH;
	public static String HASHCODE;
	private static byte[] HASHBYTE;
	private static CountDownLatch WaitChildProcess;
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	
	public static void possiblePasswords(int n,String chosen) {
		if (n>MAX_LENGTH) {
			return;
		}
		MessageDigest digest;
    	try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(chosen.getBytes(StandardCharsets.UTF_8));
			if (Arrays.equals(hash,HASHBYTE)) {
				synchList.add(chosen);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int j = 0;j < CHARS.length;j++) {
			if (chosen.length()>=MAX_LENGTH) {
				return;
			}else {
				String newstr = chosen + CHARS[j];	
				possiblePasswords(n+1,newstr);
			}
		}
		return;
	}

    public static void process()throws InterruptedException {
    	worker[] workers = new worker[WORK_NUMBER];
        for (int i = 0; i < WORK_NUMBER-1; i++) {
            int start = i*MAX_LENGTH;
            char[] symbolsPart = Arrays.copyOfRange(CHARS, start, start+MAX_LENGTH);
            workers[i] = new worker(symbolsPart);
        }
        char[] symbolsPart = Arrays.copyOfRange(CHARS, MAX_LENGTH*(WORK_NUMBER-1), CHARS.length);
        workers[WORK_NUMBER-1] = new worker(symbolsPart);
		for(int i = 0; i < WORK_NUMBER; i++) {
			workers[i].start();
		}
		try {
			WaitChildProcess.await();
			for(String each : synchList) {
				System.out.println(each);
			}
			System.out.println("all done");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
    
    public static void crack(char[] symbols) throws NoSuchAlgorithmException {
    	for (char ch : symbols) {
    		String chosen = String.valueOf(ch);
	    	possiblePasswords(1,chosen);
        }
	}
  
    private static class worker extends Thread {
    	private char[] symbols;

    	public worker(char[] part) {
    		this.symbols = part;
		}
    	@Override
        public void run(){
//            System.out.println("Worker starts to decode...");
            try {
            	synchronized (symbols) {
            		crack(this.symbols);
				}
            	WaitChildProcess.countDown();
        	}
            catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
             } finally {
//                System.out.println(Thread.currentThread().getName() + " Finished");
            }
        }
       
    }
	
    public static void main(String[] args) throws Exception {
    	if (args.length<2) {
    		if(args.length!=1) {
    			throw new Exception ("Usage: <password>.");
    		}
	    	try {
	    		MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    		byte[] hash = digest.digest(args[0].getBytes(StandardCharsets.UTF_8));
	    		String hashedString = hexToString(hash);
	    		System.out.println(hashedString);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else {
			if (args.length!=3) {
				throw new Exception ("Usage: <hashcode> <maxLength> <numThreads>.");
			}
			HASHCODE = args[0];
			HASHBYTE = hexToArray(HASHCODE);
			MAX_LENGTH = Integer.parseInt(args[1]);
			WORK_NUMBER = Integer.parseInt(args[2]);
			WaitChildProcess = new CountDownLatch(WORK_NUMBER);
			process();
		}

    }
	// possible test values:
	// a ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb
	// fm 440f3041c89adee0f2ad780704bcc0efae1bdb30f8d77dc455a2f6c823b87ca0
	// a! 242ed53862c43c5be5f2c5213586d50724138dea7ae1d8760752c91f315dcd31
	// xyz 3608bca1e44ea6c4d268eb6db02260269892c0b42b86bbf1e77a6fa16c3c9282

}