Neural turing machines evolved through NEAT
===========================================

We construct Neural Turing Machines (NTMs) using the neuroevolutionary approach *NEAT*, in order to evaluate the feasibility of evolutionary methods in combination with NTMs.
NEAT has the advantage that it is an unsupervised learning algorithm, and therefore does not need training data which is not available in some domains.
When using evolution rather than backpropagation to generate NTMs for the Copytask, the Single T-Maze and the Double T-Maze learning tasks, the differentiable NTM proposed by Graves *et al.* performs significantly worse than our newly proposed simplified NTM.
This simpler architecture is possible, because evolved NTMs need not adhere to the differentiability restrictions of the backpropagation training method. 
Our proposed NTM architecture has far less parameters and simpler calculations, which mitigates the concerns raised for the current NTMs scalability, both in terms of network complexity as well as computation time. 
Our evolutionary NTMs succeed in solving the Continuous Double T-Maze, which to the best of our knowledge have not been solved before.

[Preliminary results have been published as part of the *Reasoning, Attention and Memory workshop* (RAM), at the NIPS 2015 conference.](http://sebastianrisi.com/wp-content/uploads/greve_ram15.pdf)

How to use
-------------
4 bat files exist in the root of the project:

* **RUN Evaluate NEAT T-Maze.bat** is used to evaluate the Double T-Maze champion. The settings in the bat file will automatically use tmaze.properties and the champion chromosome (id 2331278). Notice that this will fail if you run *RUN Evolve NEAT T-Maze.bat*, as this operation overwrites the chromosome files.

* **RUN Evolve HyperNEAT T-Maze.bat** is used to start HyperNEAT evolution of a Double T-Maze agent.

* **RUN Evolve NEAT T-Maze.bat** is used to start NEAT evolution of a Double T-Maze agent.

* **RUN Replay NEAT T-Maze.bat** is used to replay the Double T-Maze champion. The settings in the bat file will automatically use tmaze.properties and the champion chromosome (id 2331278). Notice that this will fail if you run *RUN Evolve NEAT T-Maze.bat*, as this operation overwrites the chromosome files.
The replay consists of two windows - one showing the memory contents and another showing the location of the agent in the maze. **Press space to advance one time-step.**






Introduction
-------------
Artificial neural networks (ANNs) are a machine learning technique used to estimate a function possibly of multiple inputs and multiple outputs. They are represented by a network of interconnected 'neurons' imitating the way biological neural networks works, with distinct sets of input neurons and output neurons and a number of 'hidden' neurons in between. The connection from one neuron to another is called a synapse and has a strength determining how much it contributes to the activation of the latter.
There are multiple ways to construct and adjust ANNs; hand crafting, back propagation, evolutionary algorithms and multiple others.

The Turing Machine (TM) is a simple (theoretical) machine which has access to an infinite memory tape where it can write to, erase, read from and move the cursor. Using the current state and the data in the current location on the tape the machine will have instructions for further manipulations [0].

Combined with the ANN the memory tape from the TM could be a simple way of storing information for future behavior outside of the network and thus simplifying the required neural structure making it easier to develop and interpret.


State of the art
----------------------
In 2014, Graves et al. at Google DeepMind published an article describing their implementation of 'Neural Turing Machines' [1]. They coupled an ANN with an external memory tape and introduced multiple addressing options for the network to use when reading and writing. Their approach rely on training the ANN with backpropagation, requiring that all functions and operations be differentiable.

Several techniques already exists for allowing ANNs to adapt to direct changes in the environment after the learning phase. The first is called Recurrent Neural Networks [2] which have a feedback loop from neurons to upstream neurons which persists over activations. This allows them to remember information while it is actively preserved. Another technique is to simply allow the network to adjust weights after the initial training by plasticity which can be facilitated through neuromodulation [3]. 


Evolved neural networks with turing machines
---------------------------------------------

Based on the current research we will in this project attempt to evolve Neural Turing Machines using the NEAT (NeuroEvolution of Augmenting Topologies) framework for solving increasingly difficult tasks that requires the agent to have some form of memory in order to perform well.
The choice of evolving the network rather than using backpropagation will likely create a more transparent connection between the TM memory tape and the ANN.

This difference to existing means of online memory in ANNs might have great advantages in interpretability, evolution time and performance.

Evaluation
------------------
The most common way of evaluating an agent's ability to adapt to online changes in the environment is the T-Maze where the goal moves during the evaluation.

Similarly the Double T-Maze offers a greater challenge to the adaptability of the agent. 

As a final challenge we propose a simple world of competing species where the agent will have to remember what species are nutritious and which are poisonous.


Success criteria
-----------------
Create an implementation of a Neural Turing Machine in the NEAT framework and evaluate its performance on increasingly difficult tasks.
If possible we want to show how the evolved AI manipulates and exploits the memory strip to solve the tasks in a way that would be more difficult to do without memory. 
If time allows it we would also like to explore multiple different neural turing machines in a competitive coevolution setting.


Method
---------------
We will read literature relevant to the field, implement a full-functional implementation of the Neural Turing Machine suitable for evolution with NEAT and use this system for evolving and evaluating ANNs utilizing the memory tape in several increasingly difficult domains.

What will be handed in
----------------------
Implemented software source code and report.


-------------------------------------------------
[0] Turing, Alan Mathison. "On computable numbers, with an application to the Entscheidungsproblem." J. of Math 58.345-363 (1936): 5.

[1] Graves, Alex, Greg Wayne, and Ivo Danihelka. "Neural Turing Machines." arXiv preprint arXiv:1410.5401 (2014).

[2] Ziemke, Tom. "Remembering how to behave: Recurrent neural networks for adaptive robot behavior." (1999).

[3] Soltoggio, Andrea, et al. "Evolutionary advantages of neuromodulated plasticity in dynamic, reward-based scenarios." Proceedings of the 11th International Conference on Artificial Life (Alife XI). No. LIS-CONF-2008-012. MIT Press, 2008.
