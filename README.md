# blockchain

## v0.1
Hashing function realisation

### How it works
1. Convert given input to bytes (by ASCII) 
2. Pad message to 512 bit chunks
3. Split each message to 32 bit chunks
4. Hash 16 chunks of message until you get 64 chunks 
5. Compress all message blocks while hashing them with constants and mixing bytes of each chunk
6. Convert final chunks to hex

### Test analysis
1. Tested my function with different input files to check whether function always returns fixed size hashes and is deterministic.    