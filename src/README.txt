CS455 Intro to Distributed Systems

David Edwards
david.edwards@colostate.edu



cs455.scaling.client
--------------------------------------------------------------------------------
Client
 Main Client class.  Launches a ClientSender thread, responsible for sending packets to the Server
 and reconciling the returned Hash Code.  Launches a ClientStatistics timer thread to display
 statistics regarding messages sent and received.  Maintains a count of messages sent and received.

ClientSender
 Class responsible for establishing a connection to a Server, then sending and receiving data until shut down.


ClientStatistics
 Class which monitors and displays statistics about the running Client

SentHashList
 Maintains the list of Hashes sent to the Server.

cs455.scaling.server
--------------------------------------------------------------------------------
Server
 Main Server class.  Takes in parameters and listens for incoming connections from Clients.  Launches a
 ThreadPool to maintain threads, and a Statistics thread to monitor connections and throughput to the server.

ServerStatistics
 Class which monitors and displays statistics about the running Server

cs455.scaling.threadpool
--------------------------------------------------------------------------------
ThreadPool
 Responsible for instantiating Threads and monitoring their state.

WorkerThread
 Class representing a Thread in the ThreadPool, responsible for communication with the Client,
 as well as adding and removing from the WorkQueue

WorkQueue
 Thread-safe Queue, allowing the Server to add items, and the Threadpool to remove them.

cs455.scaling.util
--------------------------------------------------------------------------------
NotImplementedException
 Placeholder Exception used to mark not-yet-implemented code.

ServerHashCode
 Utility Class to process Hash Codes.

cs455.scaling.work
--------------------------------------------------------------------------------
WorkItem
 Interface for the WorkItems which are added to, and consumed from, the WorkQueue

WorkType
 The types of Work in the WorkQueue

ReadWorkItem
 ReadWorkItem represents a connected Client, represented by Key, ready to be read from.

HashWorkItem
 HashWorkItem represents block of data, ready to be read from, before being sent back to the
 Client represented by Key

WriteWorkItem
 WriteWorkItem represents a completed Hash Value to be sent back to the Client represented by Key
