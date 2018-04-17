# UConn Senior Design Project - Atomic Distributed Shared Memory for Networks

## Team Members
*Christian Carmellinin
*Dylan Ramsay
*Adomous Wright
*Kevin Konrad

## Introdction
*With the advance of networking technology,
communication has become a major systemic activity in the field of
distributed systems. In a network with a central server processing
read and write requests from several client machines, there exists
a single point of failure that can bring the system down, the central
server. Redundancy of data over multiple servers is the only way
to guarantee availability, however it presents challenges with
consistency. The client trying to read data, can consult all servers
on the network to identify the newest value, but this approach is
very inefficient and not fault-tolerant as it assumes that all servers
are available. With this in mind, we’ll explore a distributed shared
storage/memory model for a network with a single and/or multiple
writers. In this project we will develop an implementation of shared
readable and writeable objects in message-passing systems that
provides resiliency and consistency when the underlying
distributed platforms are subject to processor failures and
communication delays. Essentially, the implementation of shared
objects must ensure the atomicity of data, despite failures of some
nodes within the network


