# rsocket-composingbackpressure-example
An example showing how backpressure composes between multiple microservices with [RSocket](http://rsocket.io).

## Building the Example
Run the following command to build the example:

    ./gradlew clean build
    
## Running the Example
Follow the steps below to run the example:

1. Run the following command to start the `number-service`:

        ./gradlew :number-service:run
        
    If the service has started successfully, you will see the following in the terminal:
    
        > Task :number-service:run
        [main] INFO example.number.service.NumberService - RSocket server started on port: 7002
        
2. In a new terminal, run the following command to start the `letter-service`:

        ./gradlew :letter-service:run
        
    If the service has started successfully, you will see the following in the terminal:
    
        > Task :letter-service:run
        [main] INFO example.letter.service.LetterService - RSocket server started on port: 7001
        
3. In a new terminal, run the following command to start streaming data with the `client`:

        ./gradlew :client:run
        
    If successful, you will see the client receives 10,000 letter and number pairs in the terminal:
    
        [reactor-tcp-nio-1] INFO example.client.Client - Received: f9996
        [reactor-tcp-nio-1] INFO example.client.Client - Received: R9997
        [reactor-tcp-nio-1] INFO example.client.Client - Received: t9998
        [reactor-tcp-nio-1] INFO example.client.Client - Received: O9999
        [reactor-tcp-nio-1] INFO example.client.Client - Received: G10000
        [reactor-tcp-nio-1] INFO example.client.Client - Done
        
    Notice that in the `letter-service` terminal that the client is requesting `8` combinations at a time:
    
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: t9993
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: n9994
        [reactor-tcp-nio-3] INFO example.letter.service.LetterService - Received Request For: 8
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: H9995
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: f9996
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: R9997
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: t9998
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: O9999
        [reactor-tcp-nio-1] INFO example.letter.service.LetterService - Sending: G10000
        
    Notice that in the `number-service` the client's demand of `8` combinations is propagated to the number service:
    
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 9993
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 9994
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Received Request For: 8
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 9995
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 9996
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 9997
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 9998
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 9999
        [reactor-tcp-nio-2] INFO example.number.service.NumberService - Sending: 10000
        
## Bugs and Feedback
For bugs, questions, and discussions please use the [Github Issues](https://github.com/gregwhitaker/rsocket-composingbackpressure-example/issues).

## License
MIT License

Copyright (c) 2020 Greg Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.