# Moving Average Calculator

This is a solution for a Unbabel engineering exercise that can be found [here](https://github.com/Unbabel/backend-engineering-challenge).

## Arguments

The arguments for the program are defined in the pom.xml file inside &lt;arguments&gt;<br>
```xml
<arguments>
    <argument>C:\Users\ASUS\IdeaProjects\AveragePerMinuteCalculator\AveragePerMinuteCalculator\example_input_file.json</argument>
    <argument>8000</argument>
</arguments>
``` 
The 1st argument is the directory for the input file, the 2nd argument is the window size in minutes.

## How to run

Assuming maven is installed, run these commands:

```bash
mvn clean install
mvn exec:java