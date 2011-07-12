/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.infinispan.arquillian.container.managed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * A
 * {@link org.jboss.arquillian.container.spi.client.container.DeployableContainer}
 * implementation for the Infinispan container.
 * 
 * @author <a href="mailto:mgencur@redhat.com">Martin Gencur</a>
 * @version $Revision: $
 */
public class InfinispanContainer implements DeployableContainer<InfinispanConfiguration>
{
   private static Logger log = Logger.getLogger(InfinispanContainer.class.getName());

   private Process process;

   private InfinispanConfiguration configuration;

   @Override
   public ProtocolDescription getDefaultProtocol()
   {
      return new ProtocolDescription("Local");
   }

   @Override
   public Class<InfinispanConfiguration> getConfigurationClass()
   {
      return InfinispanConfiguration.class;
   }

   @Override
   public void setup(InfinispanConfiguration configuration)
   {
      this.configuration = configuration;
   }

   @Override
   public void start() throws LifecycleException
   {
      final String ispnHome = configuration.getIspnHome();
      final int jmxPort = configuration.getJmxPort();
      final String mainClassName = "org.infinispan.server.core.Main";
      final String preferIPv4 = "-Djava.net.preferIPv4Stack=true";
      final String log4jConfig = "-Dlog4j.configuration=file://" + ispnHome + File.separator + "etc" + File.separator + "log4j.xml";
      final String jvmJmxSettings = "-Dcom.sun.management.jmxremote.port=" + jmxPort + 
                                    " -Dcom.sun.management.jmxremote.authenticate=false" +
                                    " -Dcom.sun.management.jmxremote.ssl=false";

      try
      {
         List<String> cmd = new ArrayList<String>();

         cmd.add("java");
         cmd.add("-classpath");

         List<File> allLibs = new ArrayList<File>();
         allLibs.addAll(getJars(new File(ispnHome), false));
         allLibs.addAll(getJars(new File(constructFilePath(ispnHome, "lib")), true));

         if ("memcached".equals(configuration.getProtocol()))
         {
            allLibs.addAll(getJars(new File(constructFilePath(ispnHome, "modules", "memcached")), true));
         }
         else if ("hotrod".equals(configuration.getProtocol()))
         {
            allLibs.addAll(getJars(new File(constructFilePath(ispnHome, "modules", "hotrod")), true));
         }
         else if ("websocket".equals(configuration.getProtocol()))
         {
            allLibs.addAll(getJars(new File(constructFilePath(ispnHome, "modules", "websocket")), true));
         }

         cmd.add(constructClassPath(allLibs));

         splitAndAdd(cmd, configuration.getJavaVmArguments());
         
         cmd.add(preferIPv4);
         cmd.add(log4jConfig);

         splitAndAdd(cmd, jvmJmxSettings);

         cmd.add(mainClassName);

         for (String param : constructProgramArgs())
         {
            cmd.add(param);
         }

         log.info("Starting container with: " + cmd.toString());
         ProcessBuilder processBuilder = new ProcessBuilder(cmd);
         processBuilder.redirectErrorStream(true);
         process = processBuilder.start();
         new Thread(new ConsoleConsumer()).start();
         waitForServerToStart(configuration.getStartupTimeoutInSeconds());
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not start managed container", e);
      }
   }

   private void splitAndAdd(List<String> cmd, String arg)
   {
      for (String param : arg.split("\\s+"))
      {
         cmd.add(param);
      }
   }

   private void waitForServerToStart(int timeoutInSeconds)
   {
      try
      {
         log.info("Waiting for server to start...");
         Thread.sleep(timeoutInSeconds * 1000);
      }
      catch (InterruptedException e)
      {
         log.warning("Interrupted while waiting for the server to start");
      }
   }

   private List<String> constructProgramArgs()
   {
      List<String> args = new ArrayList<String>();
      args.add("--host=" + configuration.getHost());
      if (configuration.getPort() != 0)
      {
         args.add("--port=" + configuration.getPort());
      }
      if (configuration.getMasterThreads() != 0)
      {
         args.add("--master_threads=" + configuration.getMasterThreads());
      }
      if (configuration.getWorkerThreads() != 0)
      {
         args.add("--worker_threads=" + configuration.getWorkerThreads());
      }
      if (configuration.getCacheConfig() != null)
      {
         args.add("--cache_config=" + configuration.getCacheConfig());
      }
      args.add("--protocol=" + configuration.getProtocol());
      args.add("--idle_timeout=" + configuration.getIdleTimeout());
      args.add("--tcp_no_delay=" + configuration.isTcpNoDelay());
      if (configuration.getSendBufSize() != 0)
      {
         args.add("--send_buf_size=" + configuration.getSendBufSize());
      }
      if (configuration.getRecvBufSize() != 0)
      {
         args.add("--recv_buf_size=" + configuration.getRecvBufSize());
      }
      if (configuration.getProxyHost() != null)
      {
         args.add("--proxy_host=" + configuration.getProxyHost());
      }
      if (configuration.getProxyPort() != 0)
      {
         args.add(" --proxy_port=" + configuration.getProxyPort());
      }
      args.add("--topo_lock_timeout=" + configuration.getTopoLockTimeout());
      args.add("--topo_repl_timeout=" + configuration.getTopoReplTimeout());
      args.add("--topo_state_transfer=" + configuration.isTopoStateTransfer());
      if (configuration.getCacheManagerClass() != null)
      {
         args.add("--cache_manager_class=" + configuration.getCacheManagerClass());
      }
      return args;
   }

   @Override
   public void stop() throws LifecycleException
   {
      try
      {
         if (process != null)
         {
            process.destroy();
            process.waitFor();
            process = null;
         }
      }
      catch (Exception e)
      {
         throw new LifecycleException("Could not stop container", e);
      }
   }

   @Override
   public void deploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Deploying to the Infinispan server is not possible");
   }

   @Override
   public void undeploy(Descriptor descriptor) throws DeploymentException
   {
      throw new UnsupportedOperationException("Undeploying from the Infinispan server is not possible");
   }

   @Override
   public ProtocolMetaData deploy(final Archive<?> archive) throws DeploymentException
   {
      throw new UnsupportedOperationException("Deploying to the Infinispan server is not possible");
   }

   @Override
   public void undeploy(final Archive<?> archive) throws DeploymentException
   {
      throw new UnsupportedOperationException("Undeploying from the Infinispan server is not possible");
   }

   private List<File> getJars(File startDir, boolean recursive) throws Exception
   {
      validateDirectory(startDir);
      List<File> result = new ArrayList<File>();

      File[] jars = startDir.listFiles(new FilenameFilter()
      {
         private Pattern jarZipPattern = Pattern.compile(".*\\.([Jj][Aa][Rr]|[Zz][Ii][Pp])$"); // *.jar,*.zip

         public boolean accept(File dir, String name)
         {
            Matcher jarZipMatcher = jarZipPattern.matcher(name);
            return jarZipMatcher.matches();
         }
      });

      List<File> jarList = Arrays.asList(jars);
      result.addAll(jarList);

      if (!recursive)
      {
         return result;
      }

      File[] dirs = startDir.listFiles(new FileFilter()
      {
         public boolean accept(File file)
         {
            return file.isDirectory() && file.canRead();
         }
      });

      for (File file : dirs)
      {
         List<File> deeperList = getJars(file, true);
         result.addAll(deeperList);
      }

      return result;
   }

   private void validateDirectory(File startDir)
   {
      if (!(startDir.isDirectory() && startDir.canRead()))
      {
         throw new IllegalArgumentException("The specified directory does not exist or cannot be read: " + startDir.getAbsolutePath());
      }
   }

   private String constructFilePath(String... strings)
   {
      final String SEP = File.separator;
      StringBuilder sb = new StringBuilder();
      for (String s : strings)
      {
         sb.append(s).append(SEP);
      }
      return sb.substring(0, sb.lastIndexOf(SEP));
   }

   private String constructClassPath(List<File> jarFiles)
   {
      final String SEP = File.pathSeparator;
      StringBuilder sb = new StringBuilder();
      for (File f : jarFiles)
      {
         sb.append(f.getAbsolutePath()).append(SEP);
      }
      return sb.substring(0, sb.lastIndexOf(SEP));
   }

   /**
    * Runnable that consumes the output of the process. If nothing consumes the
    * output the Server will hang on some platforms
    * 
    * @author Stuart Douglas
    */
   private class ConsoleConsumer implements Runnable
   {
      @Override
      public void run()
      {
         final InputStream stream = process.getInputStream();
         final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
         final boolean writeOutput = AccessController.doPrivileged(new PrivilegedAction<Boolean>()
         {
            @Override
            public Boolean run()
            {
               // By default, redirect to stdout unless disabled by this
               // property
               String val = System.getProperty("org.infinispan.writeconsole");
               return val == null || !"false".equals(val);
            }
         });
         String line = null;
         try
         {
            while ((line = reader.readLine()) != null)
            {
               if (writeOutput)
               {
                  System.out.println(line);
               }
            }
         }
         catch (IOException e)
         {
         }
      }
   }
}
