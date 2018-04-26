[![Maven Central](https://maven-badges.herokuapp.com/maven-central/edu.brown.cs.burlap/burlap/badge.svg)](https://maven-badges.herokuapp.com/maven-central/edu.brown.cs.burlap/burlap) [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)]() ![java6](https://img.shields.io/badge/java-6-blue.svg) ![java7](https://img.shields.io/badge/java-7-blue.svg) ![java8](https://img.shields.io/badge/java-8-blue.svg)

Intelligent Grading with Inverse Reinforcement Learning 
======

This is a project on using Inverse Reinforcement Learning (IRL) for automated grading of physical (sensorimotor) skills. It also includes a snapshot of the [BURLAP](http://burlap.cs.brown.edu/) codebase, since I had to make a few changes in BURLAP to create my IRL framework.

## How to run
This project is built using Maven; I will outline steps to use it with [IntelliJ](https://www.jetbrains.com/idea/), a free Java IDE.

 1. Clone the project to a local directory (`git clone https://github.com/gautams3/IRL_IntelligentGrading.git` ) 
 3. Import the project as a Maven project in IntelliJ (Import Project -> go to root project folder -> double click pom.xml. You may have to wait a while for the dependencies to download and the indexing)
 4. Open file `src/main/java/Tutorial/IRLParkingLotExample.java`
 5. Run that class (`main()` function. You may have to add a Configuration that runs the IRLParkingLotExample class)

There are 4 modes to run in sequential order, explained thoroughly in the paper. The main() function in IRLParkingLotExample let's you choose these modes, using the `GridWorldRunOptions` enum
1. Explore and record: This lets you navigate the parking lot world, and record episodes
2. Playback: Playback recorded episodes
3. RunIRL: Run the IRL algorithm to learn the reward function based on the given expert demonstrations (transitions are considered deterministic)
4. TestUser: Test the user trials based on the learned reward function from step 3.

In order to help you skip to the mode you wish to run, I have added sample output files for expert demonstrations, user trials, and IRL reward function output.
