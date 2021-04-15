import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//for files
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class PredictorModule{
	public static void main(String a[]){
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(new predictor(), 1, 2, TimeUnit.SECONDS);
	}

	public static class predictor implements Runnable {
        @Override
        public void run(){

			String fileName = "observations.txt";
			List<String> lines = new ArrayList<String>();
	        String line = null;
	        String arr = "[";   //this is the truncated observation list to be sent to hmm code

	        try {
	            // FileReader reads text files in the default encoding.
	            FileReader fileReader = new FileReader(fileName);
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            while((line = bufferedReader.readLine()) != null) {
	                lines.add(line);
	            }
	            bufferedReader.close();
	            int numSegsSoFar;
	            String lastObservedBitrate;
	            String lastLine = lines.get(lines.size()-1);
	            String pattern = "(.*)( )([0-9]+)";
                Pattern pat = Pattern.compile(pattern);
                Matcher m = pat.matcher(lastLine);
                if(m.find()){
					numSegsSoFar = Integer.parseInt(m.group(1));
					lastObservedBitrate = m.group(3);                	
                }
                else{
                	numSegsSoFar = 1;
                	lastObservedBitrate = "0";
                }
	            //Thus, we are predicting for segment number numSegsSoFar+1.
	            List<String> arrTruncated = lines.subList(Math.max(0, lines.size()-10) ,lines.size()); 
	            for(int i=0;i<arrTruncated.size();i++){
	            	m = pat.matcher(arrTruncated.get(i));
	            	if(m.find()){
	            		arr = arr+m.group(3)+",";
	            		// arr=arr+arrTruncated.get(i)+",";
	            	}
	            	else{
	            		System.out.println("this is infact error");
	            		arr=arr+"0"+",";
	            	}
	            } 
	            arr = arr.substring(0, arr.length() - 1);
	            arr+="]";
	            System.out.println(arr);


	        	//$$$$$$$$$$TODO:: OBSERATION LIST MUST MATCH WITH THIS REPS ARRAY. - DO CHANGES ACCORDINGLY.
		        // reps = new int[]{45652, 522286, 1032682, 1546902, 2133691, 3078587, 3840360, 4219897};   
            	// String arr = "[1,2,3]";
                ProcessBuilder pb = new ProcessBuilder("python", "multinomial_hmm.py", arr);
                Process p = pb.start();
                // int bitrate; //not needed.
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream())); 
		        String bitrate = null; 
		        while ((bitrate = stdInput.readLine()) != null) 
		        {
		            // System.out.println(bitrate);   //this is the final predicted representation.
		            int seg = numSegsSoFar+5;
		            String str = "("+Integer.toString(seg)+","+bitrate+")\n";
		            //open the segment file and append: (numSegsSoFar+1, bitrate).
		            try { 
			            BufferedWriter segFileWriter = new BufferedWriter(new FileWriter("segmentFile.txt", true)); 
			            segFileWriter.write(str); 
			            segFileWriter.close(); 
			        } 
			        catch (IOException e) { 
			            System.out.println("exception occoured" + e); 
			        } 
		        } 
            }
            catch(Exception e){
                System.out.println("exception has occured here"+e);
            }
        }
    }
}