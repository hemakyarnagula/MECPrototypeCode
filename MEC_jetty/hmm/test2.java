import java.io.*;
import java.util.*;
 
class test2{
	public static void main(String a[]){
		try{
		 
			// number1 = [1, 2, 3];
			List<Integer> l2 = new ArrayList<Integer>(); 
			l2.add(1); 
			l2.add(2);
			l2.add(3); 
			// int number2 = 32;
			String arr="[";
			for(int i=Math.max(0, l2.size()-10);i<l2.size();i++){
				arr+=(l2.get(i)).toString()+",";
			}
			arr = arr.substring(0, arr.length() - 1);
			arr+="]";
			// ProcessBuilder pb = new ProcessBuilder("python","test1.py",""+number1,""+number2);
			ProcessBuilder pb = new ProcessBuilder("python","multinomial_hmm.py", arr);
			Process p = pb.start();

			InputStream out = p.getInputStream();
			OutputStream in = p.getOutputStream();

			byte[] buffer = new byte[4000];
			String s = "";
			while (p.isAlive()) {
				int no = out.available();
				if (no > 0) {
					int n = out.read(buffer, 0, Math.min(no, buffer.length));
					s += (new String(buffer, 0, n));
				}
			}
			System.out.println(s);

			/*
			BufferedReader in = new BufferedReader(new PipedInputStreamReader(p.getOutputStream()));
			String str = in.readLine();
            if(str!=null){
                //int ret = new Integer(str).intValue();
                System.out.println("correct string came:" + str);
            }
            else{
                System.out.println("wrong string coming: "+str);
            }
			// System.out.println("The next predicted observation for this seq is : "+ret);*/
		}catch(Exception e){System.out.println(e);}
	}
}
