## Spring Boot and Redis

#### Enable Redis repositories and configure connection

```
@Configuration
@EnableRedisRepositories
public class RedisConfiguration extends CachingConfigurerSupport {

    @Value(value = "${spring.redis.host}")
    private String redisHostname;

    @Value(value = "${spring.redis.port}")
    private int redisPort;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHostname, redisPort);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Review> reviewRedisTemplate() {
        RedisTemplate<String, Review> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
}
```

Redis host, port and timeout parameters are defined in `application.properties` file.

```
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.timeout=6000
```

#### Service layer

Each review object will be stored for 60 seconds only in Redis database. After which the 
object will be deleted.

```
@Service
@Service
@CacheConfig(cacheNames = "review")
public class ReviewService {

    @Autowired
    private RedisTemplate<String, Review> reviewRedisTemplate;

    private static final String REDIS_PREFIX_SESSIONS = "review";
    private static final String REDIS_KEYS_SEPARATOR = ":";

    @CachePut(key = "#review.id")
    public void save(final Review review) {
        review.setId(UUID.randomUUID().toString());
        review.setTime(new Timestamp(System.currentTimeMillis()));
        reviewRedisTemplate.opsForValue().set(getRedisKey(review.getId()), review);
        reviewRedisTemplate.expire(getRedisKey(review.getId()), 60, TimeUnit.SECONDS);
    }

    @Cacheable
    public List<Review> findAll() {
        return reviewRedisTemplate.opsForValue().multiGet(reviewRedisTemplate.keys(getRedisKey("*")));
    }

    private String getRedisKey(final String key) {
        return REDIS_PREFIX_SESSIONS + REDIS_KEYS_SEPARATOR + key;
    }
}
```

#### Create REST Controllers

```
@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PutMapping(value = "/reviews")
    public ResponseEntity<Review> add(@RequestBody Review review) {
        reviewService.save(review);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @GetMapping(value = "/reviews")
    public ResponseEntity<List<Review>> getAll() {
        List<Review> reviewsList = reviewService.findAll();
        return new ResponseEntity<>(reviewsList, HttpStatus.OK);
    }
}
```

#### Create domain entities

```
@RedisHash(value = "author")
public class Author {

    @Id
    private String id;

    @Indexed private String name;

    // Constructors / Getters / Setters
}
```

| Id    | Author            |                  
| ----- | -----------------:|       
| 1     | Stephen King      |       
| 2     | Richard Dawkins   |

```
@RedisHash(value = "book")
public class Book {

    @Id
    private String id;

    @Indexed private String title;
    @Indexed private String authorId;
    @Indexed private Date publishDate;
    @Indexed private String type;
    @Indexed private int numOfPages;
    @Indexed private String publisher;
    @Indexed private String language;
    @Indexed private String isbn;

    // Constructors / Getters / Setters
}
```

| Id    | Title             | Author            | Date              | Type          | Number of Pages   | Publisher                 | Language      | ISBN              |                   
| ----- | -----------------:| -----------------:| -----------------:| -------------:| -----------------:| -------------------------:| -------------:| -----------------:|         
| 1     | The Shining       | Stephen King      | July 1, 1980      | paperback     | 659               | Hodder & Stoughton        | English       | 9780450040184     |        
| 2     | The Selfish Gene  | Richard Dawkins   | May 25, 2006      | paperback     | 360               | Oxford University Press   | English       | 9780199291151     |

```
@RedisHash(value = "review")
public class Review {

    @Id
    private String id;

    @Indexed private String userId;
    @Indexed private String bookId;
    @Indexed private String text;
    @Indexed private int rating;
}
```