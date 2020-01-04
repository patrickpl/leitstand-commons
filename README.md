# Leitstand Commons

Leitstand is mainly written in Java and [Microprofile](https://microprofile.io) compatible.
One of the goals of disaggregated access networks is to get rid of vendor lock-ins.
Leitstand banks on the microprofile in order to let users select the runtime environment, be it an application server or a cloud-native runtime environment.

The following Java APIs are used in Leitstand:

- Context and Dependency Injection (CDI)
- Java Persistence API (JPA)
- Java API for RESTFful Web Services (JAX-RS)
- JSON Binding (JSON-B)
- Java Bean Validation
- Java Transaction API (JTA)
- Java Database Connectivity (JDBC)


The _leitstand-commons_ project contains the Leitstand foundation classes and outlines [how to implement a Leitstand Java module](./leitstand-commons/README.md).
In addition, the _leitstand-etc_ project provides means to [load the module configuration](./leitstand-etc/README.md).

The _leitstand-test_ project contains base classes for [unit and integration testing](./leitstand-test/README.md) supplying an in-memory database and transaction management.

