Havesomerest is a (REST) API test automation framework, currently in a proof of concept state. The tests are written in plain JSON or XML format and placed in the right place in the folder structure.

# The concept

Take a look at the (bit modified) example from the Wikipedia article about the [URL](https://en.wikipedia.org/wiki/Uniform_Resource_Locator). 

scheme:[//[user:password@]host[:port]]**[/]path/to/a/resource**[?query][#fragment]

The idea (which is not something new as some mocking frameworks already introduced this) is that the path and the API of the application can be represented as a folder structure, creating the same tree structure as the API under test. The endpoints are defined by JSON or XML test files placed in the appropriate folder. Currently the test framework looks for tests in a folder structure: **/src/test/rest**

A (bit modified) example from the Blogger API:

```
GET     /src/test/rest/api/blogs/[blogId]
GET     /src/test/rest/api/blogs/[blogId]/posts
POST    /src/test/rest/api/blogs/[blogId]/posts
GET     /src/test/rest/api/blogs/[blogId]/posts/[postId]
PATCH   /src/test/rest/api/blogs/[blogId]/posts/[postId]
PUT     /src/test/rest/api/blogs/[blogId]/posts/[postId]
DELETE  /src/test/rest/api/blogs/[blogId]/posts/[postId]
...
```

Which can be represented as:
```
Project_Directory
├── configure
├── configure.in
├── src
│   ├── test
│   │   ├── rest
│   │   |   ├── api
│   │   |   |   ├── blogs
│   │   |   |   |   ├── _blogId_
│   │   |   |   |   |   ├── get200_432_getBlogs.json
│   │   |   |   |   |   ├── posts
│   │   |   |   |   |   |   ├── get200_432_getPosts.json
│   │   |   |   |   |   |   ├── post200_432_postPosts.json
│   │   |   |   |   |   |   ├── _postId_
│   │   |   |   |   |   |   |   ├── get200_432_11_getPosts.json
│   │   |   |   |   |   |   |   ├── patch200_432_11_patchPosts.json
│   │   |   |   |   |   |   |   ├── put200_432_11_putPosts.json
│   │   |   |   |   |   |   |   ├── delete200_432_11_deletePosts.json
...
```

As you can see, Path variables are represented as folders prefixed and suffixed with an underscore, and the actual value is added to the filename.

The filename **get200_432_11_getPosts.json** is built up by several parts. The **get** is the request method of the test, the **200** is the expected status code, the **_432_** is the value of the path variable *_blogId_*, the **11_** part is the value for the path variable *_postId_*, and the **getBlogs** part is the name of the test case.

# The structure of the test file

Test scenarios are written in the markup language which the API could communicate in. Currently JSON and XML is supported. The usual Blogger API example:

```
{
  "request": {
    "headers": {},
    "parameters": {
      "endDate": "2016-02-15",
      "fetchImages": "true",
      "maxResults": "50",
      "fetchBodies": "true",
      "startDate": "2016-02-14",
      "labels": "foo,bar",
      "status": "active"
    }
  },

  "response": {
    "headers": {},
    "body": {
      "items": [...]
    }
  },

  "description": "Retrieves a list of posts, possibly filtered."
}
```

And the pure structure.
```
{
  "request": {
    "headers": {},
    "parameters": {}
  },

  "response": {
    "headers": {},
    "body": {}
  },

  "description": ""
}
```

Currently it contains 3 sections. Request, Response and Description. 

The Request object contains 2 other objects, Headers and Parameters. The headers are the request headers to be added to the request and parameters are working similarly.

The Response object contains 2 other objects, Headers and Body.

The Desription is a string.

# Building

The project can be build with maven and requires Java 1.8.

```
./mvn clean package
```

# Running the tests

```
cp target/havesomerest-1.0-SNAPSHOT.jar . && java -jar havesomerest-1.0-SNAPSHOT.jar
```

# Planned features

There are couple of ideas on making the framework easily usable with legacy and new projects as well, including JUnit integration, SetUp and TearDown mechanism, Database pre filling, console and websocket based (G)UI, etc.
