# Java object Serializer and Deserializer 
This program serializes Java object instances to a JSON stream and sends it over a network socket to a peer which then performs a deserialization, creating the object instances back and filling in the proper parameters. Program was written for the CPSC 501 Advanced Programming Techiques at the University of Calgary for the Fall 2020 semester.

# Refactorings
First refactoring done using **Branch And Merge**
- Bad code smell: Long class
- How did the refactoring make the code better: it is now easier to follow along, all the different methods to create a specific Object are in a different class called ObjectBuilder.

Next, I have performed multiple smaller refactorings, such as name changes and Extract Method (creating printHeader())
- Bad code smell: Long method
- After the refactoring, the method and variable names are more clear, and easier to follow along.
