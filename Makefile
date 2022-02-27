
all: run

clean:
	rm -f out/Main.jar out/Algorithm.jar

out/Main.jar: out/parcs.jar src/Main.java
	@javac -cp out/parcs.jar src/Main.java
	@jar cf out/Main.jar -C src Main.class
	@rm -f src/Main.class

out/Algorithm.jar: out/parcs.jar src/Algorithm.java
	@javac -cp out/parcs.jar src/Algorithm.java
	@jar cf out/Algorithm.jar -C src Algorithm.class
	@rm -f src/Algorithm.class

build: out/Main.jar out/Algorithm.jar

run: out/Main.jar out/Algorithm.jar
	@cd out && java -cp 'parcs.jar:Main.jar' Main
