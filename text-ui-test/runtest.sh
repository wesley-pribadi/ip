#!/usr/bin/env bash

# Get the directory where the script is located
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

# Change to that directory so relative paths work correctly
cd "$SCRIPT_DIR"


# create bin directory if it doesn't exist
if [ ! -d "../out/production/ip" ]
then
    # mkdir ../bin
    echo "please mkdir ../out/production/ip"
    exit 1
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# compile the code into the bin folder, terminates if error occurred
if ! javac -cp ../src/main/java -Xlint:none -d ../out/production/ip ../src/main/java/*.java
then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ../out/production/ip Dyuque < input.txt > ACTUAL.TXT

# convert to UNIX format
#cp EXPECTED.TXT EXPECTED-UNIX.TXT
#dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT

# compare the output to the expected output
#diff ACTUAL.TXT EXPECTED-UNIX.TXT
diff ACTUAL.TXT EXPECTED.TXT
if [ $? -eq 0 ]
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi
