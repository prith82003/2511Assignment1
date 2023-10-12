Week 2 (18 - 23 September):
This week I worked on the basis of the assignment and the basic structure of the entities.

Entities:
When designing the software, I decided that each type of entity will be its own class so that behaviour unique to each entity type can be handled in 
its class. All the constants for each entity type are in their classes so that this information can be passed to the super constructor to set the 
speed, storage space, etc.

createDevice():
When creating this method, I realised I would need to keep a collection of devices to keep track of all the different devices during runtime. I 
initially decided to use a List of devices, but I realised that I would need to be able to access devices by their ID, so I decided to use a HashMap 
instead. This way, I can access devices by their ID, and I can also iterate through all the devices in the HashMap.

createSatellite():
I decided to use a HashMap to store the satellites as well, for the same reason as above.

addFileToDevice():
When creating this function, I realised that each device and satellite would need to have a list of files that they contain. I decided to use a HashMap 
to store the files in each device and satellite, so that I can access the files by their ID, and I can also iterate through all the files in the 
HashMap. I decided to put this hashmap inside the entity class, since all entities will need to have a list of files. In order to actually add files to 
this map, I created an addFile method in the entity class, which takes in a file and adds it to the hashmap. I also created a getFile method in the 
entity class, which takes in a file ID and returns the file with that ID.

getInfo():
In order to complete this function, I had used all the properties that I had already created for the previous functions. In order to get the file 
responses I initially had each entity get each individual property of its file and add it to the file response object, however, I later realised that I 
could create a getInfo method in the file which returns a file response object that I would put into a hashmap inside the entity's getInfo method. This 
way, I can iterate through the hashmap and get all the file responses for each file in the entity.


Week 3 (25 - 30 September):
This week I completed functionality for task 1 and started considering how task 2 would work. I first decided to draft out the simulate function in 
the satellites 

I implemented most of the functionality of simulating the movement of the standard satellite, in order to get an early start on task 2, I decided to
figure out how I was going to determine if two entites are in range and they can transfer. I created a helper file in which I stored helper functions.
I have not considered how to handle relay connections just yet.

Week 4 (2 - 7 October):
In order to achieve different behaviours for each Satellite, I decided that each Satellite would have its own simulate function which takes care of all 
this. I didn't do the same for devices since in this task there are no moving devices and they dont need to handle file transferring (since satellites 
can take care of this). Each satellite now has its own function in which the position is updated. I created a simulate function in the base Satellite 
class which calls an updatePosition function and transfer files function. I decided that the simulate function will be private to the Satellite class 
and the updatePosition function will be visible to the subclasses which can override it if they need to.

simulate():
Recently, I realised that having a default updatePosition function in the base class was unnecessary and bad style, so I decided to remove it and 
instead convert it to an abstract function which is overrided in the subclasses. This way, I can have different updatePosition functions for each 
satellite type. I added the transfer files function, currently, I have a FileReader and Writer class which take care of reading and writing to/from
files. I use these objects in the Satellite class to transfer data between the different files. I have 2 different Lists of File Reader/Writers
and I iterate through this list to update each one. I check the number of bytes that need to transferred and read/write that many bytes.

Revision to Task 1:
Whilst writing communicableEntitiesInRange() I realised that having a list of all the entities in the system would be useful. I decided to create a 
List of the entities that exist in the system and add each entity to this list when it is created.

communicableEntitiesInRange():
When creating this function, I realised that it would be simple to just iterate through all the entities in the system and check if it can 
communicate with the entity that is passed in.

Week 5 (9 - 14 October):
This week I decided to start on sendFile(). This function required me to think about how the file would be sent from one device to another, how I could
keep track of all the different file transfers and the different behaviours associated with each file transfer. I had already created FileReader/Writer 
classes but I recently realised integrating them into one class would make it more readable and concise.
I commented out all the code in FileReader and FileWriter and copied the functions into FileIO and made them private. I then created a transfer
files function which does the reading and writing in one go. The standard and teleport behaviours are mostly similar, differing only when teleporting, 
so I imlpemented a base transferFiles function in Satellite.java. I then created an onTeleport function in satellite which is only triggered by a 
teleportingSatellite when it teleports. This onTeleport handles the functionality such as corrupting a file and instantly downloading a file.

Revision to Task 1:
I recently realised that having a list of satellites and devices was unnecessary, I modified the behaviour such that I only used the list of entities and I deleted the other two lists.

Revision to Task 2:
Previously I did not handle connections between entities efficently, I have now created a class called Connection which keeps track of the two entities
in the connection, it has functions which allow you to get the two entities and also check if the connection is active. This object is used in FileIO
class so it knows which two entities to transfer between. Doing my connections this way allows me to implement relay connections more efficiently.
Initially, I had thought that relay connections were determined at the start of the transfer once and if the connection was interrupted in any way, the 
connection would break and not attempt to find another relay in order to continue the transfer. 
However, after checking the EdStem forum, I have realised that not only multiple relays can be used in a transfer, but, if the connection is 
interrupted, a new/additional relay can be used to sustain the connection.
 
In order to implement this, I created a recursive function in BlackoutController which tries to bridge the gap between the source and destination 
entities by finding a relay connection. During each tick, FileIO checks if the connection is active, if it is broken, it tries to find a relay
satellite in order to continue the connection. 

I have also made some minor tweaks to some files and replaced some for loops with streams for greater readability.
