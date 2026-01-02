Two implementations of a concurrent prime number counter written in C using UNIX system calls.

parallel_prime.c processes a large input file in parallel using multiple child processes created with fork.
parallel_prime_pipe.c extends this by sending each child process's local result to the parent process via pipe.

The input file (numbers.txt) contains whilespace-separated integers and is processed concurrently with boundary handling to avoid splitting numbers.

Used system calls: stat, fork, lseek, read, pipe, wait
