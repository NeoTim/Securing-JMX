bad:
	jar cvf jmx_patch.jar sun/
	javac Main.java  # dummy program
	java -Dcom.sun.management.jmxremote.port=7091 -Dcom.sun.management.jmxremote.rmi.port=7091 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false Main
	# java -Dcom.sun.management.jmxremote Main  # only with local attach

good:
	jar cvf jmx_patch.jar sun/
	javac Main.java  # dummy program
	java -Xbootclasspath/p:jmx_patch.jar -Dcom.sun.management.jmxremote.host=127.0.0.1 -Dcom.sun.management.jmxremote.port=7091 -Dcom.sun.management.jmxremote.rmi.port=7091 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false Main

nicer:
	jar cvf jmx_patch.jar sun/
	javac Main.java  # dummy program
	java -Xbootclasspath/p:jmx_patch.jar -Dcom.sun.management.jmxremote.interface=lo -Dcom.sun.management.jmxremote.port=7091 -Dcom.sun.management.jmxremote.rmi.port=7091 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false Main
