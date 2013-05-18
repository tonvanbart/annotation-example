### Java6 annotation preprocessing

This project demonstrates the use of the Java6 annotation preprocessor.
The use case here is usage of Slf4J internationalized logging, where we would prefer keeping the
message texts inside the logging enum, rather than in an external properties file. When the project
is built, the preprocessor will extract the texts and generate the properties before compiling.

The annotation-example-handler module contains the custom annotation and the handler; annotation-example-messages
contains a LogEvents enum using the annotation and depends on the handler project.
This project uses Maven, run `mvn clean install` in the top-level project. After building the generated properties
files are in target/classes of the example-msgs project in the same directory as the enum class file.

The project contains two preprocessors; one demonstrates the usage of a custom annotation for the texts, the other
shows reading the source javadoc in order to create the properties file. To determine which one will run change the
project configuration.
