
all: run

clean:
	rm -f out/Bluck.jar out/MyClass.jar

out/Bluck.jar: out/parcs.jar src/Bluck.java
	@javac -cp out/parcs.jar src/Bluck.java
	@jar cf out/Bluck.jar -C src Bluck.class
	@rm -f src/Bluck.class

out/MyClass.jar: out/parcs.jar src/MyClass.java src/Node.java
	@javac -cp out/parcs.jar src/MyClass.java
	@jar cf out/MyClass.jar -C src MyClass.class
	@rm -f src/MyClass.class

build: out/Bluck.jar out/MyClass.jar

run: out/Bluck.jar out/MyClass.jar
	@cd out && java -cp 'parcs.jar:Bluck.jar' Bluck