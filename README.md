# Securing JMX

All credit goes to Teijo Holzer for this neat hack.

## The Problem

JMX likes to bind to all interfaces. A single server box can run multiple JVM
applications, and JMX is enabled for these JVMs. Doing JMX authentication is
hard, and I have never seen it being used.

We do not know which exact application will run on a particular host machine,
so writing static firewall rules to lock down JMX is not possible.

Dynamic firewalling of JMX ports is possible (snoop on Java processes, and
their command line parameters which reveal the JMX port), but it is an ugly
hack.

It is possible to create your own 'RMIServerSocketFactory' but that would mean
changing hundreds of those micro-services written in Java.

## Fix

Teijo Holzer fixed this by patching the default JVM RMI socket factory that is
responsible for creating this server socket. It now supports the new
'com.sun.management.jmxremote.host', and 'com.sun.management.jmxremote.interface'
properties. This patched JVM RMI socket factory is compiled to "jmx_patch.jar"
by the Makefile.

It might be hard to get this patch accepted upstream, since it might change the
"public ServerSocket createServerSocket(int port) throws IOException" API
promise (when better error handling is introduced in the patch).

## Usage

Adding "-Xbootclasspath/p:jmx_patch.jar -Dcom.sun.management.jmxremote.host=127.0.0.1"
to JVM_OPTS environment variable will bind bind the JMX service only to address
127.0.0.1.


Running "make bad" gives us,

```
$ netstat -ntlp | grep java  # without the patch
tcp6       0      0 :::7091                 :::*   LISTEN  23296/java
tcp6       0      0 fe80::ce3d:82ff:f:12345 :::*   LISTEN  23296/java
tcp6       0      0 :::43848                :::*   LISTEN  23296/java
```

Running "make good" gives us,

```
$ netstat -ntlp | grep java
tcp6       0      0 127.0.0.1:7091          :::*   LISTEN  23631/java
tcp6       0      0 fe80::ce3d:82ff:f:12345 :::*   LISTEN  23631/java
tcp6       0      0 :::46588                :::*   LISTEN  23631/java
```

See "Makefile" for the details.

## Relevant JDK files

* jdk/src/java.rmi/share/classes/sun/rmi/transport/proxy/RMIDirectSocketFactory.java

* jdk/src/java.management/share/classes/sun/management/Agent.java

* jdk/src/java.management/share/classes/sun/management/jmxremote/ConnectorBootstrap.java

* jdk/src/jdk.attach/share/classes/com/sun/tools/attach/VirtualMachine.java

* jdk/src/java.base/unix/native/libnet/PlainSocketImpl.c

## Fun

Can we do the same using AspectJ? It would be super fun to see that happening.

## References

* http://mail.openjdk.java.net/pipermail/jmx-dev/2015-November/000873.html (upstream patch!)

* http://openjdk.java.net/guide/repositories.html

* http://hg.openjdk.java.net/build-infra/jdk8/raw-file/tip/README-builds.html

* https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html

* http://docs.oracle.com/javase/tutorial/networking/nifs/definition.html

* http://www.tutorialspoint.com/jdb/jdb_quick_guide.htm

* http://docs.oracle.com/javase/8/docs/technotes/tools/windows/jdb.html

* http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6425769

* http://realjenius.com/2012/11/21/java7-jmx-tunneling-freedom/
