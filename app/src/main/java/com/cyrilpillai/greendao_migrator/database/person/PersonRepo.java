package com.cyrilpillai.greendao_migrator.database.person;


import com.cyrilpillai.greendao_migrator.dao.PersonDao;
import com.cyrilpillai.greendao_migrator.database.util.BaseRepo;

import java.util.List;

public class PersonRepo extends BaseRepo {

    private static PersonRepo instance = null;
    private PersonDao dao;

    private PersonRepo() {
        super();
        dao = daoSession.getPersonDao();
    }

    public static PersonRepo getInstance() {
        if (instance == null) {
            synchronized (BaseRepo.class) {
                if (instance == null) {
                    instance = new PersonRepo();
                }
            }
        }
        return instance;
    }

    /**
     * Save Person
     *
     * @param person Person Entity
     */
    public void savePerson(Person person) {
        dao.insert(person);
    }

    /**
     * Save People
     *
     * @param people List of Person Entity
     */
    public void savePeople(List<Person> people) {
        dao.insertInTx(people);
    }

    /**
     * Lists all Person Entities
     *
     * @return List of Person
     */
    public List<Person> listAllPeople() {
        return dao.queryBuilder()
                .list();
    }


    /**
     * Deletes all the People from the Person Table
     */
    public void deleteAllPeople() {
        dao.deleteInTx(listAllPeople());
    }
}
