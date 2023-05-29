package com.example.roommateapplication.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key =MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCdgnKojJwBR4ar\\nJSz01tXa7OeDniHaZNLM2mpiKdN0b3BYZBbBqRdl6T3B0skLfNT4yEahyRr3sC6d\\nrQeh8HDkI0FdXiS9oLH9PcgxoESniWRUxqt7voS4AMuIMUpcNb1kAKBJNxQeB2OH\\nRjrLmLPG+gLRaaDLZwu6+Af0Xwm1nxjHB9j/FJtzUpekAEhOsjsgq7TNEiGzBw0e\\nvnK/LvbnZLnyNflrJaABHtdggfMq9ed/9/YuR+ycOLV163WlNVAmA+86FLQ1fXAX\\nKvrMXJXrl63T4MkU0vuHXe086I4PC+Fw/XG7/SJxBzi56+UIBwGmC7u+NhAPZDJ4\\n0JZqyrNVAgMBAAECggEAGjqC/9y8TL09qGm+IP5WaciZ69GtFvpDnR/A59xJrUGy\\nXlG6dWDEN/D9WvWQC3Tr3QqyslW0AytoM9MAqp+ImQDi7VqUOSyVfxNm7xaeYByx\\no6lanHZXEPH28pSsqoq7YTf2eQKuO6rk0L2p5EXnJ/rxdbBGeaCM9ENBxdh+nCrh\\nRRHR8L8BT0Ha5cmYv/FRd/iJtOMg1aH95AcORjq0Z/FbWMfNZDuVd7WnkscV31om\\naqRVdZTIYKTqPO/cz65hYYRFl+ss6VnHsOeZip4ul0gq22hAs9RfY+aaA3M7vs12\\nrpild2H1LwrH9wionL9kSldx3lwsnjGPRvHu4HkRyQKBgQDZez9c8k5WdU+OOh+1\\nPGWki946CA+xdryNv1amGq8U+SLX1m99ZocEfTD9DPRxoHh3DJmzewBM9h8WZtPI\\nN5pou624DC0J/3dTlBwELOx9emACg/0+F0vbarG+cfzJFTXyYyefYUIQ/rjsq8jo\\nLTP7zz9YrivZg5TTGxrkrCn9HQKBgQC5aAkuRLXxDemO3Hlmk74/suSUzbRZK6Jd\\nH7sRNWv+Op4ovIoSJwtaUEc4WjYF1Q/DId1FH9Mgr4Rx14BOB8nIY5tQRMyawfTY\\nc23E3qa0ve4MuvECzfouL6Nq317NMO7XJLLlxVeNlbRSB5DLtop9mlShXvZfzLUh\\nP/jeo5mRmQKBgGPNbl9+BrsgbJjNTZ3DELX5RcsFxgXfglneM6pYVn5eckHwcwtu\\nEInb1a5a0paOWJnxW1uFF7c5vzxthcS814w2jFsIo2aSKLVfI5g68XAHx6V/4qgq\\n0rHnfZfzAVlZ9X69SAKZBFN+QWjC0JjTnaZyDhTpINBZ1OMn7iiAZWh1AoGABTD3\\niNcdrQkxr/FWaNvcKNZudggoc5K4MhlQWJQBWRX1FbjpuMZlF/tFczt7ZcBod5M8\\nKdJRTAn+EF3zVrv7F2RKlf3HKNIjc+gPe8KsMkVbI/ocA3MW/TISRkzIz7/+AAIk\\nDDDxWaKGcQGLOVj8GbY3hmktRtNpguvSoRE9nIkCgYAVkfY6guh0l8zl9dlDjgJS\\nooJIRVxZMJp150QExCA8j3oNXALqtqLWCupjFHtmu6DmOIysGU9xhGYq7VwO5b2w\\nXP5eV1eRNtJ2kjEoGxNcW8wb5GsY+kvlBBDmHPs32mmJiZNFC3JYHh2JVKK+LZ7u\\nD6tcA5vE6NzcFIZrUn/Q4w==\\"

    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body) {
        return null;
    }
}
