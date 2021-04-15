//previous version of code. Not to be used.
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

// import org.apache.log4j.Logger;
// import org.apache.log4j.PropertyConfigurator;

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
import org.eclipse.jetty.util.log.StdErrLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 
public class FileServerWithHmm{

    public static HashSet<String> segments = new HashSet<String>();

    public static void main(String[] args) throws Exception{

        //for logging.
        RolloverFileOutputStream os = new RolloverFileOutputStream("yyyy_mm_dd_jcglogging.log", true);
        PrintStream logStream = new PrintStream(os);
        System.setOut(logStream);
        System.setErr(logStream);
        Server server = new Server(9596);

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(new predictor(), 1, 2, TimeUnit.SECONDS);

        // Log.getRootLogger().info("JCG Embedded Jetty logging started", new Object[]{});
        // ServletHandler handler = new ServletHandler();
        // handler.addServletWithMapping(ExitServlet.class, "/*");
        // handler.addFilterWithMapping(RequestFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        // ServletHandler testHandler = new ServletHandler();
        // testHandler.addServletWithMapping(TestServlet.class, "/hey");

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

        // RewriteHandler rewrite = new RewriteHandler();
        // rewrite.setRewriteRequestURI(true);
        // rewrite.setRewritePathInfo(false);
        // rewrite.setOriginalPathAttribute("requestedPath");   

        // RedirectPatternRule redirect = new RedirectPatternRule();
        // redirect.setPattern("/hello");
        // redirect.setLocation("/hey");
        // rewrite.addRule(redirect);

        // HandlerCollection handlerCollection = new HandlerCollection();
        // handlerCollection.addHandler(gzip);
        // handlerCollection.addHandler(handler);
        // handlerCollection.addHandler(rewrite);
        // handlerCollection.addHandler(testHandler);
        // server.setHandler(handlerCollection);
        server.start();
        server.join();
    }

    public static class predictor implements Runnable {
        // static int count=0;
        
        @Override
        public void run(){
            // count+=1;
            Log.getRootLogger().info("oh yeahhhh!", new Object[]{});
        }
    }
    public static class RequestFilter implements Filter {
        
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
                             throws IOException, ServletException {
        
        // RequestWrapper modifiedRequest = null;
            // PropertyConfigurator.configure("log4j.properties");
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {         
                
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                
                
                String requestURI = httpRequest.getRequestURI();

                if(requestURI.matches("/src/main/webapp/bunny_(.*)")){
                    System.out.println("YES_HERE");
                    if(segments.contains(requestURI)){
                        System.out.println("YES_PRESENT");
                    }
                    else{
                        // TimeUnit.SECONDS.sleep(10);
                        try{
                            Thread.sleep(1000);
                        }
                        catch(InterruptedException ex){
                            System.out.println("could not sleep!");
                            Thread.currentThread().interrupt();
                        }
                        System.out.println("NO_PRESENT");
                        segments.add(requestURI);
                    }
                    // System.out.println("###  MATCHED!!! ###");    
                    /* check if this segment is present with the cache,
                       if no, sleep for some time and add to cache.
                       else, just continue (ignoring the whole situation). 
                    */          
                }
                chain.doFilter(request, response);   
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
            response.getWriter().println("<h1>Hello from TestServlett</h1>\n");
            response.getWriter().println("<h1>now exiting me as well</h1>");
        }
    }   
}