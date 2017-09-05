# GreenDAO-Migrator

Database is a major part of any non-trivial Android application. Caching becomes a very important aspect of any application which follows the Offline First Architecture and as a developer we want the users to have a great experience even when they are not connected to the Internet.

Using an ORM like greenrobot's [GreenDAO](https://github.com/greenrobot/greenDAO) makes it super easy to persist data and saves us from the wrath of `Cursors`.

But `Schema Migrations` are not straight forward in GreenDAO or most of the ORMs for that matter.

If you use GreenDAO in your application and hate the migration process, you can surely get something out of this project.

This project eases the migration process and once all of the setup is completed the only think that we need to do is  change the DB schema version in the app level `build.gradle` and it just works.
```java
/**
 * Just increment the schemaVersion after changing the DB Schema
 */
greendao {
    schemaVersion 1
}
```

### How it works
[This answer](https://stackoverflow.com/questions/13373170/greendao-schema-update-and-data-migration/30334668#30334668) on StackOverflow provides a utility class which creates temporary tables to persist the data present in current tables before dropping all the tables and recreating them based on the new schema and restores the previous data making the migration process seamless.

The utility class accepts a database instance and a list of DAO classes.
```java
MigrationHelper.getInstance().migrate(db,
               List<? extends AbstractDao<?, ?>> daoClasses);
```

I created a method that would return a list of all the DAO classes but now I had a responsibility of adding the DAO class of any new table that I created and I have forgotten to do that on several occasions because I am a `HUMAN`.

Thus, I used annotation processing to create a class at compile time that generates a method that returns a list of all the DAO classes. The Annotation Processor basically scans through all the classes with the `@Entity` annotation and adds it to the list of DAO classes.

The generated class looks something like this

```java
public final class DaoHelper {
  /**
   * Returns a List of all the DAOs classes available */
  public static List<Class<? extends AbstractDao<?, ?>>> getAllDaos() {
  	List<Class<? extends AbstractDao<?, ?>>> daoClasses = new ArrayList();
    daoClasses.add(ExampleDao.class);
    return daoClasses;
  }
}
```

#### BONUS
GreenDAO generates the `DaoMaster.class` and `DaoSession.class` every time you build the project. The package where these files are generated is not defined and GreenDAO usually generates it in the same package as one of your classes annotated with `@Entity` (generally the one that the annotation processor picked up the last for processing).
This is quite irritating when you are working with team members and you need to change the `import` statements everytime you pull someone else's code.

Add this to you app level `build.gradle`
```java
greendao {
    daoPackage "package.name"
}
```

This enforces GreenDAO to generate the files in the specified package solving the above issue.

**P.S:** I know GreenDAO is going to be deprecated in the future in favor of [ObjectBox](https://github.com/greenrobot/ObjectBox) but ~~since it is still in `BETA`,~~ I think there are people out there who are still using GreenDAO in production apps (like me) and would benefit from this project.

## License
```
Copyright 2017 Cyril Pillai

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
