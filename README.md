# Cache Memory Simulator (Direct-Mapped, Write-Back)
This project is a software simulation of a cache memory subsystem built in Java.
It models how a real cache behaves when interacting with main memory, including *cache hits, misses, and write-back behavior*.


The simulator follows the specifications of a direct-mapped cache with write-back policy.

## Overview
This program simulates how a cpu interacts with memory through a cache:
* Main memory is represented as a 2KB array (2048 bytes)
* Cache consists of 16 slots
* Each cache block holds 16 bytes
* The system supports:
  * Read byte
  * Write byte
  * Display cache

Each operation mimics real hardware behavior, including:
* Tag comparison
* Indexing
* Block loading
* Dirty bit handling
* Write-back to memory

## Cache Design
### Confirguration
* Cache type: Direct-mapped
* Write policy: Write-back
* Block zize: 16 bytes
* Number of slots: 16
* Memory Size: 2048 bytes

### Address Breakdown
Each memory address is divided into:
* Tag
* Index (slot)
* Offset
These are extracted using bitwise operations

## Data Strcutures
Each cache slot contains:
* valid bit (whether slot is in use)
* diry bit (whether block was modified)
* tag (identifies the memory block)

## Supported Operations
* Read (R)
  * Checks if data is in cache
  * If yes -> hit
  * If no -> miss -> load block from memory
  * Outputs: Address, value, hit/miss status
 
* Write (W)
  * Checks if block loaded in cache
  * If block present -> hit
  * If block not present -> miss -> load block from memory
  * Writes data in
  * Marks block as dirty
  * If replaced later -> writes back to memory
 
* Display (D)
  * Prints full cache contents including valid and dirty bit, slot number, tag, and data block
