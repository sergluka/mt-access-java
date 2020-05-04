Reactive Java client for the [MT Access](https://sergeylukashevich.github.io/mt-access-doc) API.

## MT Access

MT Access is a service that allows easy access to [MT4 server](https://www.metatrader4.com/en/brokers/api) 
the most known Forex trading plaform. If you are interested, please [contact](mailto:sergey.lukashevich@finplant.com?subject=MT%20Access)

## Details

Client is written on Java 8 based on [Project Reactor](https://projectreactor.io/)

# Examples

## Blocking connect / disconnect

```java
import com.finplant.mt_access.MtAccessClient;

var params = MtAccessClient.ConnectionParameters.builder()
        .uri(URI.create("wss://localhost:8080"))
        .server("127.0.0.1")
        .login(1)
        .password("password")
        .build();

MtAccessClient client = 
        MtAccessClient.createSecure(params,
                                    BaseSpecification.classLoader.getResourceAsStream("keystore.jks"),
                                    "keystore password", true);

MonoProcessor blockingProcessor = MonoProcessor.create();
connectionDisposable = client.connection().subscribe({ blockingProcessor.onNext(true) })
blockingProcessor.timeout(Duration.ofSeconds(30)).block();

System.out.format("Connected\n");

connectionDisposable.dispose();

System.out.format("Disconnected\n");
```

## Reconnect on connection lost

```java
client.connection()
    .doOnError(e -> System.err.format("Connection lost: %s", e.getMessage()))
    .retryBackoff(10, Duration.ofSeconds(1))
    .subscribe(none -> System.out.println("Connected"),
               Throwable::printStackTrace,
               () -> System.out.println("Connection closed"));
```

## Create new user
```java
Mt4UserRecord user1 = Mt4UserRecord.builder()
                                   .login(100)
                                   .enable(true)
                                   .group("demoforex")
                                   .enableChangePassword(true)
                                   .readOnly(false)
                                   .enableOtp(false)
                                   .passwordPhone("PhonePass")
                                   .name("Johans Smits")
                                   .country("Latvia")
                                   .city("Riga")
                                   .state("n/a")
                                   .zipcode("LV-1063")
                                   .address("Maskavas 322 - 501")
                                   .leadSource("Source")
                                   .phone("+37100112233")
                                   .email("sergey.lukashevich@finplant.com")
                                   .comment("User comment")
                                   .id("id1")
                                   .status("STATUS")
                                   .leverage(1000)
                                   .agentAccount(1)
                                   .taxes(BigDecimal.valueOf(30.33))
                                   .sendReports(false)
                                   .mqid(123456)
                                   .userColor(0xFF00FFL)
                                   .apiData(new byte[0])
                                   .password("Pass1")
                                   .passwordInvestor("Pass2")
                                   .build();

var newLogin = client.users().add(user1).timeout(Duration.ofSeconds(3)).block();
System.out.format("New user login is %d\n", newLogin);

```

## Get user by login

```java
var user = client.users().get(100).timeout(Duration.ofSeconds(30)).block();
System.out.format("User: %s\n", user);

```

## Tick feed subscription

```java
client.symbols().show("EURUSD").block();

client.market().listen().take(10).subscribe(
        tick -> { System.out.format("Tick: %s\n", tick); },
        Throwable::printStackTrace,
        () -> client.symbols().hide("EURUSD").subscribe());
```

## Trading request confirmation with a new price

```java
client.dealing().listen().subscribe(request -> {
    client.dealing().confirm(request.getId(),
                           BigDecimal.valueOf(2.11), BigDecimal.valueOf(2.12),
                           DealingProcedures.ConfirmMode.NORMAL)
        .subscribe(none -> System.out.println("Request is confirmed"),
                   e -> System.err.println("Cannot confirm request: " + e.getMessage()));
});

```

See more samples in [tests](src/test-integration/groovy/com/finplant/mt_access_client/) 


