Week 1:
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



Week 3:
When I started this task I realised that all the satellites would have to be simulated, their position and file transfers. In order to achieve this
I decided that each Satellite would have its own simulate function which takes care of all this. I didn't do the same for devices since in this task 
there are no moving devices and they dont need to handle file transferring (since satellites can take care of this). Each satellite now has its own 
function in which the position is updated. I created a simulate function in the base Satellite class which calls an updatePosition function and 
transfer files function. I decided that the simulate function will be private to the Satellite class and the updatePosition function will be visible
to the subclasses which can override it if they need to.

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
communicate with the entity that is passed in. In order to maintain code readability, I decided to create a helper file which
contains a function that checks if two entities can communicate with each other. This way, I can just call this function in the function.



Week 4:
This week I decided to start on sendFile(). This function required me to think about how the file would be sent from one device to another, how I could
keep track of all the different file transfers and the different behaviours associated with each file transfer. I had already created FileReader/Writer 
classes but I recently realised integrating them into one class would make it more readable and concise.
I commented out all the code in FileReader and FileWriter and copied the functions into FileIO and made them private. I then created a transfer
files function which does the reading and writing in one go. The standard and teleport behaviours are mostly similar, differing only when teleporting, 
so I imlpemented a base transferFiles function in Satellite.java. I then created an onTeleport function in satellite which is only triggered by a 
teleportingSatellite when it teleports. This onTeleport handles the functionality such as corrupting a file and instantly downloading a file.

