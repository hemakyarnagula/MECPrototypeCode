package org.example;

import java.io.*;
import java.lang.String;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletHandler;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.RolloverFileOutputStream;
import org.eclipse.jetty.util.log.Log;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;

import org.eclipse.jetty.http.pathmap.MappedResource;
 
public class FileServer
{
    public static HashSet<String> segments = new HashSet<String>();
    public static List<Integer> observations = new ArrayList<Integer>();
    public static String observationFileName = "observations.txt";  
    static File observationFile = new File(observationFileName);
    static BufferedWriter obsFileWriter = null;
    static FileWriter fw = null;
    
    // static File hmmOutputFile = new File("hmmOutput.txt");

    public static void main(String[] args) throws Exception{

        //for logging.
        RolloverFileOutputStream os = new RolloverFileOutputStream("yyyy_mm_dd_jcglogging.log", true);
        PrintStream logStream = new PrintStream(os);
        System.setOut(logStream);
        System.setErr(logStream);


        //create observation file
        try{
            System.out.println("creating the file");
            if(observationFile.createNewFile()){
                System.out.println("ok created");
            }
            else{
                System.out.println("it existed");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }

        //port on which the server listens
        Server server = new Server(9597);

        Log.getRootLogger().info("JCG Embedded Jetty logging has started.", new Object[]{});

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource("."));
        server.setHandler(context);

        EnumSet<DispatcherType> dispatches = EnumSet.allOf(DispatcherType.class);
        FilterHolder holder = new FilterHolder(RequestFilter.class);
        holder.setName("RequestFilter");
        context.addFilter(holder, "*.m4s", dispatches);
        // context.addFilter(RequestFilter, "(*.)bunny_(.*)",dispatches);

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // It is important that this is last.
        ServletHolder holderDef = new ServletHolder("default",DefaultServlet.class);
        holderDef.setInitParameter("dirAllowed","true");
        context.addServlet(holderDef,"/");

        server.start();
        server.join();
    }

    public static class RequestFilter implements Filter {
        
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
                             throws IOException, ServletException {
        
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {         
                
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                //parsing the request URI to get perceived bitrate
                String requestURI = httpRequest.getRequestURI();
                System.out.println("received URI is " + requestURI);
                String pattern = "(.*?)([0-9]+)(bps/BigBuckBunny_2s)([0-9]+)(.m4s)(graduate)([0-9]+)(.m4s)";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(requestURI);
                if(m.find()){
                    requestURI = m.group(1)+m.group(2)+m.group(3)+m.group(4)+m.group(5);
                    String fb = m.group(7);     //bandwidth perceived by the client.
                    String segNo = m.group(4);
                    String requestedBitrate = m.group(2);
                    String codedBitrate="0";
                    System.out.println("requested segment bitrate is " + requestedBitrate);
                    System.out.println("perceived bitrate is "+fb);
                    System.out.println("changed URI is now "+requestURI);
                    fb+="\n";
                    try{
                        fw = new FileWriter(observationFile.getAbsoluteFile(), true);
                        obsFileWriter = new BufferedWriter(fw);
                        if (Integer.parseInt(requestedBitrate)<45652){
                            obsFileWriter.write(segNo + " 0\n");
                            codedBitrate = "0";
                        }
                        else if (Integer.parseInt(requestedBitrate)<522286){
                            obsFileWriter.write(segNo + " 1\n");
                            codedBitrate = "1";
                        }
                        else if (Integer.parseInt(requestedBitrate)<1032682){
                            obsFileWriter.write(segNo + " 2\n");
                            codedBitrate = "2";
                        }
                        else if (Integer.parseInt(requestedBitrate)<1546902){
                            obsFileWriter.write(segNo + " 3\n");
                            codedBitrate = "3";
                        }
                        else if (Integer.parseInt(requestedBitrate)<2133691){
                            obsFileWriter.write(segNo + " 4\n");
                            codedBitrate = "4";
                        }
                        else if (Integer.parseInt(requestedBitrate)<3078587){
                            obsFileWriter.write(segNo + " 5\n");
                            codedBitrate = "5";
                        }
                        else if (Integer.parseInt(requestedBitrate)<3526922){
                            obsFileWriter.write(segNo + " 6\n");
                            codedBitrate = "6";
                        }
                        else{
                            obsFileWriter.write(segNo + " 7\n");
                            codedBitrate = "7";
                        }
                        System.out.println("wrote observation file for segment number "+segNo);
                        // obsFileWriter.write(requestedBitrate+'\n');   //WRONG ACTUALLY. SEE LATER. PERCEIVED IS ACTUALLY FB: 
                        //problem because of the way arb throuhgpu rule has been written. Didn't find solution yet.
                    }
                    catch(Exception e){
                        System.out.println("error in obseration writing "+ e);
                    }
                    finally{
                        try{
                            if (obsFileWriter != null)
                                obsFileWriter.close();
                            if (fw != null)
                                fw.close();
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                    //check if requested seegment is present in segment file(writeen into by the predictor). 
                    if(requestURI.matches("/src/main/webapp/bunny_(.*)")){
                        File file = new File("segmentFile.txt");

                        try {
                            Scanner scanner = new Scanner(file);

                            int lineNum=0, flag=0,  numCorectlyPrefetched=0;
                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                lineNum++;
                                if(line=="("+segNo+","+codedBitrate+")") { 
                                    numCorectlyPrefetched+=1;
                                    System.out.println("YES This segment is present");
                                    flag=1;
                                    break;
                                }
                            }
                            if(flag==0){
                                    try{
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                                        System.out.println(dtf.format(LocalDateTime.now())); 
                                        Thread.sleep(600);
                                        System.out.println(dtf.format(LocalDateTime.now()));

                                    }
                                    catch(InterruptedException ex){
                                        Thread.currentThread().interrupt();
                                    }
                                    System.out.println("NO_PRESENT");
                            }
                        } 
                        catch(FileNotFoundException e) { 
                            System.out.println("segment file not found");
                        }          
                    }
                    request.getRequestDispatcher(requestURI).forward(request, response);
                }
                else{
                    chain.doFilter(request, response);   
                }
            }
        }
        public void destroy() {
            // Do nothing
        }

        public void init(FilterConfig config) throws ServletException {
            // Do nothing
        }
    }

    @SuppressWarnings("serial")
    public static class ExitServlet extends HttpServlet{
        @Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException,
                                                            IOException
        {
            System.out.println("finally working!");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello from HelloServlet</h1>\n");
            response.getWriter().println("<h1>now exiting</h1>");
        }
    }

    @SuppressWarnings("serial")
    public static class TestServlet extends HttpServlet{
        @Override
        protected void doGet( HttpServletRequest request,
                              HttpServletResponse response ) throws ServletException,
                                                            IOException
        {
            System.out.println("finally working!");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello from TestServlet</h1>\n");
            response.getWriter().println("<h1>now exiting me as well</h1>");
        }
    }   
}
