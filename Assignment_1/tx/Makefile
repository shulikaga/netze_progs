CFLAGS=-Wall

PROGRAMS= TX
OBJECTS= Transmitter.o, crc32.o
ALLOBJECTS=$(OBJECTS) $(patsubst %,%.o,$(PROGRAMS))

default: $(PROGRAMS)

%.d: %.c
	$(SHELL) -ec '$(CC) -MM $(CFLAGS) $< | \
	perl -pe '"'"'s|($*\.o)[ :]*|\1 $@: |g'"'"' > $@'

TX: $(OBJECTS)

clean:
	-rm -f $(ALLOBJECTS) $(ALLOBJECTS:%.o=%.d) $(PROGRAMS)

include $(ALLOBJECTS:%.o=%.d)
