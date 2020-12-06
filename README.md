### Patch Tools
This library created to help serialize, deserialize, pass through application
layers and collect all changes on each layer in DTOs, Models, Entities etc.
### Subject intro
While implementing HTTP PATCH method for partially update we
should deserialize data from request body to DTO, proceed
deserialized data through application layers (Controller,
Service, Repository etc.)
Let's describe DTO:
```java
public class UserModel {
	private String login;
	private String firstName;
	private String lastName;
	private String birthDate;
	private String email;
	private String phoneNumber;
}

@ChangeLogger
public class UserDto extends UserModel {
}
```
If we need to change `birthDate` and `email` fields we should call
endpoint with body:
```json
{
  "birthDate": "1987-03-17",
  "email": "somebody@email.domain.name"
}
```
When this JSON will be deserialized we'll got DTO with two filled fields and
all other will had `null` values. So if we'll want to validate or update this
fields in entity on Repository level we should check all the fields on `null`
value and proceed it only if it isn't. Additionally we have not possibility to
clear some fields by `null` values because after it will be deserialized we
could not check if this value present in method body or it absent.
If we'll deserialize body to `Map<String, Object>` structure we could
check is the field present in data or not. But we should use unchecked casting
for proceeding each field value and it will excluded types checking on compile
stage.
Patch Tools library contains utils which should help to proceed this cases.
### Patch Tools Core
Patch Tools Core library contains the basic classes and helpers for creating
POJO wrappers which collect fields which are changed by calling setters. Also
it contains classes and helpers for creating converters based on Jackson's
ObjectMapper.
##### PatchTools intro
The main thing of this library is POJO wrapper which handles all setters calls
and adds changed field names to internal `Set<String>`. Also this wrapper
implements `ChangeLogger` interface which contains single method
`Map<String, Object> changelog();` This method returns `Map` with field names
as a Key and field values returned by field's getter.
##### Change Logger Producer
`ChangeLoggerProducer` class contains wrapper logic and service method
`produceEntity` which creates the wrapped POJO. Tipically usage of this producer
for `UserDto` class could be like:
```java
ChangeLoggerProducer<UserDto> producer = new ChangeLoggerProducer<>(UserDto.class);
UserDto user = producer.produceEntity();
user.setLogin("userlogin");
user.setPhoneNumber("+123(45)678-90-12");
Map<String, Object> changeLog = ((ChangeLogger) user).changelog();
/*
    changeLog in JSON notation will contains:
    {
        "login": "userlogin",
        "phoneNumber": "+123(45)678-90-12"
    }
*/
```
##### ObjectMapper with Annotation Introspector
For serializing/deserializing wrappers which implements ChangeLogger interface
could be used Jackson(Codehaus) ObjectMapper with
ChangeLoggerAnnotationIntrospector. With this Annotation Introspector ObjectMapper
checks while serialization if instance implements ChangeLogger interface then
`Map<String, Object>` returned by `changelog()` method will been serialized
instead POJO. For example:
```java
ObjectMapper mapper = new ObjectMapper.setAnnotationIntrospector(new ChangeLoggerAnnotationIntrospector());
ChangeLoggerProducer<UserDto> producer = new ChangeLoggerProducer<>(UserDto.class);
UserDto user = producer.produceEntity();
user.setLogin("userlogin");
user.setPhoneNumber("+123(45)678-90-12");
String result = mapper.writeValueAsString(user);
/*
    result should be equal
    "{\"login\": \"userlogin\",\"phoneNumber\": \"+123(45)678-90-12\"}"
*/
```
While deserialization this ObjectMapper will check if class which should be
instantiated has annotation `@ChangeLogger` then result will be instantiated
using ChangeLoggerProducer. For example:
```java
ObjectMapper mapper = new ObjectMapper.setAnnotationIntrospector(new ChangeLoggerAnnotationIntrospector());
String source = "{\"login\": \"userlogin\",\"phoneNumber\": \"+123(45)678-90-12\"}";
UserDto user = mapper.readValue(source, UserDto.class);
Map<String, Object> changeLog = ((ChangeLogger) user).changelog();
/*
    changeLog in JSON notation will contains:
    {
        "login": "userlogin",
        "phoneNumber": "+123(45)678-90-12"
    }
*/
```
##### Patch Mapper Producer
For more comfortable mapping ChangeLogger between different layers POJOs
(DTO, Model, Entity etc.) in Patch Tools library implemented PatchMapperProducer
class which could instantiate wrappers for abstract classes or interfaces with
abstract methods handling, interpreting its input parameters and return values
and using corresponding ObjectMapper for mapping POJOs.<br>
Method Interceptor of wrapped mapper could distinguish two cases:
* abstract method returns non void result(convert pattern method);
* abstract method returns void(update pattern method);

In the first case interceptor will create result instance and then map all
method arguments to result instance one by one.
In the first case interceptor will map to first method argument all the rest
method arguments one by one.
Example for both of this cases we could see at
[PatchMapperProducerTest.java](patch-tools-core/src/test/java/patch/tools/mapper/PatchMapperProducerTest.java).