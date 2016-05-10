# Error handling

*This document is a draft and should be integrated into the javadocs later.*

## Services
All services share the same base class for all exception: `ServiceException`.
This exception is a checked exception and MUST be handled.
The services MUST NOT throw any other exception.

### Service Locator
The service locator is the entry-point to find a service implementation. Every service should be registered here and can be queried by calling the getService(Class) method.
If a requested service isn't available, a `ServiceNotFoundException` is thrown.
Other checked exceptions should not be thrown by the Service Locator.

### Data Handlers
All data handlers (e.g. `IFileDataHandler`, `IModuleDataHandler`, ...)  have two different Excpetions:

| Exception                 | Occurrence                         |
| ------------------------- | ---------------------------------- |
| `ObjectNotFoundException` | An actions in which a single object is queried or updated didn't return results or could not be applied because the object could not be found. |
| `DataHandlerException`    | If an unexpected errors e.g. an `SQLException` occur. |

### Storage Handlers
All storage handlers (e.g. `IFileStorageHandler`, `IAvatarFileStorageHandler`, ...) have only one exception which occures:

| Exception                 | Occurrence                         |
| ------------------------- | ---------------------------------- |
| `FileStorageException` | If an error occures or something else went wrong, while storing or getting something from the file system e.g. `IOException`, `ServiceNotFoundException`, `NoSuchAlgorithmException`. |

### Mailing Service
The `IMailingService` class throws it's own exception `MailingServiceException` if an unexpected error occurs
e.g. there is no Internet access or the message was blocked by a firewall.

## Beans

### Resource Beans
Resource beans are used to create, update and list different resources.
The resource beans control MUST check the arguments (request parameter) and the users access to the resource.
Therefore it needs its own exception: `ResourceBeanException`.
To allow a distinction between different types of error (bad parameter, no access, ...)

| Exception                 | Base                    | Occurrence                         |
| ------------------------- | ----------------------- | ---------------------------------- |
| `AccessDeniedException`   | `ResourceBeanException` | The user doesn't have enough rights. The exception MUST hold the allowed level and the required level of access. |
| `BadRequestException`     | `ResourceBeanException` | The request didn't hold all required parameters. |
| `ObjectNotFoundException` | `ServiceException`        | The object could not be found. |

### Other beans

There are some other beans. These beans should not throw any exceptions at all and try to catch all `RuntimeException`s because they are directly embedded into the `jsp`-files.


## Servlet
Servlets (Subclasses of `HttpServlet` / `GenericServlet`) and `.jsp` are directly called by the user of the web application.
Therefore the user should only see messages he understands and not exception of any kind. This means the servlets and jsp-files
should handle as much exceptions as they can. These handled exceptions will be wrapped in a `HttpServletException`. At the end these `HttpServletExceptions` will be catched, parsed and then displayed to the user, as a understandable message.

| Exception          | Occurrence                        |
| ------------------ | --------------------------------- |
| `IOException`      | If an error could not be written. |
| `RuntimeException` | In `.jsp`-Servlets unchecked exceptions must not be captured. |