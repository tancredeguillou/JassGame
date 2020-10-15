# oop_project_jass
Jass Card Game in Object Oriented Programming code

Object Oriented Programming Practice Course, Professor Michel Schinz, 2018

Objective : Be able to play a Swiss card game called Jass. That is implementing the rules and the behavior of the cards using O-O Programming. Adding the possibility to play online. Adding a good looking, comprehensive visual interface. Testing and debugging the additional code every week in order to maintain maximum robustness of the programm.

TO HAVE A LOOK AT THIS PROJECT IN DETAIL, PLEASE SEE THE FULL DESCRIPTION ON THE PDF README FILE. THIS IS ONLY A BRIEF DESCRIPTION AND INDICATION TO RUN THE PROGRAMM.

To launch the program, run the LocalMain class as follow :

The program accepts 4 or 5 arguments. The first 4 specify the players, and the last, optional, specifies the seed to be used to generate the seeds of the different random generators of the program.
Each player is specified by means of a string of characters composed of one to three components, separated from each other by a two-point (:).
The first component consists of only one letter, which can be :
- either h for a human player (local)
- either s for a simulated player (local) - either r for a remote player.
The second component, optional, is the player's name. By default, the following names are assigned to players, in order : Aline, Bastien, Colette and David.
The third component, also optional, depends on the type of player :
for a simulated player, it gives the number of iterations of the MCTS algorithm (10 000 by default), it must be greater than or equal to 10.
for a remote player, it gives the name or IP address of the host on which the player's server is running (default localhost).
For example, the following arguments : s h:Marie r:Céline:128.178.243.14 s::20000 specify a game in which the following players participate :
- a simulated player named Aline, with 10,000 iterations,
- a human player named Marie,
- a remote player named Celine whose server is running on the compute whose IP address is 128.178.243.14,
- a simulated player named David, with 20,000 iterations.

If one of the arguments has been entered incorrectly, the console will output this same message, telling where the problem is situated.
To launch the program as a remote player, one need to give its IP address to the local player, and simply launch the RemotePlayer class without any arguments needed. After that, the game will start as soon as the local player launches its program.