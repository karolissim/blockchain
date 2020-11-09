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
1. Tested my function with different input files to check whether function always returns fixed size hashes and is deterministic
![Different files testing](https://github.com/knuckl35/blockchain/blob/master/testImages/differentFiles.png)
2. Hashed given file (konstitucija.txt) hashing function worked for almost 3 seconds. My function works pretty slow it might be because i used strings mostly
![Konstitucija hashing](https://github.com/knuckl35/blockchain/blob/master/testImages/konstitucija.png)
3. Tested konstitucija.txt file with different hashing algorithms by using their libraries in java. Test results:
![Benchmark](https://github.com/knuckl35/blockchain/blob/master/testImages/benchmark.png)
4. Lastly generated different length pairs and tested if util function provided different outputs
![Benchmark](https://github.com/knuckl35/blockchain/blob/master/testImages/repetition.png)

### Results
While writing my function I deeply relied on SHA-256 algorithm. My function meets all given requirements, however it is slow compared to other hashing algorithms which might have been caused by lots of Strings conversions instead of using bytes. 
