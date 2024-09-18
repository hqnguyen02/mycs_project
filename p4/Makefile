schedule: schedule.o catalog.o input.o
	gcc schedule.o catalog.o input.o -o schedule
schedule.o: schedule.c input.h catalog.h
	gcc -g -Wall -std=c99 -c -o schedule.o schedule.c
catalog.o: catalog.c catalog.h input.h
	gcc -g -Wall -std=c99 -c -o catalog.o catalog.c
input.o: input.c input.h
	gcc -g -Wall -std=c99 -c -o input.o input.c
	
clean:
	rm -f */output.txt
	rm -f */.__afs*
	rm -f *.o
	rm -f *.ko
	rm -f *.obj
	rm -f *.elf
	rm -f *.exe
	rm -f *.out
	rm -f *.app
	rm -f *.i*86
	rm -f *.x86_64
	rm -f *.hex
